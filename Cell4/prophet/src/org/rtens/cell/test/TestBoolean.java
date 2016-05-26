package org.rtens.cell.test;

import org.rtens.cell.*;
import org.rtens.cell.test.TestReceive.MockCell;

public class TestBoolean extends LibraryTest {
	
	public void testIfTrue() throws InterruptedException {
		MockCell a = (MockCell) root.addChild(new MockCell(root, "A").withReaction());
		
		send("°.Prophet.True.ifTrue", "°.A");
		send("°.Prophet.False.ifTrue", "°.A");
		
		Thread.sleep(100L);
		
		assertEquals(1, a.received);
	}
	
	public void testIfFalse() throws InterruptedException {
		MockCell a = (MockCell) root.addChild(new MockCell(root, "A").withReaction());
		
		send("°.Prophet.True.ifFalse", "°.A");
		send("°.Prophet.False.ifFalse", "°.A");
		
		Thread.sleep(100L);
		
		assertEquals(1, a.received);
	}
	
	public void testTrueFalse() throws InterruptedException {
		MockCell a = (MockCell) root.addChild(new MockCell(root, "A").setStem(new Path("°", "Prophet", "True")));
		
		Result r = send("A.setFalse");
		
		Thread.sleep(150L);
		
		assertTrue(r.wasDelivered());
		assertEquals(new Path("°", "Prophet", "False"), a.getStem());
	}
	
}
