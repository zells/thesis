package org.zells.library.Zells.Literal;

import org.zells.*;
import org.zells.library.valueCell;

public class CharacterCell extends Cell {
	
	public CharacterCell(Cell parent, String name) {
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
		if (name.length() != 1)
			return null;
		
		Cell c = new Cell(this, name)
				.setActive(true)
				.setStem(new Path("°", "Zells", "Character"));
		
		c.addChild(new valueCell(c));
		
		return c;
	}
}
