package org.rtens.cell;

public abstract class Client extends Peer {
	
	protected Address address;
	protected Cell root;
	
	public Client(Cell root, Address address) {
		this.root = root;
		this.address = address;
	}
	
	@Override
	public Address getAddress(Parameters p) {
		return address;
	}
	
}
