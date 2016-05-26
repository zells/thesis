package org.rtens.cell.kernel.cell;

import java.util.LinkedList;

import org.rtens.cell.*;
import org.rtens.cell.kernel.KernelCell;

public class PeersCell extends KernelCell {
	
	public PeersCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	public LinkedList<Cell> getChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>();
		for (Peer p : getCell().getPeers()) {
			String name = p.getAddress(null).host.toString();
			children.add(new Cell(this, name)
					.setStem(new Path("°", "Cell"))
					.setActive(true));
		}
		
		return children;
	}
	
	@Override
	public Cell addChild(String name, Context contex) {
		try {
			getCell().addPeer(getConnections().getPeer(new Address(Host.parse(name), getCell().getPath())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public boolean removeChild(String name, Context context) {
		try {
			return getCell().removePeer(new Address(Host.parse(name), getCell().getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Cell getChild(String name, Parameters p) {
		if (name.equals(getCell().getAddress(p).host.toString())) {
			return getCell();
		}
		return null;
	}
}
