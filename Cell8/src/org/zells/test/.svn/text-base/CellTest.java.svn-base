package org.zells.test;

import java.util.*;

import junit.framework.TestCase;

import org.zells.*;

public class CellTest extends TestCase {
	
	protected Result res;
	protected Cell root;
	
	public static boolean debug = false;
	public static boolean assertDelivery = true;
	
	List<Messenger> running = new LinkedList<Messenger>();
	
	protected boolean printFailedSends = true;
	
	protected int patience = 20;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		root = null;
		res = null;
		debug = false;
		running = new LinkedList<Messenger>();
		assertDelivery = true;
		MockCell.useMockKernel = false;
		
		Messenger.listener = new Messenger.Listener() {
			
			@Override
			public void unpaused(Messenger m) {}
			
			@Override
			public void started(Messenger m) {
				synchronized (running) {
					running.add(m);
				}
			}
			
			@Override
			public void paused(Messenger m) {}
			
			@Override
			public void done(Messenger m) {
				synchronized (running) {
					running.remove(m);
				}
			}
			
			@Override
			public void cancelled(Messenger m) {}
			
			@Override
			public void allResumed() {}
			
			@Override
			public void allPaused() {}
		};
	}
	
	protected Path p(String path) {
		String[] names = path.trim().split("\\.");
		Path p = new Path();
		p.addAll(Arrays.asList(names));
		return p;
	}
	
	protected Mailing m(String receiver, String message) {
		return new Mailing(p(receiver), p(message));
	}
	
	protected Result send(Path receiver) {
		return send(root, receiver, new Path());
	}
	
	protected Result send(Path receiver, Path message) {
		return send(root, receiver, message);
	}
	
	protected Result send(Cell sender, Path receiver, Path message) {
		res = sender.send(new Mailing(receiver, message), sender.getPath(), new DeliveryId());
		if (assertDelivery && !res.wasDelivered()) {
			if (printFailedSends)
				System.err.println(res.log);
			fail("Not deliverd " + new Mailing(receiver, message));
		}
		
		int i = 0;
		do {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (i > patience) {
				synchronized (running) {
					if (printFailedSends)
						for (Messenger m : running)
							if (m.getResult() != null)
								System.err.println(m.getResult().log);
					fail("Did not deliver: " + running);
				}
			}
			
			i++;
		} while (running.size() > 0);
		
		return res;
	}
	
	public static class MockCell extends Cell {
		
		public MockReaction reaction;
		public int delivered = 0;
		public static boolean useMockKernel = false;
		
		private MockKernelCell mockKernel;
		
		public MockCell(Cell parent, String name) {
			super(parent, name);
			setActive(true);
		}
		
		@Override
		public Result deliver(LinkedList<Delivery> deliveryStack, Path message, DeliveryId eid) {
			delivered++;
			return super.deliver(deliveryStack, message, eid);
		}
		
		public MockCell withReaction() {
			reaction = new MockReaction();
			setReaction(reaction);
			return this;
		}
		
		@Override
		public MockCell addChild(String name) {
			MockCell child = new MockCell(this, name);
			addChild(child);
			
			return child;
		}
		
		@Override
		public Cell addChild(Cell child) {
			if (child.getClass().equals(Cell.class))
				return super.addChild(new MockCell(this, child.getName())
						.setStem(child.getStem())
						.setReaction(child.getReaction()));
			
			return super.addChild(child);
		}
		
		@Override
		public MockCell setStem(Path stem) {
			super.setStem(stem);
			return this;
		}
		
		@Override
		public MockCell setReaction(NativeReaction reaction) {
			super.setReaction(reaction);
			return this;
		}
		
		@Override
		public Cell getKernel() {
			if (useMockKernel) {
				if (mockKernel == null)
					mockKernel = new MockKernelCell(this, "cell");
				return mockKernel;
			}
			
			return super.getKernel();
		}
		
	}
	
	public static class MockKernelCell extends KernelCell {
		
		public MockReaction reaction;
		
		public MockKernelCell(Cell parent, String name) {
			super(parent, name);
			
			reaction = new MockReaction();
			setReaction(reaction);
		}
		
	}
	
	public static class MockReaction extends Reaction {
		
		public int executed = 0;
		public Cell receiver;
		public Path role;
		public Path message;
		
		public MockReaction(Mailing... mailings) {
			this.mailings.addAll(Arrays.asList(mailings));
		}
		
		@Override
		public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
			executed++;
			this.receiver = receiver;
			this.role = new Path(role);
			this.message = message;
			
			super.execute(receiver, role, message, eid);
		}
		
	}
	
	public static class MockPeer extends Peer {
		
		public MockPeer(Cell c) {
			this(c, new Address("mock", "0"));
		}
		
		public MockPeer(Cell c, Address a) {
			super(c, a);
		}
		
		@Override
		public String toString() {
			return "->" + cell + "(" + address + ")";
		}
		
		@Override
		public Result deliver(LinkedList<Delivery> deliveryStack, Path message, DeliveryId eid) {
			return cell.deliver(deliveryStack, message, eid);
		}
		
	}
	
}
