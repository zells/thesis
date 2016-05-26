package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;

public abstract class PathCell extends ListCell {
	
	Path path;
	
	public PathCell(Cell parent, String name) {
		super(parent, name);
		
		if (getCellPath() == null)
			path = new Path();
		else path = getCellPath();
	}
	
	protected abstract Path getCellPath();
	
	protected abstract void setCellPath(Path path);
	
	@Override
	public void add(Path stem, Context context) {
		// Later Read characters instead of presuming a literal string
		path.add(stem.getLast());
		setCellPath(path);
	}
	
	@Override
	public void remove(int index, Context contex) {
		path.remove(index);
	}
	
	@Override
	public void clear() {
		path.clear();
		setCellPath(path);
	}
	
	@Override
	protected int getSize() {
		return getCellPath().size();
	}
	
	@Override
	protected Cell getCellOf(int i) {
		return new Cell(this, String.valueOf(i))
				.setStem(new Path("°", "Prophet", "Literal", "String", getCellPath().get(i)));
	}
	
}
