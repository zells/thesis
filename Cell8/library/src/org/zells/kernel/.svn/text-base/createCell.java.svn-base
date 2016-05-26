package org.zells.kernel;

import org.zells.*;

public class createCell extends KernelCell {
	
	public createCell(Cell parent) {
		super(parent, "create");
	}
	
	@Override
	public Cell getChild(String name) {
		return new createChildCell(this, name);
	}
	
	private class createChildCell extends KernelCell {
		
		public createChildCell(createCell parent, String name) {
			super(parent, name);
			
			setReaction(new createReaction());
		}
		
	}
	
	private class createReaction extends KernelReaction {
		
		@Override
		public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
			getCell().addChild(new Cell(getCell(), receiver.getName()).setStem(message));
		}
		
	}
	
}
