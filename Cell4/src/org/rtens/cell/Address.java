package org.rtens.cell;

public class Address extends Path {
	
	public Host host;
	
	public Address(Host host, Path... paths) {
		this.host = host;
		
		for (Path path : paths)
			addAll(path);
	}
	
	public Address(Address addr) {
		this(addr.host);
		
		addAll(addr);
	}
	
	public Address(String network, String hostAddress, Path... paths) {
		this(new Host(network, hostAddress), paths);
	}
	
	@Override
	public String toString() {
		return "[" + host + "]" + super.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o)
				&& o instanceof Address
				&& host.equals(((Address) o).host);
	}
	
	@Override
	public int hashCode() {
		return host.hashCode();
	}
	
}
