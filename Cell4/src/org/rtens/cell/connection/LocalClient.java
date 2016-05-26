package org.rtens.cell.connection;

import org.rtens.cell.*;

public class LocalClient extends Client {
	
	public static Host host = new Host("local", "0");
	
	public LocalClient(Cell root, Address address) {
		super(root, address);
	}
	
	@Override
	public Result deliver(Parameters p) {
		return new LocalServer(root).deliver(address, p);
	}
	
	@Override
	public Result deliverToChild(Parameters p) {
		return new LocalServer(root).deliverToChild(address, p);
	}
	
	@Override
	public Result deliverToStem(Parameters p) {
		return new LocalServer(root).deliverToStem(address, p);
	}
	
}
