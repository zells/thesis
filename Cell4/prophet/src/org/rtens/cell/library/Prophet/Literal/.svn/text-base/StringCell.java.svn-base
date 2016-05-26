package org.rtens.cell.library.Prophet.Literal;

import org.rtens.cell.*;

public class StringCell extends Cell {
	
	public StringCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected Peer getNextReceiver(Parameters p) {
		String name = p.receiverStack.getLast().removeFirst();
		p.resolvedStack.getLast().add(name);
		return getChild(name, p);
	}
	
	@Override
	public Cell getChild(String name, Parameters p) {
		
		Cell child = new Cell(this, name)
				.setStem(new Path("°", "Prophet", "String"))
				.setActive(true);
		
		int i = 0;
		for (char c : name.toCharArray()) {
			child.addChild(new Cell(this, String.valueOf(i))
					.setActive(true)
					.setStem(new Path("°", "Prophet", "Literal", "Character", String.valueOf(c))));
			i++;
		}
		
		return child;
	}
}
