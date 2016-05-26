package org.zells.test;

import org.zells.*;

public class TestLibraryBasics extends LibraryTest {
	
	/**
	 * -M-> A -self-> message.respond
	 * 
	 * <pre>
	 * M   A*   =>    M
	 *                response:A.#99.self
	 * </pre>
	 */
	public void testResponse() {
		root.addChild("A").setReaction(new Reaction(m("message.respond", "self")));
		Cell m = root.addChild("M").setStem(p("°.Cell"));
		
		send(p("A"), p("°.M"));
		
		assertNotNull(m.getChild("response"));
		assertEquals(p("°.A.#0.self"), m.getChild("response").getStem());
	}
	
}
