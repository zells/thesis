package org.zells.kernel;

import java.util.*;

import org.zells.*;

public class cellCell extends KernelCell {

	private StemCell stemCell;
	private ReactionCell reactionCell;

	public cellCell(Cell parent) {
		super(parent, "cell");
		
		setReaction(new Reaction());
	}
	
	@Override
	public Cell getCell() {
		return getParent();
	}
	
	@Override
	protected List<Cell> loadChildren() {
		List<Cell> children = new LinkedList<Cell>();
		
		stemCell = new StemCell(this);
		reactionCell = new ReactionCell(this);

		children.add(new ChildrenCell(this));
		children.add(new createCell(this));
		children.add(new NameCell(this));
		children.add(new ActiveCell(this));
		children.add(new PeersCell(this));
		
		return children;
	}
	
	@Override
	public List<Cell> getChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>(super.getChildren());
		
		if (getCell().getStem() != null)
			children.add(stemCell);
		
		if (getCell().getReaction() != null && getCell().getReaction() instanceof Reaction)
			children.add(reactionCell);
		
		return children;
	}
	
	@Override
	public Cell addChild(String name) {
		if (name.equals("Stem") && getCell().getStem() == null) {
			getCell().setStem(new Path());
		} else if (name.equals("Reaction") && getCell().getReaction() == null) {
			getCell().setReaction(new Reaction());
		} else {
			return super.addChild(name);
		}
		
		return null;
	}
	
	@Override
	public Cell removeChild(String name) {
		if (name.equals("Stem") && getCell().getStem() != null) {
			getCell().setStem(null);
		} else if (name.equals("Reaction") && getCell().getReaction() != null
				&& getCell().getReaction() instanceof Reaction) {
			getCell().setReaction(null);
		}
		
		return super.removeChild(name);
	}

}
