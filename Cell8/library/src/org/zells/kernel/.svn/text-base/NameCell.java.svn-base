package org.zells.kernel;

import org.zells.*;
import org.zells.library.Zells.Literal.StringCell;

public class NameCell extends KernelCell {
	
	public NameCell(cellCell parent) {
		super(parent, "Name");
	}
	
	@Override
	public Path getStem() {
		return new Path("°", "Zells", "Literal", "String", getCell().getName());
	}
	
	@Override
	public Cell setStem(Path stem) {
		String name = StringCell.get(stem, getRoot(), getRoot().getPath(), new DeliveryId());
		if (!name.isEmpty())
			getCell().setName(name);
		return super.setStem(stem);
	}
	
}
