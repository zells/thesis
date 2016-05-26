package org.rtens.cell.test;

import java.util.*;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.glue.PathFormat;
import org.rtens.cell.test.TestReceive.*;

public class TestContext extends TestCase {
	
	private MockCell root;
	
	public void testEquality() {
		Context c0a = new Context(null, new Path("A", "B"), new Send(new Path("C"), new Path("L", "M")));
		Context c0b = new Context(null, new Path("A", "B"), new Send(new Path("C"), new Path("L", "M")));
		Context c0c = new Context(null, new Path("A", "B"), new Send(new Path("C"), new Path("L", "C")));
		Context c0d = new Context(null, new Path("A", "C"), new Send(new Path("C"), new Path("L", "M")));
		Context c0e = new Context(null, new Path("A", "B"), new Send(new Path("C"), new Path("L", "M")));
		
		c0a.setId(1);
		c0b.setId(1);
		
		assertTrue(c0a.equals(c0b));
		assertFalse(c0a.equals(c0c));
		assertFalse(c0a.equals(c0d));
		assertFalse(c0a.equals(c0e));
		
		Context c1a = new Context(c0a, new Path("X", "E"), new Send(new Path("O"), new Path("Z", "T")));
		Context c1b = new Context(c0b, new Path("X", "E"), new Send(new Path("O"), new Path("Z", "T")));
		
		c1a.setId(12);
		c1b.setId(12);
		
		assertTrue(c1a.equals(c1b));
		
		c1a.setId(1);
		c1b.setId(2);
		
		assertFalse(c1a.equals(c1b));
	}
	
	/**
	 * The response which creates a child in its "context" is executed two time by sending two
	 * messages to its cell. A different context child should be used each time.
	 */
	public void testOwnContext() {
		Cell a = root.addChild("A").setActive(true);
		
		MockReaction ar = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				cell.getContext().addChild("Carl", context);
				cell.getContext().addChild("Lenny", context);
			}
		};
		a.setReaction(ar);
		
		send(new Path("A"));
		send(new Path("A"));
		
		assertTrue(a.getContext() != null);
		assertEquals(2, a.getContext().getContextChildren().size());
		assertEquals(2, a.getContext().getContextChildren().get(0).size());
		assertEquals(2, a.getContext().getContextChildren().get(1).size());
	}
	
	public void testAccessContextOfOtherCell() {
		Cell a = root.addChild("A").setActive(true);
		Cell b = root.addChild("B").setActive(true);
		
		final MockReaction xr = new MockReaction();
		
		MockReaction ar = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				cell.getContext().addChild("X", context).setActive(true).setReaction(xr);
				send("°.B", "Nothing", cell, context);
			}
		};
		a.setReaction(ar);
		
		MockReaction br = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("°.A.context.X", "ShouldBeX", cell, context);
			}
		};
		b.setReaction(br);
		
		send(new Path("A"));
		
		assertTrue(a.getContext() != null);
		assertEquals("X", a.getContext().getContextChildren().get(0).getFirst().getName());
		assertEquals(1, xr.executedCount);
		assertEquals("ShouldBeX", xr.context.getMessage().getLast());
	}
	
	public void testOwnMessage() {
		MockCell m = (MockCell) root.addChild("M").withReaction();
		
		MockReaction ar = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("message", "nothing", cell, context);
			}
		};
		root.addChild("A").setReaction(ar).setActive(true);
		
		root.send(new Send(new Path("°", "A"), new Path("°", "M")), TestReceive.rootContext());
		
		assertEquals(1, m.received);
	}
	
	public void testOthersMessage() {
		Cell a = root.addChild("A").setActive(true);
		Cell b = root.addChild("B").setActive(true);
		MockCell m = (MockCell) root.addChild("M").withReaction();
		
		MockReaction ar = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("°.B", "Nothing", cell, context);
			}
		};
		a.setReaction(ar);
		
		MockReaction br = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("°.A.message", "Test", cell, context);
			}
		};
		b.setReaction(br);
		
		root.send(new Send(new Path("A"), new Path("°", "M")), TestReceive.rootContext());
		
		assertEquals(1, m.received);
	}
	
	/**
	 * A cell sends itself to itself three times and should be able to access the first message from
	 * within the third execution.
	 * 
	 * <pre>
	 * -M-> A(0) -A(0)-> A(1) -A(1)-> A(2) -A(2)-> A(3) -> message.message.message.message
	 *                                                      A(2)     A(1)   A(0)     M
	 * </pre>
	 * 
	 * Also for each number for "message", the context of the cell created when it received a
	 * message should be accessible.
	 */
	public void testRecursion() {
		Cell a = root.addChild("A").setActive(true);
		MockCell m = root.addChild("M").withReaction();
		
		MockReaction ar = new MockReaction() {
			private int count = 0;
			
			@Override
			public void execute(Cell cell, Context context) {
				count++;
				
				if (count <= 3) {
					cell.getContext().addChild("X" + count, context)
							.setReaction(new MockReaction())
							.setActive(true);
					send("self", "self", cell, context);
				} else {
					send("message.message.message.message", "Test", cell, context);
					send("message.context.X3", "context2", cell, context);
					send("message.message.context.X2", "context1", cell, context);
					send("message.message.message.context.X1", "context0", cell, context);
				}
			}
		};
		a.setReaction(ar);
		
		root.send(new Send(new Path("A"), new Path("°", "M")), TestReceive.rootContext());
		
		assertEquals(1, m.received);
		
		ContextCell context = a.getContext();
		assertEquals(3, context.getContextChildren().size());
		
		assertEquals(1, ((MockReaction) getContextChild(context, "X1").getReaction()).executedCount);
		assertEquals(new Path("°", "A", "context0"),
				((MockReaction) getContextChild(context, "X1").getReaction()).context.getMessage());
		assertEquals(new Path("°", "A", "context1"),
				((MockReaction) getContextChild(context, "X2").getReaction()).context.getMessage());
		assertEquals(new Path("°", "A", "context2"),
				((MockReaction) getContextChild(context, "X3").getReaction()).context.getMessage());
	}
	
	private Cell getContextChild(ContextCell context, String name) {
		for (LinkedList<Cell> children : context.getContextChildren()) {
			for (Cell child : children) {
				if (child.getName().equals(name)) {
					return child;
				}
			}
		}
		return null;
	}
	
	public void testSendToMessageResolvesCorrectly() {
		MockReaction reaction = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("message.X", "Hi", cell, context);
				super.execute(cell, context);
			}
		};
		root.addChild("A").setReaction(reaction);
		
		MockCell x = (MockCell) root.addChild("M").addChild("X").withReaction();
		
		send(new Path("A"), new Path("°", "M"));
		
		assertEquals(new Path("°", "M", "X"), x.parameters.resolvedStack.getLast());
	}
	
	/**
	 * A creates context.X and then sends message to context.X.Z
	 * 
	 * <pre>
	 * A          S
	 * context    Z*
	 * X:°S
	 * </pre>
	 */
	public void testInheritByContextChild() {
		MockReaction ar = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				cell.getContext().addChild("X", context).setStem(new Path("°", "S")).setActive(true);
				send("context.X.Z", "hi", cell, context);
			}
		};
		root.addChild("A")
				.setActive(true)
				.setReaction(ar)
				.setConnections(new Connections(root));
		
		MockCell z = (MockCell) root.addChild("S")
				.addChild("Z").withReaction();
		
		Result r = send(new Path("A"));
		
		assertTrue(r.wasDelivered());
		assertEquals(1, z.received);
		assertEquals(1, z.reaction.executed);
	}
	
	/**
	 * If A -> B and B creates A.context.X, A should be able to access A.context.X
	 */
	public void testContextChildReceivesAlthoughCreatedByFollowingReaction() {
		MockReaction ar = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				send("°.B", "Hi", cell, context);
				send("context.X", "Ho", cell, context);
			};
		};
		
		MockReaction br = new MockReaction() {
			@Override
			public void execute(Cell cell, Context context) {
				root.getChild("A").getContext().addChild("X", context)
						.setActive(true)
						.setReaction(new Reaction());
			}
		};
		
		root.addChild("A").setReaction(ar);
		root.addChild("B").setReaction(br);
		
		Result r = send(new Path("A"));
		
		//		System.out.println(ar.results.getLast().log);
		assertTrue(r.wasDelivered());
	}
	
	public void testSearchContextInPeers() {
	// TODO Write test where a certain context child (e.g. "0") exists but on a different host.
	}
	
	private Result send(Path receiver) {
		return send(receiver, new Path());
	}
	
	private Result send(Path receiver, Path message) {
		return root.send(new Send(receiver, message), TestReceive.rootContext());
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		Cell c = root.addChild("Cell").setActive(true);
		c.setReaction(new MockReaction());
	}
	
	public static class MockReaction implements NativeReaction {
		
		public Cell cell;
		public Context context;
		public int executedCount = 0;
		
		public LinkedList<Result> results = new LinkedList<Result>();
		
		@Override
		public void execute(Cell cell, Context context) {
			executedCount++;
			this.cell = cell;
			this.context = context;
		}
		
		protected void send(String receiver, String message, Cell cell, Context context) {
			Send send;
			try {
				send = new Send(new Path(context.getReceiver(), PathFormat.parse(receiver)).resolveRootAndSelf(),
						new Path(context.getReceiver(), PathFormat.parse(message)).resolveRootAndSelf());
				Result r = cell.send(send, context);
				
				if (!r.wasDelivered())
					System.err.println("---------- Not delivered: " + send + " ---------------------\n" + r.log);
				assertTrue(r.wasDelivered());
				results.add(r);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Error: " + e);
			}
		}
		
	}
}
