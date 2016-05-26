package org.zells.kernel;

import org.zells.*;

public class StemCell extends PathCell {

	public StemCell(Cell parent) {
		super(parent, "Stem");
	}
	
	@Override
	protected Path getCellPath() {
		return getCell().getStem();
	}
	
	@Override
	protected void setCellPath(Path path) {
		getCell().setStem(path);
	}

}
