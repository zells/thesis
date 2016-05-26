package org.zells.gui.browser;

public class TreeController {
	
	private TreeView view;
	
	private boolean expanded;
	
	public TreeController(TreeView view) {
		this.view = view;
	}
	
	public void toggleExpansion() {
		setExpanded(!expanded);
	}
	
	public void toggleExpansionWholeTree() {
		setExpandedWholeTree(!expanded);
	}
	
	public void setExpandedWholeTree(boolean to) {
		for (TreeView child : view.getChildren())
			child.controller.setExpandedWholeTree(to);
		
		setExpanded(to);
	}

	public void setExpanded(boolean to) {
		if (expanded == to) return;
		
		expanded = to;
		for (TreeView child : view.getChildren())
			child.setVisible(to);
		
		view.refresh();
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
}
