package org.rtens.cell.test;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.glue.PathFormat;
import org.rtens.cell.test.TestReceive.*;

/**
 * This whole suite deals with this cell system
 * 
 * <pre>
 *  A__________
 *  B          |
 *  C:^^S      S
 *  /\         /\ 
 * D  Y:^'Y   X  Y
 *    Z
 * </pre>
 */
@SuppressWarnings("unused")
public class TestReceiveRedundancy extends TestCase {
	
	private Result result;
	private MockCell root;
	
	private MockCell a;
	private MockCell ab;
	private MockCell abc;
	
	private MockCell abcd;
	
	private MockCell abcy;
	private MockCell abcyz;
	
	private MockCell as;
	private MockCell asx;
	private MockCell asy;
	private MockCell m;
	
	public void testStemOfInheritedChild() {
		abcyz.withReaction();
		
		send(asy, "Z", "°", new Path("°", "A", "B", "C", "Y"));
		
		assertEquals(0, root.received);
		assertEquals(1, a.received);
		assertEquals(new Path("°", "A", "B", "C", "Y", "Z"), abcyz.parameters.resolvedStack.getLast());
	}
	
	public void testSendDirectlyToChild() {
		abcd.withReaction();
		
		send(abc, "D");
		
		assertEquals(0, root.received);
		assertEquals(0, a.received);
		assertEquals(0, ab.received);
		assertEquals(1, abcd.received);
	}
	
	public void testInheritedCellToSibling() {
		abcd.withReaction();
		
		send(asx, "parent.D", "°", new Path("°", "A", "B", "C", "X"));
		
		assertEquals(0, root.received);
		assertEquals(1, abcd.received);
	}
	
	public void testResolveParentOfNonSpecialChildren() {
		abcd.withReaction();
		
		send(asx, "parent.D", "°", new Path("°", "A", "B", "C", "X"));
		
		assertEquals(0, root.received);
		assertEquals(1, asx.received);
		assertEquals(1, abcd.received);
	}
	
	/**
	 * Doesn't go directly to parent anymore
	 */
	public void testGotoParentDirectly() {
		ab.withReaction();
		
		send(abcd, "parent.parent");
		
		assertEquals(0, a.received);
		assertEquals(1, abc.received);
		assertEquals(1, ab.received);
	}
	
	public void testParentOfInheritedCell() {
		ab.withReaction();
		
		send(asx, "parent.parent", "°", new Path("°", "A", "B", "C", "X"));
		
		assertEquals(0, root.received);
		assertEquals(1, a.received);
		assertEquals(1, asx.received);
		assertEquals(0, abc.received);
		assertEquals(1, ab.received);
		assertEquals(1, ab.reaction.executed);
	}
	
	public void testParentOfSpecialChildren() {
		a.withReaction();
		
		send(ab, "message.parent.A.B.C.D.parent.stem.parent");
		
		assertEquals(1, root.received);
		assertEquals(0, m.received);
		assertEquals(0, abcd.received);
		assertEquals(0, as.received);
		assertEquals(2, a.received);
		
	}
	
	private void send(Cell sender, String receiver) {
		send(sender, receiver, "°");
	}
	
	private void send(Cell sender, String receiver, String message) {
		send(sender, receiver, message, sender.getPath());
	}
	
	private void send(Cell sender, String receiver, String message, Path contextReceiver) {
		Context context = new Context(null, contextReceiver, new Send(contextReceiver, new Path("°", "M")));
		try {
			Messenger m = new Messenger(new Send(PathFormat.parse(receiver), PathFormat.parse(message)), sender,
					context);
			m.start();
			
			int i = 0;
			while (!m.isDone()) {
				Thread.sleep(10L);
				i++;
				if (i > 100) {
					if (m.getResult() != null)
						System.out.println(m.getResult().log);
					fail("Failed while " + sender.getPath() + " (" + contextReceiver + ") was sending " + message
							+ " to " + receiver);
				}
			}
			
			result = m.getResult();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}
	}
	
	private void pl() {
		System.out.println(result.log);
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		TestReceive.mockConnections = new MockConnections(root);
		root.setConnections(TestReceive.mockConnections);
		
		a = (MockCell) root.addChild("A");
		ab = (MockCell) a.addChild("B");
		abc = (MockCell) ab.addChild("C").setStem(new Path("parent", "parent", "S"));
		abcd = (MockCell) abc.addChild("D");
		as = (MockCell) a.addChild("S");
		asx = (MockCell) as.addChild("X");
		
		abcy = (MockCell) abc.addChild("Y").setStem(new Path("parent", "stem", "Y"));
		abcyz = (MockCell) abcy.addChild("Z");
		
		asy = (MockCell) as.addChild("Y");
		
		m = (MockCell) root.addChild("M");
	}
}
