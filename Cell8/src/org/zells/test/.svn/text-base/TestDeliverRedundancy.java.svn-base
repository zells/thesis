package org.zells.test;

public class TestDeliverRedundancy extends DeliveryTest {
	
	/**
	 * <pre>
	 * A
	 * B*  
	 * C*
	 * 
	 * -> A.B -> C
	 * </pre>
	 */
	public void testChild() {
		MockCell a = root
				.addChild("A");
		a
				.addChild("B").setReaction(new MockReaction(m("C", "°")))
				.addChild("C").withReaction();
		
		send(p("A.B"));
		
		assertTrue(a.delivered < 3);
	}
	
	/**
	 * <pre>
	 * A
	 * B*
	 * C*
	 * 
	 * -> A.B.C -> parent.parent
	 * </pre>
	 */
	public void testParent() {
		MockCell a = root
				.addChild("A");
		MockCell b = a
				.addChild("B").withReaction();
		b
				.addChild("C").setReaction(new MockReaction(m("parent.parent", "°")));
		
		send(p("A.B.C"));
		
		assertEquals(1, b.reaction.executed);
		assertEquals(1, a.delivered);
	}
	
}
