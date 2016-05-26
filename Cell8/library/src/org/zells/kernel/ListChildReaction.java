package org.zells.kernel;

import org.zells.*;

public abstract class ListChildReaction extends KernelReaction {
	
	protected ListCell listCell;
	
	public ListChildReaction(ListCell listCell) {
		this.listCell = listCell;
	}
	
	@Override
	public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
		perform(receiver, role, message, eid);
	}
	
	public abstract void perform(Cell receiver, Path role, Path message, DeliveryId eid);
	
}
