package org.rtens.cell;

import java.io.Serializable;

public class Host implements Serializable {
	
	public String network;
	public String hostAddress;
	
	public Host(String network, String host) {
		this.network = network;
		this.hostAddress = host;
	}
	
	@Override
	public String toString() {
		return network + ":" + hostAddress;
	}
	
	public static Host parse(String host) throws Exception {
		try {
			String[] netAddr = host.split(":", 2);
			return new Host(netAddr[0], netAddr[1]);
		} catch (Exception e) {
			throw new Exception("Parsing error for host: " + host + ". Should be 'network:hostAddress'.");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Host && ((Host) o).network.equals(network)
				&& ((Host) o).hostAddress.equals(hostAddress);
	}
	
	@Override
	public int hashCode() {
		return hostAddress.hashCode();
	}
	
}
