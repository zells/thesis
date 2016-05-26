package org.rtens.cell.test;

import org.rtens.cell.*;
import org.rtens.cell.test.TestContext.MockReaction;
import org.rtens.cell.test.TestReceive.MockCell;

public class TestList extends LibraryTest {
	
	public void testAdd() {
		Cell a = root.addChild(new MockCell(root, "A").setStem(new Path("°", "Prophet", "List")));
		
		send("°.A.add", "°.B");
		send("°.A.add", "°.C.stem");
		
		assertEquals(new Path("°", "B"), a.getChild("0").getStem());
		assertEquals(new Path("°", "C", "stem"), a.getChild("1").getStem());
		
		send("°.A.add", "°.B");
		assertEquals(new Path("°", "B"), a.getChild("2").getStem());
	}
	
	public void testEach() {
		MockCell a = (MockCell) root.addChild(new MockCell(root, "A").setStem(new Path("°", "Prophet", "List")));
		
		MockCell b = (MockCell) root.addChild(new MockCell(root, "B").withReaction());
		MockCell c = (MockCell) root.addChild(new MockCell(root, "C").withReaction());
		
		a.addChild("0").setStem(new Path("°", "B"));
		a.addChild("1").setStem(new Path("°", "C"));
		a.addChild("2").setStem(new Path("°", "B"));
		
		MockReaction echoReaction = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("message.stem", "°", cell, context);
				super.execute(cell, context);
			}
		};
		root.addChild(new MockCell(root, "echo").setReaction(echoReaction));
		
		send("°.A.each", "°.echo");
		
		assertEquals(2, b.reaction.executed);
		assertEquals(1, c.reaction.executed);
	}
	
}
