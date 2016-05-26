package org.zells.test;

import org.zells.*;

public class TestDeliverBasic extends DeliveryTest {
	
	/**
	 * Root sends a message to itself.
	 */
	public void testDirectly() {
		root.withReaction();
		
		send(new Path(), new Path("My", "Message"));
		
		assertEquals(1, root.reaction.executed);
		Cell x = root.reaction.receiver;
		assertEquals(root, x);
		assertEquals(new Path("My", "Message"), root.reaction.message);
	}
	
	/**
	 * Root sends to "self"
	 */
	public void testSelf() {
		root.withReaction();
		
		send(new Path("self"), new Path());
		
		assertEquals(1, root.reaction.executed);
	}
	
	/**
	 * °.A.B sends message to °
	 */
	public void testRoot() {
		root.withReaction();
		MockCell b = root.addChild("A").addChild("B");
		
		send(b, new Path("°"), new Path());
		
		assertEquals(1, root.reaction.executed);
	}
	
	/**
	 * Root sends to A.B.C
	 * 
	 * <pre>
	 * A
	 * B
	 * C*
	 * </pre>
	 */
	public void testChildren() {
		MockCell c = root
				.addChild("A")
				.addChild("B")
				.addChild("C").withReaction();
		
		send(new Path("A", "B", "C"), new Path());
		
		assertEquals(1, c.reaction.executed);
	}
	
	/**
	 * °.A.B sends to "parent"
	 */
	public void testParent() {
		MockCell a = root.addChild("A").withReaction();
		MockCell b = a.addChild("B");
		
		send(b, new Path("parent"), new Path());
		
		assertEquals(1, a.reaction.executed);
	}
	
	/**
	 * <pre>
	 * A*    C*
	 * B*:C  D*
	 * D*
	 * 
	 * -> A.B.stem => C 
	 * -> A.B.stem.D => C.D 
	 * -> A.B.stem.parent => A
	 * </pre>
	 */
	public void testStem() {
		MockCell a = root.addChild("A").withReaction();
		MockCell ab = a.addChild("B").setStem(new Path("°", "C")).withReaction();
		MockCell abd = ab.addChild("D").withReaction();
		
		MockCell c = root.addChild("C").withReaction();
		MockCell cd = c.addChild("D").withReaction();
		
		send(new Path("A", "B", "stem"), new Path());
		assertEquals(1, c.reaction.executed);
		assertEquals(new Path("°", "A", "B"), c.reaction.role.subPath(-1));
		
		send(new Path("A", "B", "stem", "D"), new Path());
		assertEquals(0, abd.reaction.executed);
		assertEquals(1, cd.reaction.executed);
		assertEquals(new Path("°", "A", "B", "D"), cd.reaction.role.subPath(-1));
		
		send(new Path("A", "B", "stem", "parent"), new Path());
		assertEquals(1, a.reaction.executed);
		assertEquals(new Path("°", "A"), a.reaction.role.subPath(-1));
	}
	
	// TODO Test for activation
	
}
