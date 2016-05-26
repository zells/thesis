package org.zells.kernel;

import org.zells.*;

public class ReactionCell extends ListCell {
	
	public ReactionCell(Cell parent) {
		super(parent, "Reaction");
	}
	
	private Reaction getCellReaction() {
		return (Reaction) getCell().getReaction();
	}
	
	@Override
	public void add(Cell receiver, Path role, Path message, DeliveryId eid) {
		Reaction r = getCellReaction();
		r.mailings.add(new Mailing(new Path(), new Path()));
		
		getCell().setReaction(r);
	}
	
	@Override
	public void clear() {
		getCell().setReaction(new Reaction());
	}
	
	@Override
	protected Cell getCellOf(int index) {
		return new SendCell(this, String.valueOf(index), getCellReaction().mailings.get(index));
	}
	
	@Override
	protected int getSize() {
		if (!(getCell().getReaction() instanceof Reaction) || getCellReaction() == null) return 0;
		
		return getCellReaction().mailings.size();
	}
	
	@Override
	public void remove(Cell receiver, Path role, Path message, DeliveryId eid) {
	// Only full update are possible
	}
	
}
