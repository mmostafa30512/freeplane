/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.help;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.freeplane.core.resources.ResourceBundles;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.MenuBuilder;
import org.freeplane.core.ui.components.FreeplaneMenuBar;
import org.freeplane.core.util.HtmlUtils;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.MenuUtils;
import org.freeplane.core.util.MenuUtils.MenuEntry;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.map.MapController;
import org.freeplane.features.map.MapModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.ModeController;
import org.freeplane.features.mode.browsemode.BModeController;
import org.freeplane.features.mode.mindmapmode.MModeController;

class DocumentationAction extends AFreeplaneAction {
	private static final long serialVersionUID = 1L;
	private final String document;

	DocumentationAction( final String actionName, final String document) {
		super(actionName);
		this.document = document;
	}

	public void actionPerformed(final ActionEvent e) {
		final ResourceController resourceController = ResourceController.getResourceController();
		final File baseDir = new File(resourceController.getResourceBaseDir()).getAbsoluteFile().getParentFile();
		final File file;
		final int extPosition = document.lastIndexOf('.');
		if (extPosition != -1) {
			final String languageCode = ((ResourceBundles) resourceController.getResources()).getLanguageCode();
			final String map = document.substring(0, extPosition) + "_" + languageCode
			        + document.substring(extPosition);
			final File localFile = new File(baseDir, map);
			if (localFile.canRead()) {
				file = localFile;
			}
			else {
				file = new File(baseDir, document);
			}
		}
		else {
			file = new File(baseDir, document);
		}
		try {
			final URL endUrl = file.toURL();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (endUrl.getFile().endsWith(".mm")) {
							 Controller.getCurrentController().selectMode(BModeController.MODENAME);
							if (Controller.getCurrentModeController().getMapController().newMap(endUrl, false)) {
								appendAcceleratableMenuEntries();
							}
						}
						else {
							Controller.getCurrentController().getViewController().openDocument(endUrl);
						}
					}
					catch (final Exception e1) {
						LogUtils.severe(e1);
					}
				}
			});
		}
		catch (final MalformedURLException e1) {
			LogUtils.warn(e1);
		}
	}

	@Override
	public void afterMapChange(final Object newMap) {
	}

	private void appendAcceleratableMenuEntries() {
		// use the MModeController for the mindmap mode menu if possible - the browse menu doesn't have much entries!
		final ModeController modeController = ResourceController.getResourceController().isApplet() ? Controller
		    .getCurrentModeController() : MModeController.getMModeController();
		final MenuBuilder menuBuilder = modeController.getUserInputListenerFactory().getMenuBuilder();
		final DefaultMutableTreeNode menuEntryTree = MenuUtils.createAcceleratebleMenuEntryTree(
		    FreeplaneMenuBar.MENU_BAR_PREFIX, menuBuilder);
		final MapController mapController = Controller.getCurrentModeController().getMapController();
		final NodeModel rootNode = mapController.getRootNode();
		final NodeModel newNode = mapController.newNode(TextUtils.getText("hot_keys"), rootNode.getMap());
		newNode.setFolded(true);
		newNode.setLeft(true);
		final int HOT_KEYS_INDEX = 2;
		mapController.insertNodeIntoWithoutUndo(newNode, rootNode, HOT_KEYS_INDEX);
		insertAcceleratorHtmlTable(newNode, menuEntryTree);
		MenuUtils.insertAsNodeModelRecursively(newNode, menuEntryTree.children(), mapController);
	}

	@SuppressWarnings("unchecked")
	private void insertAcceleratorHtmlTable(final NodeModel newNode, final DefaultMutableTreeNode menuEntryTree) {
		final MapController mapController = Controller.getCurrentModeController().getMapController();
		final String title = TextUtils.getText("hot_keys_table");
		final MapModel map = mapController.getRootNode().getMap();
		final NodeModel titleNode = mapController.newNode(title, map);
		titleNode.setFolded(true);
		newNode.insert(titleNode);
		final NodeModel tableNode = mapController.newNode(DocumentationAction.formatAsHtml(menuEntryTree.children(),
		    title), map);
		titleNode.insert(tableNode);
	}

	// ==========================================================================
	//                 format accelerator map as html text
	// ==========================================================================
	private static String formatAsHtml(final Enumeration<DefaultMutableTreeNode> children, final String title) {
		final StringBuilder builder = new StringBuilder();
		builder.append("<html><head><style type=\"text/css\">" //
		        //doesn't work: + "  table { margin: 1px 0px; border-spacing: 0px; }"//
		        + "  h1 { background-color: #B5C8DB; margin-bottom: 0px; margin-top: 1ex; }"//
		        + "  h2 { background-color: #B5C8DB; margin-bottom: 0px; margin-top: 1ex; }"//
		        + "  h3 { background-color: #B5C8DB; margin-bottom: 0px; margin-top: 1ex; }"//
		        + "</head><body>");
		DocumentationAction.appendAsHtml(builder, children, title, 2);
		builder.append("</body></html>");
		return builder.toString();
	}

	private static void appendAsHtml(final StringBuilder builder, final Enumeration<DefaultMutableTreeNode> children,
	                                 final String title, final int level) {
		builder.append("<h").append(level).append('>').append(title).append("</h1>");
		DocumentationAction.appendChildrenAsHtml(builder, children, title, level);
	}

	@SuppressWarnings("unchecked")
	private static void appendChildrenAsHtml(final StringBuilder builder,
	                                         final Enumeration<DefaultMutableTreeNode> children, final String title,
	                                         final int level) {
		final ArrayList<MenuEntry> menuEntries = new ArrayList<MenuEntry>();
		final ArrayList<DefaultMutableTreeNode> submenus = new ArrayList<DefaultMutableTreeNode>();
		// sort and divide
		while (children.hasMoreElements()) {
			final DefaultMutableTreeNode node = children.nextElement();
			if (node.isLeaf()) {
				menuEntries.add((MenuEntry) node.getUserObject());
			}
			else {
				submenus.add(node);
			}
		}
		// actions
		if (!menuEntries.isEmpty()) {
			builder.append("<table cellspacing=\"0\" cellpadding=\"0\">");
			for (final MenuEntry entry : menuEntries) {
				final String keystroke = entry.getKeyStroke() == null ? "" //
				        : MenuUtils.formatKeyStroke(entry.getKeyStroke());
				builder.append(DocumentationAction.el("tr", DocumentationAction.el("td", entry.getLabel() + "&#xa0;")
				        + DocumentationAction.el("td", keystroke)
				        + DocumentationAction.el("td", entry.getToolTipText())));
			}
			builder.append("</table>");
		}
		// submenus
		for (final DefaultMutableTreeNode node : submenus) {
			final String subtitle = (level > 2 ? title + "&#8594;" : "") + String.valueOf(node.getUserObject());
			DocumentationAction.appendAsHtml(builder, node.children(), subtitle, level + 1);
		}
	}

	private static String el(final String name, final String content) {
		return HtmlUtils.element(name, content);
	}
}