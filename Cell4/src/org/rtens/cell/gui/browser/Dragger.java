package org.rtens.cell.gui.browser;

import java.awt.Point;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import org.rtens.cell.*;
import org.rtens.cell.connection.LocalClient;
import org.rtens.cell.gui.Browser;

/**
 * Responsible for dragging a {@link PeerView} across the browser.
 */
public class Dragger implements MouseMotionListener, MouseListener {
	
	private PeerView view;
	private PeerView dragging;
	private Point grabbed;
	private Browser browser;
	
	public Dragger(Browser b, PeerView view, Point grabbed) {
		this.view = view;
		this.grabbed = grabbed;
		this.browser = b;
		
		dragging = new PeerView(browser, view.getAddress(), null, view.getCell());
		dragging.setTransparent(true);
		dragging.setVisible(false);
		
		browser.add(dragging, Browser.dragLayer);
		
		view.addMouseMotionListener(this);
		view.addMouseListener(this);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Point trans = new Point(e.getX() - grabbed.x, e.getY() - grabbed.y);
		dragging.setLocation(
				view.getLocationOnScreen().x - browser.getLocationOnScreen().x + browser.getLocation().x + trans.x,
				view.getLocationOnScreen().y - browser.getLocationOnScreen().y + browser.getLocation().y + trans.y);
		
		dragging.setVisible(true);
		
		dragging.revalidate();
		dragging.getRootPane().repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		HostView inside = getHostContaining(dragging.getLocation());
		if (inside != null && inside != view.getHost()) {
			new DragNDropAction(view, inside).getMenu().show(view, e.getX(), e.getY());
		}
		
		view.removeMouseMotionListener(this);
		view.removeMouseListener(this);
		
		browser.remove(dragging);
		browser.revalidate();
		browser.repaint();
	}
	
	private HostView getHostContaining(Point p) {
		for (HostView h : browser.getHosts()) {
			if (p.x > h.getLocation().x
					&& p.y > h.getLocation().y
					&& p.x < h.getLocation().x + h.getPreferredSize().width
					&& p.y < h.getLocation().y + h.getPreferredSize().height)
				return h;
		}
		return null;
	}
	
	private class DragNDropAction {
		
		private JPopupMenu menu;
		private PeerView peerView;
		private HostView hostView;
		
		public DragNDropAction(PeerView peer, HostView host) {
			this.peerView = peer;
			this.hostView = host;
		}
		
		protected void createNewPeer() {
			PeerView to = createPeersOnPath(peerView.getAddress());
			
			Address targetAddress = to.getAddress();
			if (targetAddress.host.equals(LocalClient.host)) {
				String network = peerView.getTree().getHost().getHost().network;
				targetAddress = new Address(Connections.getServer(network, browser.getRoot()).getHost(),
						to.getAddress());
			}
			peerView.getCell().addPeer(targetAddress);
			
			peerView.revalidate();
			peerView.getRootPane().repaint();
		}
		
		protected void copy() {
			copy(peerView);
		}
		
		private void copy(PeerView p) {
			PeerView created = createPeersOnPath(p.getAddress());
			
			created.getCell().setReaction(p.getCell().getReaction());
			created.getCell().setStem(p.getCell().getStem());
			created.getCell().setActive(p.getCell().isActive());
			created.updateTooltip();
		}
		
		private PeerView createPeersOnPath(Path path) {
			path = new Path(path);
			
			Address address = new Address(hostView.getHost());
			address.add(path.removeFirst());
			
			PeerView view = null;
			while (!path.isEmpty()) {
				view = hostView.getPeerView(address, true);
				String name = path.removeFirst();
				
				if (!view.getCell().isConnected())
					throw new RuntimeException("Could not create cell '" + name + "'. Parent is not connected: "
							+ view.getAddress());
				
				view.getCell().setActive(true);
				
				if (!view.getCell().hasChild(name)) {
					view.getCell().addChild(name);
				}
				
				address.add(name);
			}
			return hostView.getPeerView(address, true);
		}
		
		protected void copyTree() {
			copyTree(peerView);
		}
		
		private void copyTree(PeerView p) {
			copy(p);
			
			for (TreeView child : p.getTree().getChildren()) {
				copyTree(child.getRoot());
			}
		}
		
		protected void move() {
			moveAll(peerView);
		}
		
		private void moveAll(PeerView p) {
			copy(p);
			
			for (TreeView child : new LinkedList<TreeView>(p.getTree().getChildren())) {
				moveAll(child.getRoot());
			}
			
			p.delete();
		}
		
		public JPopupMenu getMenu() {
			if (menu == null) {
				menu = new JPopupMenu();
				
				menu.add(new AbstractAction("Copy cell") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						copy();
					}
				});
				menu.add(new AbstractAction("Copy cell and children") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						copyTree();
					}
				});
				menu.add(new AbstractAction("Move cell and children") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						move();
					}
				});
				menu.add(new AbstractAction("New peer") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						createNewPeer();
					}
				});
			}
			return menu;
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	
	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public void mousePressed(MouseEvent arg0) {}
}
