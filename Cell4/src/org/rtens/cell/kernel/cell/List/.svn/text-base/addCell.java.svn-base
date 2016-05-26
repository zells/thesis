package org.rtens.cell.kernel.cell.List;

import org.rtens.cell.*;
import org.rtens.cell.kernel.KernelCell;
import org.rtens.cell.kernel.cell.ListCell;

public class addCell extends KernelCell {
	
	private ListCell listCell;
	
	public addCell(ListCell parent, String name) {
		super(parent, name);
		this.listCell = parent;
		
		setReaction(new NativeReaction() {
			
			@Override
			public void execute(Cell cell, Context context) {
				if (adoptIfNotOwn(context)) {
					listCell.adopted(context);
					return;
				}
				listCell.add(context.getMessage(), context);
			}
		});
	}
	
}
