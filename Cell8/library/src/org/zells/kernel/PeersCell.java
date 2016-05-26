package org.zells.kernel;

import java.util.*;

import org.zells.*;

public class PeersCell extends KernelCell {
	
	public PeersCell(Cell parent) {
		super(parent, "Peers");
	}
	
	@Override
	public List<Cell> getChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>();
		for (Peer p : getCell().getPeers()) {
			String name = p.getAddress().toString();
			children.add(new Cell(this, name)
					.setStem(new Path("°", "Cell"))
					.setActive(true));
		}
		
		return children;
	}
	
	@Override
	protected Deliverer getNextDeliverer(String name, LinkedList<Delivery> deliveryStack) {
		if (name.equals("local:0")) {
			deliveryStack.getFirst().resolveNextReceiver();
			return getCell();
		}
		
		for (Peer p : getCell().getPeers()) {
			if (p.getAddress().toString().equals(name)) {
				deliveryStack.getFirst().resolveNextReceiver();
				return p;
			}
		}
		return super.getNextDeliverer(name, deliveryStack);
	}
	
	@Override
	public Cell addChild(String name) {
		try {
			getCell().addPeer(Peer.create(getCell(), Address.parse(name)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Cell removeChild(String name) {
		try {
			getCell().removePeer(Address.parse(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
//	
//	@Override
//	public boolean removeChild(String name, Context context) {
//		try {
//			return getCell().removePeer(new Address(Host.parse(name), getCell().getPath()));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//	
//	@Override
//	public Cell getChild(String name, Parameters p) {
//		if (name.equals(getCell().getAddress(p).host.toString())) {
//			return getCell();
//		}
//		return null;
//	}
}
