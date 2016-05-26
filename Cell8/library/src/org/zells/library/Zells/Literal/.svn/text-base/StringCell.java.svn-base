package org.zells.library.Zells.Literal;

import java.util.LinkedList;

import org.zells.*;

public class StringCell extends Cell {
	
	public StringCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected Deliverer getNextDeliverer(String name, LinkedList<Delivery> deliveryStack) {
		deliveryStack.getFirst().resolveNextReceiver();
		return getChild(name);
	}
	
	@Override
	public Cell getChild(String name) {
		
		Cell child = new Cell(this, name)
				.setStem(new Path("°", "Zells", "String"))
				.setActive(true);
		
		int i = 0;
		for (char c : name.toCharArray()) {
			child.addChild(new Cell(child, String.valueOf(i))
					.setActive(true)
					.setStem(new Path("°", "Zells", "Literal", "Character", String.valueOf(c))));
			i++;
		}
		
		return child;
	}
	
	public static String get(Path stringCell, Cell sender, Path role, DeliveryId eid) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; true; i++) {
			Result result = sender.send(new Mailing(new Path(stringCell, new Path(String.valueOf(i), "value")), new Path("°")),
					role,
					eid);
			if (!result.wasDelivered()) break;
			sb.append(result.deliveredTo.getLast());
		}
		return sb.toString();
	}
	
}
