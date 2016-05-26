package org.rtens.cell.test;

import junit.framework.*;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rtens.cell.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestServer.class);
		suite.addTestSuite(TestReceive.class);
		suite.addTestSuite(TestLoader.class);
		suite.addTestSuite(TestKernelCells.class);
		suite.addTestSuite(TestAdoption.class);
		suite.addTestSuite(TestReceiveRedundancy.class);
		suite.addTestSuite(TestMessenger.class);
		suite.addTestSuite(TestCellConnector.class);
		suite.addTestSuite(TestContext.class);
		//$JUnit-END$
		return suite;
	}
	
}
