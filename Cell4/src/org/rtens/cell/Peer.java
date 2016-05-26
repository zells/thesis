package org.rtens.cell;

public abstract class Peer {
	
	public String getName() {
		return getAddress(null).getLast();
	}
	
	public abstract Address getAddress(Parameters p);
	
	public abstract Result deliver(Parameters p);
	
	public abstract Result deliverToStem(Parameters p);
	
	public abstract Result deliverToChild(Parameters p);
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Peer && ((Peer) o).getAddress(null).equals(getAddress(null));
	}
	
	@Override
	public int hashCode() {
		return getAddress(null).hashCode();
	}
}
