/**
 * author: Marcel Genzmehr
 * 26.01.2012
 */
package org.freeplane.plugin.workspace.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.freeplane.plugin.workspace.model.node.AWorkspaceTreeNode;

/**
 * 
 */
public class IOController {
	
	private final HashMap<Class<? extends AWorkspaceTreeNode>, HashMap<Integer, List<IWorkspaceNodeActionListener>>> listenerMap = new HashMap<Class<? extends AWorkspaceTreeNode>, HashMap<Integer, List<IWorkspaceNodeActionListener>>>();
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/

	/***********************************************************************************
	 * METHODS
	 * @param eventType 
	 * @param node 
	 **********************************************************************************/
	public List<IWorkspaceNodeActionListener> getNodeActionListeners(Class<? extends AWorkspaceTreeNode> clazz, Integer eventType) {
		HashMap<Integer, List<IWorkspaceNodeActionListener>> levelOne = listenerMap.get(clazz);
		if(levelOne != null) {		
			return levelOne.get(eventType);	
		}
		return null;
	}
	
	
	public void registerNodeActionListener(Class<? extends AWorkspaceTreeNode> clazz, Integer eventType, IWorkspaceNodeActionListener listener) {
		HashMap<Integer, List<IWorkspaceNodeActionListener>> levelOne = listenerMap.get(clazz);
		if(levelOne == null) {		
			HashMap<Integer, List<IWorkspaceNodeActionListener>> levelTwo = new HashMap<Integer, List<IWorkspaceNodeActionListener>>();
			Vector<IWorkspaceNodeActionListener> vec = new Vector<IWorkspaceNodeActionListener>();
			vec.add(listener);
			levelTwo.put(eventType, vec);
			listenerMap.put(clazz, levelTwo);		
		}
		else {
			List<IWorkspaceNodeActionListener> listeners = levelOne.get(eventType);
			if(!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
		
	}
	
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
