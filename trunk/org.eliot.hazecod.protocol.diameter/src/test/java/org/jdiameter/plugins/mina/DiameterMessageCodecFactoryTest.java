package org.jdiameter.plugins.mina;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiameterMessageCodecFactoryTest {

	private DiameterMessageCodecFactory testObj;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDiameterMessageCodecFactory() {
		testObj = new DiameterMessageCodecFactory();
		assertNotNull(testObj);
	}

}
