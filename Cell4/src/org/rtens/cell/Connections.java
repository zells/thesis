package org.rtens.cell;

import java.util.*;

import org.rtens.cell.connection.*;

/**
 * Factory for {@link Peer Peers} and {@link Server Servers}
 */
public class Connections {
	
	private Cell root;
	private static List<Server> servers;
	
	public Connections(Cell root) {
		this.root = root;
	}
	
	public Peer getPeer(Address address) throws Exception {
		if (address.host.equals(LocalClient.host)) {
			return new LocalClient(root, address);
		} else if (address.host.network.equals(SocketServer.network)) {
			return new SocketClient(root, address);
		}
		throw new Exception("No peer found for " + address);
	}
	
	public static List<Server> getServers(Cell root) {
		if (servers == null) {
			servers = new LinkedList<Server>();
			servers.add(new SocketServer(root));
		}
		
		return servers;
	}
	
	public static Server getServer(String name, Cell root) {
		for (Server s : getServers(root))
			if (s.getNetwork().equals(name))
				return s;
		
		return null;
	}
}
