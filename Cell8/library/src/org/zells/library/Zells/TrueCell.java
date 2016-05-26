package org.zells.library.Zells;

import java.util.List;

import org.zells.*;
import org.zells.library.valueCell;

public class TrueCell extends Cell {

	public TrueCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected List<Cell> loadChildren() {
		List<Cell> children = super.loadChildren();
		children.add(new valueCell(this));
		return children;
	}

}
