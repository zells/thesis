package org.zells.test;

import org.zells.*;

public class TestSampleFibonacci extends LibraryTest {
	
	public void testZero() {
		assertFibonacci("0", "0");
	}
	
	public void testOne() {
		assertFibonacci("1", "1");
	}
	
	public void testTwo() {
		assertFibonacci("2", "1");
	}
	
	public void testThree() {
		assertFibonacci("3", "2");
	}
	
	public void testFour() {
		assertFibonacci("4", "3");
	}
	
//	public void testFive() {
//		assertFibonacci("5", "5");
//	}
//	
//	public void testSix() {
//		assertFibonacci("6", "8");
//	}
//	
//	public void testSeven() {
//		assertFibonacci("7", "13");
//	}
//	
//	public void testEight() {
//		assertFibonacci("8", "21");
//	}
	
	public static int counter = 0;
	
	private void assertFibonacci(String i, String number) {
		Cell index = root.addChild("index").setStem(p("°.Zells.Literal.Number." + i));
		
		send(p("Fibonacci"), p("°.index"));
		
		assertNotNull(index.getChild("response"));
		send(p("index.response.value"));
		assertEquals(p("°.Zells.Literal.Number." + number), res.deliveredTo);
//		System.out.println(i + ": " + counter);
		counter = 0;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		patience = 500;
		
		Cell fib = root
				.addChild("Fibonacci").setReaction(new Reaction(
						m("cell.create.isOne", "°.Zells.Literal.Number.1"),
						m("message.equals", "isOne"),
						m("isOne.response.ifTrue", "respondOne"),

						m("cell.create.isZero", "°.Zells.Literal.Number.0"),
						m("message.equals", "isZero"),
						m("isZero.response.ifTrue", "respondZero"),

						m("isOne.response.or", "isZero.response"),
						m("isZero.response.response.ifFalse", "respondSum")
						));
		
		fib
				.addChild("respondZero").setReaction(new Reaction(
						m("parent.parent.message.respond", "°.Zells.Literal.Number.0")
						));
		
		fib
				.addChild("respondOne").setReaction(new Reaction(
						m("parent.parent.message.respond", "°.Zells.Literal.Number.1")
						));
		
		fib
				.addChild("respondSum").setReaction(new Reaction(
						m("cell.create.minusOne", "°.Zells.Literal.Number.1"),
						m("parent.parent.message.subtract", "minusOne"),
						m("parent.parent.parent", "minusOne.response"),
						
						m("cell.create.minusTwo", "°.Zells.Literal.Number.2"),
						m("parent.parent.message.subtract", "minusTwo"),
						m("parent.parent.parent", "minusTwo.response"),
						
						m("minusOne.response.response.add", "minusTwo.response.response"),
						
						m("parent.parent.message.respond", "minusTwo.response.response.response")
						));
	}
}
