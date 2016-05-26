package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;

public class ChildrenCell extends ListCell {
	
	public ChildrenCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	public void add(Path stem, Context context) {
		// Later read name instead of assuming literal string cell
		getCell().addChild(stem.getLast(), context);
	}
	
	@Override
	public void clear() {
		getCell().getChildren().clear();
	}
	
	@Override
	protected Cell getCellOf(int index) {
		return new Cell(this, String.valueOf(index))
				.setStem(new Path("°", "Prophet", "Literal", "String", getCell().getChildren().get(index).getName()));
	}
	
	@Override
	protected int getSize() {
		return getCell().getChildren().size();
	}
	
	@Override
	public void remove(int index, Context context) {
		getCell().removeChild(getCell().getChildren().get(index).getName(), context);
	}
	
}
