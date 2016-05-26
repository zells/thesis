package org.zells.gui;

import java.util.*;

import javax.swing.JLayeredPane;

import org.zells.*;
import org.zells.gui.browser.*;

public class Browser extends JLayeredPane {
	
	public static final Integer dragLayer = new Integer(400);
	public static final Integer connectionLayer = new Integer(2);
	public static final Integer hostLayer = new Integer(1);
	
	/*** Children ***/
	private Map<Address, HostView> hosts = new HashMap<Address, HostView>();
	
	public Browser(PeerModel rootModel) {
		
		setLayout(new AbsoluteLayout());
		
		getHostView(Server.localAddress, true)
						.getPeerView(rootModel.getPath(), true)
						.setModel(rootModel);

		getHostView(Server.localAddress, false).controller.setExpanded(true);
	}
	
	public HostView getHostView(Address addr, boolean create) {
		if (hosts.containsKey(addr))
			return hosts.get(addr);
		
		if (!create) return null;
		
		HostView host = new HostView(this, addr);
		
		HostView mostRight = null;
		for (HostView hv : hosts.values()) {
			if (mostRight == null || hv.getLocation().x > mostRight.getLocation().x)
				mostRight = hv;
		}
		
		if (mostRight != null)
			host.setLocation(mostRight.getLocation().x + mostRight.getPreferredSize().width + 20,
					mostRight.getLocation().y);
		
		hosts.put(addr, host);
		add(host, hostLayer);
		
		return host;
	}
	
	public void addConnection(Connection c) {
		c.setBrowser(this);
		add(c, connectionLayer);
	}

	public void deselectAll() {
		for (HostView host : hosts.values()) {
			host.tree.deselectAll();
		}
	}
	
}
