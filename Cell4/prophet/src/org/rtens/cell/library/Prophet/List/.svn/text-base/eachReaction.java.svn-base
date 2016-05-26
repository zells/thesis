package org.rtens.cell.library.Prophet.List;

import org.rtens.cell.*;

public class eachReaction extends LibraryReaction {
	
	@Override
	public void execute(Cell cell, Context context) {
		int index = -1;
		Result r;
		do {
			index++;
			Path item = new Path(context.getReceiver(), new Path("parent", String.valueOf(index)));
			r = cell.send(new Send(new Path(item, new Path("cell")), new Path("°")), context);
			if (r.wasDelivered()) {
				sendAndWait(context.getMessage(), item, cell, context);
			}
		} while (r.wasDelivered());
	}
	
}
