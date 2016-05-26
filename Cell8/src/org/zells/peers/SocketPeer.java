package org.zells.peers;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

import org.zells.*;

public class SocketPeer extends Peer {
	
	public String hostname;
	public int port;
	
	public SocketPeer(Cell cell, Address address) {
		super(cell, address);
		
		String namePort[] = address.host.split(":", 2);
		hostname = namePort[0];
		port = Integer.parseInt(namePort[1]);
	}
	
	@Override
	public Result deliver(LinkedList<Delivery> deliveryStack, Path message, DeliveryId eid) {
		try {
			Socket s = new Socket(hostname, port);
			ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
			
			os.writeObject(cell.getPath());
			os.writeObject(deliveryStack);
			os.writeObject(message);
			os.writeObject(eid);
			os.flush();
			
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			
			Result result = (Result) is.readObject();
			
			s.close();
			
			return result;
		} catch (Exception e) {
			Result result = new Result();
			result.log.add(new Result.Log.Entry(eid, cell.getPath(), "Could not connect to peer: " + e));
			return result;
		}
	}
	
}
