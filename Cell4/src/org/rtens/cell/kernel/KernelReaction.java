package org.rtens.cell.kernel;

import org.rtens.cell.*;

public abstract class KernelReaction implements NativeReaction {
	
	@Override
	public void execute(Cell cell, Context context) {
		execute((KernelCell) cell, ((KernelCell) cell).getCell(), context);
	}
	
	public abstract void execute(KernelCell kernelCell, Cell cell, Context context);
	
}
