package org.rtens.cell.gui.browser;

import java.awt.*;
import java.util.LinkedList;

import javax.swing.JComponent;

import org.rtens.cell.*;
import org.rtens.cell.gui.*;

public class TreeView extends JComponent implements ClickedPeerListener {
	
	private PeerView rootView;
	private HostView host;
	
	private TreeView parent;
	private LinkedList<TreeView> children;
	
	private boolean expanded;
	
	private Dimension gap = new Dimension(10, 20);
	private Browser browser;
	
	public LinkedList<ClickedPeerListener> clickedPeerListeners = new LinkedList<ClickedPeerListener>();
	
	public TreeView(Browser b, HostView host, Address address) {
		setLayout(new AbsoluteLayout());
		
		this.browser = b;
		this.host = host;
		this.rootView = new PeerView(browser, address, this);
		
		rootView.clickedPeerListeners.add(this);
		
		add(rootView);
	}
	
	@Override
	public void clickedOn(PeerView p) {
		for (ClickedPeerListener l : clickedPeerListeners)
			l.clickedOn(p);
	}
	
	public TreeView getParentTree() {
		return parent;
	}
	
	public HostView getHost() {
		return host;
	}
	
	/**
	 * Returns view of peer corresponding with path relative to root.
	 * 
	 * View is created if not existing.
	 * 
	 * @param makeVisible
	 */
	public PeerView getPeerView(Path path, boolean makeVisible) {
		if (path.isEmpty()) return rootView;
		
		String name = path.removeFirst();
		
		if (getChild(name) == null)
			addChild(name);
		
		if (makeVisible && !isExpanded())
			expand();
		
		return getChild(name).getPeerView(path, makeVisible);
	}
	
	public void clearSelections() {
		rootView.unselect();
		
		if (children != null) {
			for (TreeView child : getChildren())
				child.clearSelections();
		}
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	public void collapse() {
		if (expanded) {
			expanded = false;
			
			for (TreeView child : getChildren())
				remove(child);
			
			revalidate();
			browser.repaint();
		}
	}
	
	public void expand() {
		if (!expanded) {
			expanded = true;
			
			for (TreeView child : getChildren())
				add(child);
			
			revalidate();
			browser.repaint();
		}
	}
	
	public void expandWholeTree() {
		expand();
		
		for (TreeView child : getChildren())
			child.expandWholeTree();
	}
	
	public void collapseWholeTree() {
		collapse();
		
		for (TreeView child : getChildren())
			child.collapseWholeTree();
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		if (expanded) {
			int xPos = 0;
			for (TreeView child : getChildren()) {
				child.setLocation(xPos, rootView.getPreferredSize().height + gap.height);
				xPos += child.getPreferredSize().width + gap.width;
			}
		}
		
		rootView.setLocation((getPreferredSize().width - rootView.getPreferredSize().width) / 2, 0);
	}
	
	public LinkedList<TreeView> getChildren() {
		if (children == null) {
			children = new LinkedList<TreeView>();
			
			if (rootView.getCell().isConnected() && rootView.getCell().getChildren() != null) {
				for (String child : rootView.getCell().getChildren()) {
					addChild(child);
				}
			}
		}
		
		return children;
	}
	
	public TreeView addChild(String name) {
		
		Address childAddr = new Address(rootView.getAddress());
		childAddr.add(name);
		
		TreeView childTree = new TreeView(browser, host, childAddr);
		childTree.clickedPeerListeners.add(this);
		
		childTree.parent = this;
		
		if (children != null)
			getChildren().add(childTree);
		
		if (isExpanded()) {
			add(childTree);
			
			revalidate();
			browser.repaint();
		}
		
		return childTree;
	}
	
	public void removeChild(TreeView childTree) {
		children.remove(childTree);
		remove(childTree);
		
		revalidate();
		browser.repaint();
	}
	
	public TreeView getChild(String name) {
		for (TreeView child : getChildren())
			if (child.rootView.getAddress().getLast().equals(name))
				return child;
		
		return null;
	}
	
	public PeerView getRoot() {
		return rootView;
	}
	
	@Override
	public String toString() {
		return "t:" + rootView;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (isExpanded() && !getChildren().isEmpty()) {
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
	public void paint(Graphics g) {
		if (isExpanded()) {
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
	
}
