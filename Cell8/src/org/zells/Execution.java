package org.zells;

import java.util.LinkedList;

import org.zells.glue.CellLoader;

public class Execution extends Cell {
	
	private Path message;
	
	public Execution(Cell parent, String name, Path message) {
		super(parent, name);
		this.message = message;
		setStem(new Path("parent"));
		setActive(true);
	}
	
	@Override
	public void setLoader(CellLoader loader) {
		// DONT SAVE EXECUTIONS
	}
	
	public static String makeName(DeliveryId eid) {
		return "#" + eid;
	}
	
	@Override
	protected Deliverer getNextDeliverer(String name, LinkedList<Delivery> deliveryStack) {
		if (name.equals("message")) {
			deliveryStack.getFirst().receiver.removeFirst();
			deliveryStack.getFirst().receiver.addAll(0, message);
			return this;
		} else {
			return super.getNextDeliverer(name, deliveryStack);
		}
	}
	
	public Path getMessage() {
		return message;
	}
	
}
