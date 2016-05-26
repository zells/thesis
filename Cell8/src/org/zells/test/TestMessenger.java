package org.zells.test;

import org.zells.*;

public class TestMessenger extends CellTest {
	
	public void testReaction() throws InterruptedException {
		Reaction r = new Reaction();
		r.mailings.add(new Mailing(new Path("°", "A"), new Path("MA")));
		r.mailings.add(new Mailing(new Path("°", "A", "B"), new Path("MB")));
		
		MockCell root = new MockCell(null, "°");
		MockCell a = root.addChild("A").withReaction();
		
		r.execute(new Execution(root, "#0", new Path("°")), new Path("°", "0"), new Path(), new DeliveryId(0));
		
		Thread.sleep(100L);
		assertEquals(1, a.reaction.executed);
		
		MockCell b = a.addChild("B").withReaction();
		
		Thread.sleep(100L);
		assertEquals(1, b.reaction.executed);
	}
	
	// TODO Test pause and resume
}
