package org.zells.test;




public class TestDeliverParent extends DeliveryTest {
	
	/**
	 * -> A.B.parent.parent.X
	 * 
	 * <pre>
	 * A  C*
	 * B
	 * </pre>
	 */
	public void testSimpleParent() {
		root.addChild("A").addChild("B");
		MockCell x = root.addChild("C").withReaction();
		
		send(p("A.B.parent.B.parent.parent.C"));
		
		assertEquals(1, x.reaction.executed);
		assertEquals(p("°.C"), x.reaction.role.subPath(-1));
	}
	
	/**
	 * -> A.B.D.parent.parent
	 * 
	 * <pre>
	 * A*    C
	 * B:C   D
	 * </pre>
	 */
	public void testParentOfInheritedCell() {
		MockCell a = root.addChild("A").withReaction();
		a.addChild("B").setStem(p("°.C"));
		root.addChild("C").addChild("D");

		send(p("A.B.D.parent.parent"));
		
		assertEquals(1, a.reaction.executed);
		assertEquals(p("°.A"), a.reaction.role.subPath(-1));
	}
	
	/**
	 * -> A.B.stem.D.parent => A.B
	 * 
	 * <pre>
	 * A      C*
	 * B*:C   D
	 * </pre>
	 */
	public void testParentOfStem() {
		MockCell ab = root.addChild("A").withReaction()
				.addChild("B").withReaction().setStem(p("°.C"));
		MockCell c = root.addChild("C").withReaction();
		c.addChild("D");
		
		send(p("A.B.stem.D.parent"));

		assertEquals(1, ab.reaction.executed);
		assertEquals(0, c.reaction.executed);
		assertEquals(p("°.A.B"), ab.reaction.role.subPath(-1));
	}

}
