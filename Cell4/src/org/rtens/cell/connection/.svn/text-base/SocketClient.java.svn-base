package org.rtens.cell.connection;

import java.io.*;
import java.net.*;
import java.util.HashSet;

import org.rtens.cell.*;
import org.rtens.cell.Parameters.SearchEntry;

public class SocketClient extends Client {
	
	private InetSocketAddress socketAddress;
	
	public SocketClient(Cell root, Address address) {
		super(root, address);
		
		String[] hostNamePort = address.host.hostAddress.split(":");
		
		this.socketAddress = new InetSocketAddress(hostNamePort[0], Integer.parseInt(hostNamePort[1]));
	}
	
	public Server getServer() {
		return Connections.getServer("Socket", root);
	}
	
	public static String formatAddress(InetSocketAddress addr) {
		return addr.getHostName() + ":" + addr.getPort();
	}
	
	// TODO Should be in Client class
	private HashSet<SearchEntry> switchAddresses(HashSet<SearchEntry> searched, Host replace, Host with) {
		
		HashSet<SearchEntry> result = new HashSet<SearchEntry>();
		
		for (SearchEntry se : searched) {
			if (se.address.host.equals(replace)) {
				result.add(new SearchEntry(new Address(with, se.address), se.path));
			} else {
				result.add(se);
			}
		}
		
		return result;
	}
	
	private Result deliverToServer(char method, Parameters p) {
		Socket s;
		
		p = new Parameters(p);
		if (getServer().isRunning())
			p.searched = switchAddresses(p.searched, LocalClient.host, getServer().getHost());
		
		try {
			s = new Socket(socketAddress.getAddress(), socketAddress.getPort());
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
			
			os.writeObject(new Path(address));
			os.writeChar(method);
			os.writeObject(p);
			os.flush();
			
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			
			Result r = (Result) is.readObject();
			
			s.close();
			
			return r;
		} catch (Exception e) {
			Result res = new Result();
			
			if (getServer().isRunning())
				res.searched = switchAddresses(res.searched, getServer().getHost(), LocalClient.host);
			
			res.log.add(new Log.Entry(getAddress(null), "Could not connect to peer: " + e));
			return res;
		}
	}
	
	@Override
	public Result deliver(Parameters p) {
		return deliverToServer(Server.DELIVER, p);
	}
	
	@Override
	public Result deliverToChild(Parameters p) {
		return deliverToServer(Server.DELIVER_TO_CHILD, p);
	}
	
	@Override
	public Result deliverToStem(Parameters p) {
		return deliverToServer(Server.DELIVER_TO_STEM, p);
	}
	
}
