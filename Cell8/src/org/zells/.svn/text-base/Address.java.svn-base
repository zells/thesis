package org.zells;

import java.io.Serializable;

public class Address implements Serializable {
	
	public String network;
	public String host;
	
	public Address(String network, String host) {
		this.network = network;
		this.host = host;
	}
	
	@Override
	public String toString() {
		return network + ":" + host;
	}
	
	@Override
	public int hashCode() {
		return host.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Address && ((Address) o).network.equals(network)
				&& ((Address) o).host.equals(host);
	}

	public static Address parse(String addressString) throws Exception {
		String[] netHost = addressString.split(":", 2);
		return new Address(netHost[0], netHost[1]);
	}
	
}
