package org.zells.gui.browser;

import java.awt.*;
import java.util.LinkedList;

import javax.swing.JComponent;

import org.zells.Path;
import org.zells.gui.*;

public class TreeView extends JComponent {
	
	/*** Children ***/
	public PeerView rootView;
	private LinkedList<TreeView> children;
	
	/*** Parents ***/
	private Browser browser;
	private HostView host;
	public TreeView parentTree;
	
	/*** State ***/
	TreeController controller;
	
	/*** Configuration ***/
	private Dimension gap = new Dimension(10, 20);
	
	public TreeView(Browser browser, HostView host, TreeView parent, String name) {
		this.browser = browser;
		this.host = host;
		this.controller = new TreeController(this);
		this.parentTree = parent;
		
		setLayout(new AbsoluteLayout());
		
		rootView = new PeerView(browser, this, name);
		add(rootView);
	}
	
	public HostView getHost() {
		return host;
	}
	
	public PeerView getPeerView(Path path, boolean createOnTheFly) {
		if (path.isEmpty()) return rootView;
		
		String name = path.removeFirst();
		
		return getChild(name, createOnTheFly).getPeerView(path, createOnTheFly);
	}
	
	public LinkedList<TreeView> getChildren() {
		if (children == null) {
			children = new LinkedList<TreeView>();
			
			if (rootView.getModel() != null)
				for (String child : rootView.getModel().getChildren())
					addChild(child);
		}
		return children;
	}
	
	public void refreshChildren() {
		if (rootView.getModel() == null) return;
		
		for (String child : rootView.getModel().getChildren()) {
			getChild(child, true);
		}
		
	}
	
	private TreeView getChild(String name, boolean createOnTheFly) {
		for (TreeView child : getChildren())
			if (child.rootView.getCellName().equals(name))
				return child;
		
		if (!createOnTheFly) return null;
		
		return addChild(name);
	}
	
	public TreeView addChild(String name) {
		TreeView child = new TreeView(browser, host, this, name);
		child.setVisible(controller.isExpanded());
		
		getChildren().add(child);
		add(child);
		
		if (rootView.getModel() != null)
			child.rootView.setModel(rootView.getModel().getChildModel(name));
		
		refresh();
		return child;
	}
	
	public void removeChild(TreeView tree) {
		remove(tree);
		getChildren().remove(tree);
		refresh();
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		
		rootView.setVisible(aFlag);
		
		if (controller.isExpanded())
			for (TreeView child : getChildren())
				child.setVisible(aFlag);
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (controller.isExpanded() && !getChildren().isEmpty()) {
			int widthSum = 0;
			int heighestChild = 0;
			
			for (TreeView child : children) {
				widthSum += child.getPreferredSize().width + gap.width;
				if (child.getPreferredSize().height > heighestChild)
					heighestChild = child.getPreferredSize().height;
			}
			widthSum -= gap.width;
			
			return new Dimension(widthSum, rootView.getPreferredSize().height + gap.height + heighestChild);
		} else {
			return rootView.getPreferredSize();
		}
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		if (controller.isExpanded()) {
			int xPos = 0;
			for (TreeView child : getChildren()) {
				child.setLocation(xPos, rootView.getPreferredSize().height + gap.height);
				xPos += child.getPreferredSize().width + gap.width;
			}
		}
		
		rootView.setLocation((getPreferredSize().width - rootView.getPreferredSize().width) / 2, 0);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawString((controller.isExpanded() ? "-" : "+"),
				rootView.getLocation().x,
				rootView.getLocation().y + rootView.getPreferredSize().height);
		
		if (controller.isExpanded()) {
			for (TreeView child : getChildren()) {
				g.drawLine(rootView.getLocation().x + rootView.getPreferredSize().width / 2,
						rootView.getLocation().y + rootView.getPreferredSize().height / 2,
						child.getLocation().x + child.rootView.getLocation().x
								+ child.rootView.getPreferredSize().width / 2,
						child.getLocation().y + child.rootView.getLocation().y
								+ child.rootView.getPreferredSize().height / 2);
			}
		}
		
		super.paint(g);
	}
	
	public void refresh() {
		host.invalidate();
		browser.revalidate();
		browser.repaint();
	}
	
	public void deselectAll() {
		rootView.deselect();
		if (children == null) return;
		for (TreeView child : children)
			child.deselectAll();
	}

	public void makeVisible() {
		if (parentTree != null) {
			parentTree.makeVisible();
			parentTree.controller.setExpanded(true);
		} else {
			host.controller.setExpanded(true);
		}
	}
	
}
