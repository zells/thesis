package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;
import org.rtens.cell.kernel.*;

public class setStemCell extends KernelCell {
	
	public setStemCell(Cell parent, String name) {
		super(parent, name);
		
		setReaction(new KernelReaction() {
			
			@Override
			public void execute(KernelCell kernelCell, Cell cell, Context context) {
				Result r = sendAndWait(new Send(new Path(context.getMessage(), new Path("cell")), new Path("°")),
						context);
				
				Path resolved = new Path(r.getResolvedReceiver());
				resolved.removeLast();
				cell.setStem(resolved);
			}
		});
	}
}
