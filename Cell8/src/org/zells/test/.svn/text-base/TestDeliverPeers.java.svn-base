package org.zells.test;

import org.zells.Path;

public class TestDeliverPeers extends DeliveryTest {
	
	/**
	 * Root1 sends to A which has response in other peer.
	 * 
	 * <pre>
	 * 0 A(1)      1 A*
	 * </pre>
	 */
	public void testReactionInPeer() {
		MockCell a0 = root.addChild("A");
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A").withReaction();
		
		a0.addPeer(new MockPeer(a1));
		
		send(p("A"), p("°"));
		
		assertEquals(1, a1.reaction.executed);
	}
	
	/**
	 * The actual receiver is a peer of the peer of the sender
	 * 
	 * <pre>
	 * 0 A(1)      1 A(2)      2 A*
	 * </pre>
	 */
	public void testReactionOfPeerOfPeer() {
		MockCell a0 = root.addChild("A");
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A");
		
		MockCell root2 = new MockCell(null, "2");
		MockCell a2 = root2.addChild("A").withReaction();
		
		a0.addPeer(new MockPeer(a1));
		a1.addPeer(new MockPeer(a2));
		
		send(p("A"), p("°"));
		
		assertEquals(1, a2.reaction.executed);
	}
	
	/**
	 * The local cell has some of the children but not all
	 * 
	 * <pre>
	 * 0 A(1)    1 A
	 *   B         B
	 *   C         C*
	 * </pre>
	 */
	public void testReactionOfPeerOfParent() {
		MockCell a0 = root.addChild("A");
		a0.addChild("B").addChild("C");
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A");
		MockCell c1 = a1.addChild("B").addChild("C").withReaction();
		
		a0.addPeer(new MockPeer(a1));
		
		send(p("A.B.C"), p("°"));
		
		assertEquals(1, c1.reaction.executed);
		assertEquals(new Path("°", "A", "B", "C"), c1.reaction.role.subPath(-1));
	}
	
	/**
	 * <pre>
	 * 0 A(1)    C     1 A
	 *   B(1):C  D*		 B
	 * </pre>
	 */
	public void testInheritAfterSearchingPeers() {
		MockCell a0 = root.addChild("A");
		MockCell b0 = a0.addChild("B").setStem(new Path("°", "C"));
		MockCell d0 = root.addChild("C").addChild("D").withReaction();
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A");
		MockCell b1 = a1.addChild("B");
		
		a0.addPeer(new MockPeer(a1));
		b0.addPeer(new MockPeer(b1));
		
		send(p("A.B.D"), p("°"));
		
		assertEquals(1, d0.reaction.executed);
		assertTrue(0 < a1.delivered);
		assertTrue(0 < b1.delivered);
	}
	
	/**
	 * <pre>
	 * 0 A(1) C    1 A
	 *   B:C  D*     B
	 *               D*
	 * </pre>
	 */
	public void testDontInheritIfChildIsInPeer() {
		MockCell a0 = root.addChild("A");
		MockCell b0 = a0.addChild("B").setStem(new Path("°", "C"));
		MockCell d0 = root.addChild("C").addChild("D").withReaction();
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A");
		MockCell d1 = a1.addChild("B").addChild("D").withReaction();
		
		a0.addPeer(new MockPeer(a1));
		
		send(b0, p("D"), p("°"));
		
		assertEquals(1, d1.reaction.executed);
		assertEquals(0, d0.reaction.executed);
	}
	
	/**
	 * <pre>
	 * 0 A(2)    1 A(2)  C     2 A
	 *   B(1)      B:C   D*      B
	 *   					     D*
	 * </pre>
	 */
	public void testDontInheritIfChildIsInPeer2() {
		MockCell a0 = root.addChild("A");
		MockCell b0 = a0.addChild("B");
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A");
		MockCell b1 = a1.addChild("B").setStem(new Path("°", "C"));
		MockCell d1 = root1.addChild("C").addChild("D").withReaction();
		
		MockCell root2 = new MockCell(null, "2");
		MockCell a2 = root2.addChild("A");
		MockCell d2 = a2.addChild("B").addChild("D").withReaction();
		
		a0.addPeer(new MockPeer(a2));
		b0.addPeer(new MockPeer(b1));
		a1.addPeer(new MockPeer(a2));
		
		send(b0, p("D"), p(""));
		
		assertEquals(0, d1.reaction.executed);
		assertEquals(1, d2.reaction.executed);
	}
	
	/**
	 * °@1 -> A.B.C.D.P
	 * 
	 * <pre>
	 * 0     °        1   °        2   °(0)
	 *     /   \         /  \         /    \
	 *   A(1)   K       A(2) U       A      V
	 *   B(1)   P*      B:U  C       B      D:K
	 *                       D       C:V
	 *                       P*
	 * </pre>
	 */
	public void testAdvancedCase() {
		MockCell a0 = root.addChild("A");
		MockCell b0 = a0.addChild("B");
		MockCell p0 = root.addChild("K").addChild("P").withReaction();
		
		MockCell root1 = new MockCell(null, "1");
		MockCell a1 = root1.addChild("A");
		MockCell b1 = a1.addChild("B").setStem(new Path("°", "U"));
		MockCell p1 = root1.addChild("U").addChild("C").addChild("D").addChild("P").withReaction();
		
		MockCell root2 = new MockCell(null, "2");
		MockCell a2 = root2.addChild("A");
		a2.addChild("B").addChild("C").setStem(new Path("°", "V"));
		root2.addChild("V").addChild("D").setStem(new Path("°", "K"));
		
		root2.addPeer(new MockPeer(root));
		a1.addPeer(new MockPeer(a2));
		b0.addPeer(new MockPeer(b1));
		a0.addPeer(new MockPeer(a1));
		
		send(p("A.B.C.D.P"), p("°"));
		
		assertEquals(0, p1.reaction.executed);
		assertEquals(1, p0.reaction.executed);
	}
	
}
