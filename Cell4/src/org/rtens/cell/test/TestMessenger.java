package org.rtens.cell.test;

import java.util.LinkedList;

import org.rtens.cell.*;
import org.rtens.cell.test.TestReceive.MockCell;

import junit.framework.TestCase;

public class TestMessenger extends TestCase {
	
	private MockCell root;
	private LinkedList<Messenger> started = new LinkedList<Messenger>();
	private LinkedList<Messenger> done = new LinkedList<Messenger>();
	
	public void testFlowControl() throws InterruptedException {
		Messenger m = new Messenger(new Send(new Path("°", "A"), new Path()), root, TestReceive.rootContext());
		m.start();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		
		MockCell a = root.addChild("A").withReaction();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(1, done.size());
		assertEquals(1, a.received);
	}
	
	public void testPauseAndUnpause() throws InterruptedException {
		Messenger m = new Messenger(new Send(new Path("°", "A"), new Path()), root, TestReceive.rootContext());
		m.start();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		
		m.pause();
		Thread.sleep(100L);
		
		assertEquals(true, m.isPaused());
		
		MockCell a = root.addChild("A").withReaction();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		assertEquals(0, a.received);
		
		m.unpause();
		
		assertEquals(false, m.isPaused());
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(1, done.size());
		assertEquals(1, a.received);
	}
	
	public void testPauseAndUnpauseAll() throws InterruptedException {
		
		Messenger m = new Messenger(new Send(new Path("°", "A"), new Path()), root, TestReceive.rootContext());
		m.start();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		
		Messenger.pauseAll();
		Thread.sleep(100L);
		
		assertEquals(true, m.isPaused());

		MockCell a = root.addChild("A").withReaction();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		assertEquals(0, a.received);
		
		Messenger.unpauseAll();
		
		assertEquals(false, m.isPaused());
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(1, done.size());
		assertEquals(1, a.received);
	}
	
	public void testCancel() throws InterruptedException {
		
		Messenger m = new Messenger(new Send(new Path("°", "A"), new Path()), root, TestReceive.rootContext());
		m.start();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		
		m.cancel();
		Thread.sleep(100L);
		
		assertEquals(false, m.isPaused());
		assertEquals(true, m.isCancelled());

		MockCell a = root.addChild("A").withReaction();
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		assertEquals(0, a.received);
		
		m.unpause();
		
		assertEquals(false, m.isPaused());
		
		Thread.sleep(100L);
		assertEquals(1, started.size());
		assertEquals(0, done.size());
		assertEquals(0, a.received);
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		Cell c = root.addChild("Cell");
		c.setReaction(new MockResponse());
		
		Messenger.listener = new Messenger.Listener() {
			
			@Override
			public void started(Messenger m) {
				started.add(m);
			}
			
			@Override
			public void done(Messenger m) {
				done.add(m);
			}
			
			@Override
			public void cancelled(Messenger m) {}
			
			@Override
			public void paused(Messenger m) {}
			
			@Override
			public void unpaused(Messenger m) {}
			
			@Override
			public void allPaused() {}
			
			@Override
			public void allUnpaused() {}
		};
	}
	
	@SuppressWarnings("unused")
	private static class MockResponse implements NativeReaction {
		
		public Cell cell;
		public Context context;
		
		@Override
		public void execute(Cell cell, Context context) {
			this.cell = cell;
			this.context = context;
		}
		
	}
	
}
