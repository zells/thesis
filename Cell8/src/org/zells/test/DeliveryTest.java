package org.zells.test;


public class DeliveryTest extends CellTest {
	
	protected MockCell root;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		root = new MockCell(null, "°");
		super.root = root;
	}
}
