package org.zells.test;

import java.util.LinkedList;

import org.zells.*;

public class TestDeliverExecution extends DeliveryTest {
	
	/**
	 * -> A.B -> parent.C
	 * 
	 * <pre>
	 * A
	 * B*
	 * C*
	 * </pre>
	 * @throws InterruptedException 
	 */
	public void testSendToChild() throws InterruptedException {
		MockCell c = root
				.addChild("A")
				.addChild("B").setReaction(new MockReaction(m("parent.C", "M")))
				.addChild("C").withReaction();
		
		send(p("A.B"));
		
		assertEquals(1, c.reaction.executed);
	}
	
	/**
	 * -> A.B -> parent.parent.C
	 * 
	 * <pre>
	 * A_
	 * | \
	 * B* C*
	 * </pre>
	 */
	public void testSendToSister() {
		MockCell a = root.addChild("A");
		a.addChild("B").setReaction(new MockReaction(m("parent.parent.C", "M")));
		MockCell c = a.addChild("C").withReaction();
		
		send(p("A.B"));
		
		assertEquals(1, c.reaction.executed);
	}
	
	/**
	 * -°M-> A -> message
	 * 
	 * <pre>
	 * A M*
	 * </pre>
	 */
	public void testSendToMessage() {
		root.addChild("A").setReaction(new MockReaction(m("message", "°")));
		MockCell m = root.addChild("M").withReaction();
		
		send(p("A"), p("°.M"));
		
		assertEquals(1, m.reaction.executed);
	}
	
	/**
	 * A cell sends "parent" to itself three times and should be able to access the first message
	 * from within the third execution.
	 * 
	 * <pre>
	 *                                                      A(2)    A(1)     A(0)    M
	 * -M-> A(0) -A(0)-> A(1) -A(1)-> A(2) -A(2)-> A(3) -> message.message.message.message
	 * </pre>
	 */
	public void testRecursion() {
		
		root.addChild("A").setReaction(new MockReaction(
				m("°.A", "self")) {
			
			private int count = 0;
			
			@Override
			public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
				count++;
				
				if (count > 3) {
					mailings = new LinkedList<Mailing>();
					mailings.add(m("message.message.message.message", "°"));
				}
				super.execute(receiver, role, message, eid);
			}
		});
		
		MockCell m = root.addChild("M").withReaction();
		
		send(p("°.A"), p("°.M"));
		
		assertEquals(1, m.reaction.executed);
	}
	
	/**
	 * Message is an alias so parent of it should be its actual parent
	 * 
	 * <pre>
	 * N*  A*
	 * M*
	 * 
	 * -°NM-> A -> message -> parent.parent
	 * </pre>
	 */
	public void testSendToParentOfMessage() {
		MockCell n = root.addChild("N").withReaction();
		n.addChild("M").setReaction(new MockReaction(m("parent.parent", "°")));
		root.addChild("A").setReaction(new MockReaction(m("message", "°")));
		
		send(p("A"), p("°.N.M"));
		
		assertEquals(1, n.reaction.executed);
	}
	
	/**
	 * <pre>
	 * A:B  B   D*
	 *      C
	 * </pre>
	 */
	public void testInheritFollowedByRoot() {
		Cell a = root.addChild("A").setStem(p("°.B"));
		root.addChild("B").addChild("C");
		Cell d = root.addChild("D").withReaction();
		
		send(p("A.C.°.D"));
		
		assertNull(a.getChild("C"));
		assertNotNull(d.getChild("#0"));
	}
}
