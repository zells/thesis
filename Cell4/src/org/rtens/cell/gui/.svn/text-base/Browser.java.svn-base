package org.rtens.cell.gui;

import java.awt.Graphics;
import java.util.*;

import javax.swing.*;

import org.rtens.cell.*;
import org.rtens.cell.gui.browser.*;

/**
 * Displays a distributed cells system graphically.
 * 
 * Each {@link HostView host} is displayed as a rectangle with its address and contains the
 * {@link TreeView cell tree} starting with the root. A displayed {@link PeerView cell} might be
 * accessible or just a representation. Connections between peers are visualized as well.
 */
public class Browser extends JLayeredPane implements ClickedPeerListener {
	
	public static final Integer dragLayer = 3;
	public static final Integer connectionLayer = 2;
	public static final Integer hostLayer = 1;
	
	private Map<Host, HostView> hosts = new HashMap<Host, HostView>();
	
	private Cell root;
	
	private JPanel connectionPane = new JPanel(new AbsoluteLayout());
	public LinkedList<ClickedPeerListener> clickedPeerListeners = new LinkedList<ClickedPeerListener>();
	
	public Browser(Cell root) {
		setLayout(new AbsoluteLayout());
		
		connectionPane.setOpaque(false);
		add(connectionPane, connectionLayer);
		
		this.root = root;
		
		HostView localHost = getHostView(root.getAddress(null));
		for (Server s : Connections.getServers(root)) {
			if (s.isRunning()) {
				hosts.put(s.getHost(), localHost);
				localHost.addHost(s.getHost());
			}
		}
		
		getPeerView(root.getAddress(null), false);
	}
	
	@Override
	public void clickedOn(PeerView p) {
		for (ClickedPeerListener l : new LinkedList<ClickedPeerListener>(clickedPeerListeners))
			l.clickedOn(p);
	}
	
	public Cell getRoot() {
		return root;
	}
	
	public Set<HostView> getHosts() {
		return new HashSet<HostView>(hosts.values());
	}
	
	/**
	 * Returns component representing the cell with the given address.
	 * 
	 * If the component does not exist it will be created.
	 */
	public PeerView getPeerView(Address address, boolean makeVisible) {
		return getHostView(address).getPeerView(address, makeVisible);
	}
	
	/**
	 * Returns the view of the host corresponding to given address.
	 */
	private HostView getHostView(Address address) {
		if (hosts.containsKey(address.host))
			return hosts.get(address.host);
		
		HostView host = new HostView(this, address.host);
		host.clickedPeerListeners.add(this);
		
		HostView mostRight = null;
		for (HostView hv : hosts.values()) {
			if (mostRight == null || hv.getLocation().x > mostRight.getLocation().x)
				mostRight = hv;
		}
		
		if (mostRight != null)
			host.setLocation(mostRight.getLocation().x + mostRight.getPreferredSize().width + 20,
					mostRight.getLocation().y);
		
		hosts.put(address.host, host);
		add(host, hostLayer);
		
		revalidate();
		repaint();
		
		return host;
	}
	
	/**
	 * Clears selection of all cells
	 */
	public void clearSelections() {
		for (HostView host : hosts.values())
			host.getTree().clearSelections();
	}
	
	/**
	 * Returns a CellConnector that provides access cell properties
	 */
	public CellConnector getCellConnector(Address address) {
		return new CellConnector(root, address);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		for (HostView host : getHosts())
			paintPeerConnections(g, host.getTree());
	}
	
	private void paintPeerConnections(Graphics g, TreeView tree) {
		if (tree.getRoot().getCell().isConnected()) {
			for (Address addr : tree.getRoot().getCell().getPeers()) {
				Connection.paint(g, this, tree.getRoot(), getPeerView(addr, false));
			}
		}
		
		if (tree.isExpanded()) {
			for (TreeView child : tree.getChildren())
				paintPeerConnections(g, child);
		}
	}
	
}
