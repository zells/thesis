package org.rtens.cell.library.Prophet.List;

import org.rtens.cell.*;

public class addReaction extends LibraryReaction {
	
	@Override
	public void execute(Cell cell, Context context) {
		int index = -1;
		Result r;
		do {
			index++;
			r = cell.send(new Send(new Path(context.getReceiver(), new Path("parent", String.valueOf(index), "cell")),
					new Path("°")), context);
		} while (r.wasDelivered());
		
		sendAndWait(new Path(context.getReceiver(), new Path("parent", "cell", "Children", "add")),
				new Path("°", "Prophet", "Literal", "String", String.valueOf(index)), cell, context);
		
		sendAndWait(new Path(context.getReceiver(),
						new Path("parent", String.valueOf(index), "cell", "cell", "Children", "add")),
				new Path("°", "Prophet", "Literal", "String", "Stem"), cell, context);
		sendAndWait(new Path(context.getReceiver(),
				new Path("parent", String.valueOf(index), "cell", "Stem", "clear")), new Path("°"), cell, context);
		
		for (String name : context.getMessage())
			sendAndWait(new Path(context.getReceiver(),
					new Path("parent", String.valueOf(index), "cell", "Stem", "add")),
					new Path("°", "Prophet", "Literal", "String", name), cell, context);
	}
	
}
