package org.zells.kernel;

import org.zells.*;
import org.zells.library.Zells.Literal.StringCell;

public class ChildrenCell extends ListCell {
	
	public ChildrenCell(Cell parent) {
		super(parent, "Children");
	}
	
	@Override
	public void add(Cell receiver, Path role, Path message, DeliveryId eid) {
		if (message.subPath(-1).equals(new Path("°", "Zells", "Literal", "String")))
			getCell().addChild(message.getLast());
		else getCell().addChild(StringCell.get(message, receiver, role, eid));
	}
	
	@Override
	public void clear() {
	//		getCell().getChildren().clear();
	}
	
	@Override
	protected Cell getCellOf(int index) {
		return new Cell(this, String.valueOf(index))
						.setStem(new Path("°", "Zells", "Literal", "String", getCell().getChildren().get(index)
								.getName()));
	}
	
	@Override
	protected int getSize() {
		return getCell().getChildren().size();
	}
	
	@Override
	public void remove(Cell receiver, Path role, Path message, DeliveryId eid) {
		getCell().removeChild(StringCell.get(message, receiver, role, eid));
	}
	
}
