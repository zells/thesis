package org.rtens.cell.kernel.cell.List;

import org.rtens.cell.*;
import org.rtens.cell.kernel.KernelCell;
import org.rtens.cell.kernel.cell.ListCell;

public class removeCell extends KernelCell {
	
	private ListCell listCell;
	
	public removeCell(ListCell parent, String name) {
		super(parent, name);
		this.listCell = parent;
		
		setActive(true);
		
		setReaction(new NativeReaction() {
			
			@Override
			public void execute(Cell cell, Context context) {
				if (adoptIfNotOwn(context)) {
					listCell.adopted(context);
					return;
				}
				listCell.remove(context.getMessage(), context);
			}
		});
	}
	
}
