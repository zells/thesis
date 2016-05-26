package org.rtens.cell.library.Prophet.Console;

import org.rtens.cell.*;

public class printReaction implements NativeReaction {
	
	@Override
	public void execute(Cell cell, Context context) {
		System.out.println("########## " + context.getMessage().getLast());
	}
	
}
