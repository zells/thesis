package org.zells;

import java.util.*;

import javax.swing.event.ChangeListener;

import org.zells.peers.SocketServer;

public abstract class Server implements Runnable {
	
	protected Cell root;
	private Thread thread;
	
	public Logger logger;
	
	public LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();
	
	public static final Address localAddress = new Address("local", "0");
	
	private static List<Server> servers;
	
	public Server(Cell root) {
		this.root = root;
	}
	
	public static Server create(String network, Cell root) {
		if (network.equals("Socket")) {
			return new SocketServer(root);
		} else {
			throw new RuntimeException("No server for network " + network + " found.");
		}
	}
	
	public static List<Server> getServers(Cell root) {
		if (servers == null) {
			servers = new LinkedList<Server>();
			servers.add(new SocketServer(root));
		}
		
		return servers;
	}
	
	public static Server getServer(String network, Cell root) {
		for (Server s : getServers(root))
			if (s.getAddress().network.equals(network))
				return s;
		
		return null;
	}
	
	protected void log(String message) {
		if (logger != null)
			logger.log(this, message);
	}
	
	public void start(Map<String, String> params) {
		if (thread != null && thread.isAlive()) {
			log("Can't start server: Is already running.");
		} else {
			try {
				setParameters(params);
				
				thread = new Thread(this);
				thread.start();
				
				for (ChangeListener l : listeners)
					l.stateChanged(null);
			} catch (Exception e) {
				log("Can't start server: " + e.getMessage());
			}
		}
	}
	
	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}
	
	public void stop() {
		close();
		thread = null;
		
		for (ChangeListener l : listeners)
			l.stateChanged(null);
	}
	
	public abstract void run();
	
	protected abstract void close();
	
	protected abstract void setParameters(Map<String, String> params) throws Exception;
	
	public abstract Map<String, String> getParameters();
	
	public abstract Address getAddress();
	
	public interface Logger {
		public void log(Server server, String message);
	}
	
}
