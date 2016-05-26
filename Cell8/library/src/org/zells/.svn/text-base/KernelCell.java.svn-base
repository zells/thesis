package org.zells;


public class KernelCell extends Cell {

	public KernelCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected void receive(Path message, DeliveryId eid, Delivery delivery) {
		if (delivery.role.equals(getPath()))
			getReaction().execute(this, delivery.role, message, eid);
		
		delivery.role.add("##");
	}
	
	public Cell getCell() {
		return ((KernelCell) getParent()).getCell();
	}

}
