package org.zells.kernel;

import org.zells.*;
import org.zells.library.Zells.Literal.StringCell;

public abstract class PathCell extends ListCell {
	
	private Path path;
	
	public PathCell(Cell parent, String name) {
		super(parent, name);
		
		if (getCellPath() == null)
			path = new Path();
		else path = getCellPath();
	}
	
	protected abstract Path getCellPath();
	
	protected abstract void setCellPath(Path path);
	
	@Override
	public void add(Cell receiver, Path role, Path message, DeliveryId eid) {
		path.add(StringCell.get(message, receiver, role, eid));
		setCellPath(path);
	}
	
	@Override
	public void clear() {
		path.clear();
		setCellPath(path);
	}
	
	@Override
	protected Cell getCellOf(int index) {
		return new Cell(this, String.valueOf(index))
				.setStem(new Path("°", "Zells", "Literal", "String", getCellPath().get(index)));
	}
	
	@Override
	protected int getSize() {
		return getCellPath().size();
	}
	
	@Override
	public void remove(Cell receiver, Path role, Path message, DeliveryId eid) {
		// TODO Auto-generated method stub
		
	}
	
}
