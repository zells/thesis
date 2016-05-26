package org.rtens.cell.test;

import org.rtens.cell.*;

public class TestLiterals extends LibraryTest {
	
	public void testRootLoaded() {
		assertNotNull(root);
		assertNotNull(root.getChild("Prophet"));
		assertNotNull(root.getChild("Prophet").getChild("Literal"));
	}
	
	public void testString() {
		Result r = send("°.Prophet.Literal.String.\"Hel lo\".cell");
		
		assertTrue(r.wasDelivered());
		assertEquals("Hel lo", r.getReceiver().get(r.getReceiver().size() - 2));
		
		r = send("°.Prophet.Literal.String.Hello.0.stem.cell");
		
		assertTrue(r.wasDelivered());
		assertEquals("H", r.getReceiver().get(r.getReceiver().size() - 2));
		r = send("°.Prophet.Literal.String.Hello.1.stem.cell");
		assertEquals("e", r.getReceiver().get(r.getReceiver().size() - 2));
		r = send("°.Prophet.Literal.String.Hello.2.stem.cell");
		assertEquals("l", r.getReceiver().get(r.getReceiver().size() - 2));
		r = send("°.Prophet.Literal.String.Hello.3.stem.cell");
		assertEquals("l", r.getReceiver().get(r.getReceiver().size() - 2));
		r = send("°.Prophet.Literal.String.Hello.4.stem.cell");
		assertEquals("o", r.getReceiver().get(r.getReceiver().size() - 2));
		
		r = send("°.Prophet.Literal.String.Hello.5.stem.cell");
		assertFalse(r.wasDelivered());
	}
	
}
