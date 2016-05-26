package org.rtens.cell.test;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.glue.PathFormat;
import org.rtens.cell.test.TestContext.MockReaction;
import org.rtens.cell.test.TestReceive.*;

public class TestAdoption extends TestCase {
	
	private MockCell root;
	private MockCell a;
	private MockCell sx;
	
	public void testSimpleForAddingChild() {
		assertAdoption("A.X.cell.Children.add", "°.Prophet.Literal.String.Z", true);
		
		assertNotNull(a.getChild("X").getChild("Z"));
		assertNull(sx.getChild("Z"));
	}
	
	/**
	 * Add child Q to A.X
	 * 
	 * <pre>
	 * A:K.P  K:O  O         A:K.P
	 *             P   =>    X:parent.stem.X
	 *             X         Q
	 * </pre>
	 */
	public void testMultipleInheritance() {
		root = new MockCell(null, "°");
		TestReceive.mockConnections = new MockConnections(root);
		root.setConnections(TestReceive.mockConnections);
		
		Cell a = root.addChild("A").setStem(new Path("°", "K", "P"));
		root.addChild("K").setStem(new Path("°", "O"));
		root.addChild("O")
				.addChild("P")
				.addChild("X").withReaction();
		
		Result r = send("A.X", "Hi");
		
		assertTrue(r.wasDelivered());
		
		send("A.X.cell.Children.add", "°.Prophet.Literal.String.Q");
		
		assertNotNull(a.getChild("X"));
		assertNotNull(a.getChild("X").getChild("Q"));
		assertEquals(new Path("°", "A", "stem", "X"), a.getChild("X").getStem());
	}
	
	public void testAddStem() {
		assertAdoption("A.X.cell.cell.Children.add", "°.Prophet.Literal.String.Stem", true);
		
		assertNotNull(a.getChild("X").getStem());
		assertNull(sx.getStem());
	}
	
	public void testClearStem() {
		sx.setStem(new Path("MyStem"));
		
		assertAdoption("A.X.cell.Stem.clear", "°", false);
		
		assertEquals(new Path("MyStem"), sx.getStem());
		assertEquals(new Path(), a.getChild("X").getStem());
	}
	
	public void testChangeStem() {
		sx.setStem(new Path("My", "Stem"));
		assertAdoption("A.X.cell.Stem.add", "°.Prophet.Literal.String.New", false);
		
		assertEquals(new Path("My", "Stem"), sx.getStem());
		assertEquals(new Path("My", "Stem", "New"), a.getChild("X").getStem());
	}
	
	public void testChangeName() {
		send("A.X.cell.Name.cell.Stem.clear", "°");
		send("A.X.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.°");
		send("A.X.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.Prophet");
		send("A.X.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.Literal");
		send("A.X.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.String");
		send("A.X.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.NewName");
		
		assertNotNull(a.getChild("NewName"));
		assertEquals(new Path("°", "A", "stem", "X"), a.getChild("NewName").getStem());
		assertEquals("X", sx.getName());
	}
	
	public void testClearReaction() {
		Reaction r = new Reaction();
		r.add(new Send(new Path("One"), new Path("Two")));
		sx.setReaction(r);
		
		assertAdoption("A.X.cell.Reaction.clear", "°", true);
		
		assertEquals(new Reaction(), a.getChild("X").getReaction());
	}
	
	public void testChangeReaction() {
		Reaction r = new Reaction();
		r.add(new Send(new Path("One"), new Path("Two")));
		sx.setReaction(r);
		
		send("A.X.cell.Reaction.add", "°");
		
		send("A.X.cell.Reaction.1.Receiver.add", "°.Prophet.Literal.String.My");
		send("A.X.cell.Reaction.1.Receiver.add", "°.Prophet.Literal.String.Receiver");
		
		send("A.X.cell.Reaction.1.Message.add", "°.Prophet.Literal.String.Your");
		send("A.X.cell.Reaction.1.Message.add", "°.Prophet.Literal.String.Message");
		
		Reaction reactionAX = (Reaction) a.getChild("X").getReaction();
		assertEquals(2, reactionAX.getSends().size());
		assertEquals(r.getSends().getFirst(), reactionAX.getSends().get(0));
		assertEquals(new Send(new Path("My", "Receiver"), new Path("Your", "Message")), reactionAX.getSends().get(1));
	}
	
	public void testChangeActive() {
		send("A.X.cell.Active.cell.Stem.clear", "°");
		send("A.X.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.°");
		send("A.X.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.Prophet");
		send("A.X.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.False");
		
		assertEquals(false, a.getChild("X").isActive());
		assertEquals(true, sx.isActive());
	}
	
	/**
	 * -> A.C.D and check if A.D.C.context.0 exists and has child X
	 * 
	 * <pre>
	 * A:B   B
	 *       C
	 *       D { context.cell.Children.add °.Prophet.Literal.String.X }
	 * 
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void testAdoptContext() throws Exception {
		root = new MockCell(null, "°");
		
		TestReceive.mockConnections = new MockConnections(root);
		root.setConnections(TestReceive.mockConnections);
		
		MockReaction r = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("context.cell.Children.add", "°.Prophet.Literal.String.X", cell, context);
				super.execute(cell, context);
			}
		};
		
		root.addChild("A").setStem(new Path("°", "B"));
		Cell bcd = root.addChild("B")
				.addChild("C")
				.addChild("D").setReaction(r);
		
		Result res = send("A.C.D", "°");
		
		assertTrue(res.wasDelivered());
		assertEquals(new Path("°", "A", "C", "D", "context", "X"), root.getChild("A")
				.getChild("C")
				.getChild("D")
				.getContext().getContextChildren().get(0).getFirst().getPath());
		assertTrue(bcd.getContext().getContextChildren().isEmpty());
	}
	
	private void assertAdoption(String receiver, String message, boolean assertStem) {
		Result r = send(receiver, message);
		
		assertTrue(r.wasDelivered());
		assertNotNull(a.getChild("X"));
		
		if (assertStem)
			assertEquals(new Path("°", "A", "stem", "X"), a.getChild("X").getStem());
	}
	
	private Result send(String receiver, String message) {
		try {
			return root.send(new Send(PathFormat.parse(receiver),
					PathFormat.parse(message)),
					TestReceive.rootContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		
		a = root.addChild("A").setStem(new Path("°", "S"));
		sx = root.addChild("S").addChild("X");
		
		TestReceive.mockConnections = new MockConnections(root);
		root.setConnections(TestReceive.mockConnections);
	}
	
}
