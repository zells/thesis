package org.zells.test;

import org.zells.*;

public class TestDeliverAdoption extends DeliveryTest {
	
	/**
	 * <pre>
	 * A:B  B    => A:B
	 *      C       C:A.stem.C
	 *      D*      D:A.C.stem.D
	 *              #99:A.C.D
	 * -> A.C.D
	 * </pre>
	 */
	public void testReceiveByInheritedCell() {
		Cell a = root.addChild("A").setStem(new Path("°", "B"));
		MockCell d = root.addChild("B").addChild("C").addChild("D").withReaction();
		
		send(new Path("A", "C", "D"), new Path());
		
		assertNotNull(a.getChild("C"));
		assertEquals(new Path("°", "A", "stem", "C"), a.getChild("C").getStem());
		assertNotNull(a.getChild("C").getChild("D"));
		assertEquals(new Path("°", "A", "C", "stem", "D"), a.getChild("C").getChild("D").getStem());
		assertNotNull(a.getChild("C").getChild("D").getChild("#0"));
		assertEquals(new Path("parent"), a.getChild("C").getChild("D").getChild("#0").getStem());
		
		assertEquals(new Path("°", "A", "C", "D", "#0"), d.reaction.role);
	}
	
	/**
	 * -M-> A -> B -> parent.parent.message
	 * 
	 * <pre>
	 * A*
	 * B*
	 * </pre>
	 */
	public void testSendParentsMessage() {
		MockReaction ar = new MockReaction(m("B", "°"));
		root
				.addChild("A").setReaction(ar)
				.addChild("B").setReaction(new MockReaction(m("parent.parent.message", "°")));
		MockCell m = root.addChild("M").withReaction();
		
		send(p("°.A"), p("°.M"));
		
		assertEquals(1, ar.executed);
		assertEquals(1, m.reaction.executed);
	}
	
	/**
	 * <pre>
	 * A:B  B
	 *      C*
	 * 
	 * -M-> A.C -> message
	 * </pre>
	 */
	public void testMessageOfAdOptedCell() {
		root
				.addChild("A").setStem(new Path("°", "B"));
		
		MockReaction cr = new MockReaction(m("message", "°"));
		root
				.addChild("B")
				.addChild("C").setReaction(cr);
		MockCell m = new MockCell(root, "M").withReaction();
		root.addChild(m);
		
		send(p("°.A.C"), p("°.M"));
		
		assertEquals(p("°.B.C"), cr.receiver.getPath());
		assertEquals(1, m.reaction.executed);
	}
	
	/**
	 * <pre>
	 * A:B  B
	 *      C
	 *      
	 * -> A.C.cell
	 * </pre>
	 */
	public void testKernelCellExecutedOnAdoptedCell() {
		MockCell a = root.addChild("A").setStem(p("°.B"));
		MockCell c = root.addChild("B").addChild("C");
		MockCell.useMockKernel = true;
		
		send(p("A.C.cell"));
		
		MockKernelCell cell = (MockKernelCell) a.getChild("C").getKernel();
		assertNotNull(cell);
		assertEquals(0, ((MockKernelCell)c.getKernel()).reaction.executed);
		assertEquals(1, cell.reaction.executed);
		assertEquals(p("°.A.C.cell"), cell.reaction.receiver.getPath());
	}
	
	/**
	 * <pre>
	 * A:B  B:C  C   =>  A:B          B:C   C
	 *           D*      D:^stem.D          D*
	 * 
	 * -> A.D
	 * </pre>
	 */
	public void testSecondDoesntAdopt() {
		MockCell a = root.addChild("A").setStem(p("°.B"));
		MockCell b = root.addChild("B").setStem(p("°.C"));
		root.addChild("C").addChild("D").withReaction();
		
		send(p("A.D"));
		
		assertNotNull(a.getChild("D"));
		assertNull(b.getChild("D"));
	}
	
}
