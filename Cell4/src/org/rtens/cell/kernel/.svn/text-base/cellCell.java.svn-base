package org.rtens.cell.kernel;

import java.util.LinkedList;

import org.rtens.cell.*;
import org.rtens.cell.kernel.cell.*;

public class cellCell extends KernelCell {
	
	private Cell stemCell = new StemCell(this, "Stem");
	private Cell reactionCell = new ReactionCell(this, "Reaction");

	public cellCell(Cell parent, String name) {
		super(parent, name);
		
		setReaction(new KernelReaction() {
			
			@Override
			public void execute(KernelCell kernelCell, Cell cell, Context context) {}
		});
	}
	
	@Override
	protected Cell getCell() {
		return getParent();
	}
	
	@Override
	protected LinkedList<Cell> loadChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>();
		
		children.add(new NameCell(this, "Name"));
		children.add(new PeersCell(this, "Peers"));
		children.add(new ActiveCell(this, "Active"));
		children.add(new ChildrenCell(this, "Children"));
		children.add(new setStemCell(this, "setStem"));
		
		return children;
	}
	
	@Override
	public LinkedList<Cell> getChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>(super.getChildren());
		
		if (getCell().getStem() != null)
			children.add(stemCell);
		
		if (getCell().getReaction() != null && getCell().getReaction() instanceof Reaction)
			children.add(reactionCell);
		
		return children;
	}
	
	@Override
	public Cell addChild(String name, Context context) {
		if (name.equals("Stem") && getCell().getStem() == null) {
			getCell().setStem(new Path());
		} else if (name.equals("Reaction") && getCell().getReaction() == null) {
			getCell().setReaction(new Reaction());
		} else {
			return super.addChild(name, context);
		}
		
		return null;
	}
	
	@Override
	public boolean removeChild(String name, Context context) {
		if (name.equals("Stem")) {
			getCell().setStem(null);
		} else if (name.equals("Reaction") && getCell().getReaction() != null
				&& getCell().getReaction() instanceof Reaction) {
			getCell().setReaction(null);
		}
		
		return super.removeChild(name, context);
	}
}
