package org.zells.test;



public class TestDeliverInheritance extends DeliveryTest {
	
	/**
	 * A doesn't have a reaction on its own but inherits B's which inherits C's
	 * 
	 * <pre>
	 * A:B  B:C C*
	 * 
	 * -> A
	 * </pre>
	 */
	public void testInheritReaction() {
		root.addChild("A").setStem(p("°.B"));
		root.addChild("B").setStem(p("°.C"));
		MockCell c = root.addChild("C").withReaction();
		
		send(p("A"));
		
		assertEquals(1, c.reaction.executed);
		assertEquals(p("°.A"), c.reaction.role.subPath(-1));
	}
	
	/**
	 * -> A.C.E
	 * 
	 * <pre>
	 * A:B  B    D
	 *      C:D  E*
	 * </pre>
	 */
	public void testInheritChild() {
		root.addChild("A").setStem(p("°.B"));
		root.addChild("B")
				.addChild("C").setStem(p("°.D"));
		MockCell e = root.addChild("D")
				.addChild("E").withReaction();
		
		send(p("A.C.E"));
		
		assertEquals(1, e.reaction.executed);
		assertEquals(p("°.A.C.E"), e.reaction.role.subPath(-1));
	}
	
	/**
	 * -> A.E => °.D.C.E
	 * 
	 * <pre>
	 * A:°BC  B:D  D
	 *             C
	 *             E*
	 * </pre>
	 */
	public void testNestedInheritance() {
		root.addChild("A").setStem(p("°.B.C"));
		root.addChild("B").setStem(p("°.D"));
		MockCell e = root.addChild("D")
				.addChild("C")
				.addChild("E").withReaction();
		
		send(p("A.E"));
		
		assertEquals(1, e.reaction.executed);
		assertEquals(p("°.A.E"), e.reaction.role.subPath(-1));
	}
	
	/**
	 * Send message to A.X.
	 * 
	 * => C is accessed in role °BC and thus ^D should resolve to °BD
	 * 
	 * <pre>
	 *         ° ____
	 * 		 / |     \
	 * A:°BCE  B:°K   K
	 * 		   D      C:^D
	 * 		   E
	 *         X
	 * </pre>
	 */
	public void testNestedInheritanceWithParent() {
		root.addChild("A").setStem(p("°.B.C.E"));
		MockCell x = root.addChild("B").setStem(p("°.K"))
				.addChild("D")
				.addChild("E")
				.addChild("X").withReaction();
		root.addChild("K")
				.addChild("C").setStem(p("parent.D"));
		
		send(p("A.X"));
		
		assertEquals(1, x.reaction.executed);
		assertEquals(p("°.A.X"), x.reaction.role.subPath(-1));
	}
	
}
