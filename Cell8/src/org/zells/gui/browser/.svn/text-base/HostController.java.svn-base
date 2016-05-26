package org.zells.gui.browser;

public class HostController {

	private HostView view;
	
	private boolean expanded = false;

	public HostController(HostView view) {
		this.view = view;
	}

	public void toggleExpansion() {
		setExpanded(!expanded);
		view.refresh();
	}

	public void setExpanded(boolean to) {
		expanded = to;
		view.tree.setVisible(to);
	}
	
	public boolean isExpanded() {
		return expanded;
	}

}
