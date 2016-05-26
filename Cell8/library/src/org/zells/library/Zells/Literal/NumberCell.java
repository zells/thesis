package org.zells.library.Zells.Literal;

import org.zells.*;
import org.zells.library.valueCell;

public class NumberCell extends Cell {
	
	public NumberCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected Deliverer getNextDeliverer(String name, java.util.LinkedList<Delivery> deliveryStack) {
		Cell c = getChild(name);
		if (c == null) return null;
		
		deliveryStack.getFirst().resolveNextReceiver();
		return c;
	};
	
	@Override
	public Cell getChild(String name) {
		try {
			Integer.parseInt(name);
		} catch (Exception e) {
			return null;
		}
		
		Cell c = new Cell(this, name)
				.setActive(true)
				.setStem(new Path("°", "Zells", "Number"));
		

		c.addChild(new valueCell(c));
		
		return c;
	}
}
