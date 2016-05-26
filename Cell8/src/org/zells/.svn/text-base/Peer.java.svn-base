	package org.zells;

import org.zells.peers.SocketPeer;

	public abstract class Peer implements Deliverer {
		
		protected Address address;
		protected Cell cell;
		
		public Peer(Cell cell, Address address) {
			this.cell = cell;
			this.address = address;
		}
		
		public Address getAddress() {
			return address;
		}
		
		public static Peer create(Cell cell, Address address) {
			if (address.network.equals("Socket")) {
				return new SocketPeer(cell, address);
			} else {
				throw new RuntimeException("No peer for network " + address.network + " found.");
			}
		}
		
	}
