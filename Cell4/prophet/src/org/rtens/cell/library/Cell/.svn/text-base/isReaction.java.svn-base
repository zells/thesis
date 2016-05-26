package org.rtens.cell.library.Cell;

import java.util.LinkedList;

import org.rtens.cell.*;

public class isReaction extends LibraryReaction {
	
	@Override
	public void execute(Cell cell, Context context) {
		
		Path replyStem = new Path("°", "Prophet", "False");
		Path expected = sendAndWait(new Path("message", "stem", "cell"), new Path(), cell, context).resolvedReceiver;
		expected.removeLast();
		
		LinkedList<Path> tested = new LinkedList<Path>();
		Path test = new Path(context.getReceiver());
		test.removeLast();
		
		while (!tested.contains(test)) {
			
			if (test.equals(expected)) {
				replyStem = new Path("°", "Prophet", "True");
				break;
			}
			
			tested.add(test);
			test = sendAndWait(new Path(test, new Path("stem", "cell")), new Path(), cell, context).resolvedReceiver;
			test.removeLast();
		}
		
		sendAndWait(new Path("message", "cell", "Children", "add"),
				new Path("°", "Prophet", "Literal", "String", "reply"), cell, context);
		sendAndWait(new Path("message", "reply", "cell", "setStem"), replyStem, cell, context);
		sendAndWait(new Path("message", "reply", "cell", "Active", "setTrue"), new Path("°"), cell, context);
	}
	
}
