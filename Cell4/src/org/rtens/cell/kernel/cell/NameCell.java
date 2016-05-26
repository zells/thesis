package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;
import org.rtens.cell.kernel.KernelCell;

public class NameCell extends KernelCell {
	
	public NameCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	public Path getStem() {
		return new Path("°", "Prophet", "Literal", "String", getCell().getName());
	}
	
	@Override
	public Cell setStem(Path stem) {
		if (stem.containsAll(new Path("°", "Prophet", "Literal", "String")) && stem.size() == 5)
			getCell().setName(stem.getLast());
		
		return super.setStem(stem);
	}
	
}
