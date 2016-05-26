package org.rtens.cell.kernel.cell;

import java.util.LinkedList;

import org.rtens.cell.*;
import org.rtens.cell.kernel.*;

public class ActiveCell extends KernelCell {
	
	public ActiveCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected LinkedList<Cell> loadChildren() {
		LinkedList<Cell> cs = new LinkedList<Cell>();
		
		cs.add(new KernelCell(this, "setTrue")
				.setReaction(new KernelReaction() {
					
					@Override
					public void execute(KernelCell kernelCell, Cell cell, Context context) {
						getCell().setActive(true);
					}
				}));
		
		cs.add(new KernelCell(this, "setFalse")
				.setReaction(new KernelReaction() {
					
					@Override
					public void execute(KernelCell kernelCell, Cell cell, Context context) {
						getCell().setActive(false);
					}
				}));
		
		return cs;
	}
	
	@Override
	public Path getStem() {
		return new Path("°", "Prophet", getCell().isActive() ? "True" : "False");
	}
	
	@Override
	public Cell setStem(Path stem) {
		
		// Later Check boolean value instead of expecting literal cell
		if (stem.equals(new Path("°", "Prophet", "True")))
			getCell().setActive(true);
		else if (stem.equals(new Path("°", "Prophet", "False")))
			getCell().setActive(false);
		
		return super.setStem(stem);
	}
}
