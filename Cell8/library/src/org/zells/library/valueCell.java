package org.zells.library;

import java.util.LinkedList;

import org.zells.*;

public class valueCell extends Cell {
	
	public valueCell(Cell parent) {
		super(parent, "value");
	}
	
	@Override
	public Result deliver(LinkedList<Delivery> deliveryStack, Path message, DeliveryId eid) {
		if (deliveryStack.getLast().receiver.isEmpty())
			return new Result().deliveredTo(getParent().getPath(), eid);
		return super.deliver(deliveryStack, message, eid);
	}
	
}
