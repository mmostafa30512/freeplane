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
package org.freeplane.features.controller.print;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.ui.MenuBuilder;

class PrintAction extends AbstractPrintAction {
	final private boolean isDlg;

	PrintAction(final PrintController controller, final boolean isDlg) {
		super(controller, null, new ImageIcon(Controller.getResourceController().getResource("/images/fileprint.png")));
		MenuBuilder.setLabelAndMnemonic(this, isDlg ? Controller.getText("print_dialog")
		        : Controller.getText("print"));
		this.isDlg = isDlg;
	}

	public void actionPerformed(final ActionEvent e) {
		final PrintController controller = getPrintController();
		if (!controller.acquirePrinterJobAndPageFormat()) {
			return;
		}
		controller.getPrinterJob().setPrintable(Controller.getController().getMapView(),
		    controller.getPageFormat());
		if (!isDlg || controller.getPrinterJob().printDialog()) {
			try {
				controller.getPrinterJob().print();
			}
			catch (final Exception ex) {
				org.freeplane.core.util.Tools.logException(ex);
			}
		}
	}
}
