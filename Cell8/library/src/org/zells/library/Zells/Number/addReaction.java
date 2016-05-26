package org.zells.library.Zells.Number;

import org.zells.*;

public class addReaction extends Reaction {

	@Override
	protected Path getResponse(int a, int b) {
		return new Path("°", "Zells", "Literal", "Number", String.valueOf(a + b));
	}
	
}
