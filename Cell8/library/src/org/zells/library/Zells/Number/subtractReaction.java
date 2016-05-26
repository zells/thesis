package org.zells.library.Zells.Number;

import org.zells.*;

public class subtractReaction extends Reaction {

	@Override
	protected Path getResponse(int a, int b) {
		return new Path("°", "Zells", "Literal", "Number", String.valueOf(a - b));
	}
	
}
