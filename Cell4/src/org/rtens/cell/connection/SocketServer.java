package org.rtens.cell.connection;

import java.io.*;
import java.net.*;
import java.util.*;

import org.rtens.cell.*;

public class SocketServer extends Server {
	
	private InetSocketAddress myAddress;
	
	private ServerSocket socket;
	
	public static final String network = "Socket";
	
	public SocketServer(Cell root) {
		super(root);
	}
	
	public SocketServer(Cell root, InetSocketAddress myAddress) {
		this(root);
		this.myAddress = myAddress;
	}
	
	@Override
	public Host getHost() {
		return new Host(network, SocketClient.formatAddress(myAddress));
	}
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(myAddress.getPort());
			
			log("Server started on " + socket);
			
			while (true) {
				new Dispatcher(socket.accept()).start();
			}
		} catch (Exception e) {
			if (e.getMessage() == null || !e.getMessage().equals("socket closed"))
				e.printStackTrace();
			else log("Socket closed.");
		}
	}
	
	@Override
	public List<String> getParameterNames() {
		return Arrays.asList("Hostname", "Port");
	}
	
	@Override
	public void setParameters(Map<String, String> params) throws Exception {
		if (params.get("Hostname").isEmpty())
			throw new Exception("Hostname must not be empty.");
		
		try {
			int port = Integer.parseInt(params.get("Port"));
			myAddress = new InetSocketAddress(params.get("Hostname"), port);
		} catch (Exception e) {
			throw new Exception("Port has to be an integer.", e);
		}
	}
	
	@Override
	public void close() {
		if (socket == null) {
			log("Server can't be stopped because it's not running.");
			return;
		}
		
		try {
			socket.close();
			socket = null;
			log("Server stopped.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class Dispatcher extends Thread {
		
		private Socket client;
		
		public Dispatcher(Socket socket) {
			log("New incoming connection: " + socket);
			this.client = socket;
		}
		
		@Override
		public void run() {
			
			Result res;
			ObjectInputStream is;
			try {
				is = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
				
				Path origPath = (Path) is.readObject();
				char method = is.readChar();
				Parameters parameters = (Parameters) is.readObject();
				parameters.address = new Address(network, SocketClient.formatAddress(myAddress), parameters.address);
				
				Path path = new Path(origPath);
				path.removeFirst();
				
				Parameters params = new Parameters(parameters);
				
				Cell deliverer = root;
				while (deliverer != null && !path.isEmpty()) {
					params.resolvedStack.getLast().clear();
					params.resolvedStack.getLast().addAll(deliverer.getPath());
					
					deliverer = deliverer.getChild(path.removeFirst(), params);
				}
				
				if (deliverer == null)
					throw new Exception("Cell not found: " + origPath + " (on host " + myAddress + ")");
				
				switch (method) {
				case Server.DELIVER:
					res = deliverer.deliver(parameters);
					break;
				case Server.DELIVER_TO_CHILD:
					res = deliverer.deliverToChild(parameters);
					break;
				case Server.DELIVER_TO_STEM:
					res = deliverer.deliverToStem(parameters);
					break;
				default:
					throw new Exception("Unknown method identifier: " + method);
				}
				
				log("Message delivered to " + deliverer.getPath() + ": " + parameters);
			} catch (Exception e) {
				e.printStackTrace();
				res = new Result();
				res.log.add(new Log.Entry(root.getAddress(null), "Server could not deliver: " + e.getMessage()));
			}
			
			try {
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
				
				os.writeObject(res);
				os.flush();
				
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
				log("Error: " + e.getMessage());
			}
			
		}
	}

	@Override
	public String getNetwork() {
		return network;
	}
	
}
