package org.zells.kernel;

import java.util.LinkedList;

import org.zells.*;

public abstract class ListCell extends KernelCell {
	
	public ListCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected LinkedList<Cell> loadChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>();
		
		children.add(new KernelCell(this, "clear").setReaction(new ListChildReaction(this) {
			
			@Override
			public void perform(Cell receiver, Path role, Path message, DeliveryId eid) {
				listCell.clear();
			}
		}));
		children.add(new KernelCell(this, "add").setReaction(new ListChildReaction(this) {
			
			@Override
			public void perform(Cell receiver, Path role, Path message, DeliveryId eid) {
				listCell.add(receiver, role, message, eid);
			}
		}));
		children.add(new KernelCell(this, "remove").setReaction(new ListChildReaction(this) {
			
			@Override
			public void perform(Cell receiver, Path role, Path message, DeliveryId eid) {
				listCell.remove(receiver, role, message, eid);
			}
		}));
		
		return children;
	}
	
	@Override
	public LinkedList<Cell> getChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>(super.getChildren());
		
		for (int i = 0; i < getSize(); i++) {
			children.add(getCellOf(i).setActive(true));
		}
		
		return children;
	}
	
	protected abstract Cell getCellOf(int index);
	
	protected abstract int getSize();
	
	public abstract void clear();
	
	public abstract void add(Cell receiver, Path role, Path message, DeliveryId eid);
	
	public abstract void remove(Cell receiver, Path role, Path message, DeliveryId eid);
	
}
