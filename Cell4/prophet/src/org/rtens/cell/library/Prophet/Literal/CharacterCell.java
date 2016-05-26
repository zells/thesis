package org.rtens.cell.library.Prophet.Literal;

import org.rtens.cell.*;

public class CharacterCell extends Cell {
	
	public CharacterCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected Peer getNextReceiver(Parameters p) {
		String name = p.receiverStack.getLast().getFirst();
		if (name.length() != 1)
			return null;
		
		p.resolvedStack.getLast().add(p.receiverStack.getLast().removeFirst());
		
		return new Cell(this, name)
				.setActive(true)
				.setStem(new Path("°", "Prophet", "Character"));
	}
	
}
