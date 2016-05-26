package org.zells.test;

import org.zells.Path;

/**
 * Tests which require a management of the search to avoid redundancy and searching in circles
 */
public class TestDeliverManaged extends DeliveryTest {
	
	/**
	 * A:B B:C C:A
	 */
	public void testCircularInheritance() {
		MockCell a = root.addChild("A").setStem(new Path("°", "B"));
		MockCell b = root.addChild("B").setStem(new Path("°", "C"));
		MockCell c = root.addChild("C").setStem(new Path("°", "A"));

		assertDelivery = false;
		send(p("A"));
		
		assertTrue(4 > a.delivered);
		assertTrue(3 > b.delivered);
		assertTrue(3 > c.delivered);
	}
	
	/**
	 * <pre>
	 * 0 A(1)   1 A(2)   2 A(0)
	 * </pre>
	 */
	public void testCircularPeers() {
		MockCell a0 = root.addChild("A");
		MockCell a1 = new MockCell(null, "1").addChild("A");
		MockCell a2 = new MockCell(null, "2").addChild("A");
		
		a0.addPeer(new MockPeer(a1));
		a1.addPeer(new MockPeer(a2));
		a2.addPeer(new MockPeer(a0));

		assertDelivery = false;
		send(p("A"));
	}
	
	public void testSameChildrenTwice() {
		assertDelivery = false;
		
		send(p("A"));
		MockCell a = root.addChild("A").withReaction();
		send(p("A"));
		assertEquals(1, a.reaction.executed);
		
		send(p("A.B"));
		MockCell b = a.addChild("B").withReaction();
		send(p("A.B"));
		assertEquals(1, b.reaction.executed);
	}
	

	/**
	 * Simulate the repeated sending of a messenger which always uses the same ID. Maybe it shouldn't
	 * 
	 * <pre>
	 * I    A:B   B    -> A.C
	 * 
	 * II   A:B   B    -> A.C
	 *            C*
	 * </pre>
	 */
	public void testAddChildToStem() {
		root.addChild("A").setStem(p("°.B"));
		MockCell b = root.addChild("B");

		assertDelivery = false;
		send(p("A.C"));
		
		b.addChild("C").withReaction();
		
		send(p("A.C"));
	}
	
}
