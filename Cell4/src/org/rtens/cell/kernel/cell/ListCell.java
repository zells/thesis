package org.rtens.cell.kernel.cell;

import java.util.LinkedList;

import org.rtens.cell.*;
import org.rtens.cell.kernel.KernelCell;
import org.rtens.cell.kernel.cell.List.*;

public abstract class ListCell extends KernelCell {
	
	public ListCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected LinkedList<Cell> loadChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>();
		
		children.add(new clearCell(this, "clear"));
		children.add(new addCell(this, "add"));
		children.add(new removeCell(this, "remove"));
		
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
	
	public abstract void add(Path stem, Context context);
	
	public abstract void remove(int index, Context context);
	
	public void remove(Path stem, Context context) {
		for (int i = 0; i < getSize(); i++) {
			if (getCellOf(i).getStem().equals(stem)) {
				remove(i, context);
				return;
			}
		}
	}
	
}
