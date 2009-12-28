package org.codehaus.mojo.deploy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.util.Arrays;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class SpringDmDeployMojoTest {

	private static final String[] SAMPLE_DIR = new String[] {				
				"com.nsn-1.0-SNAPSHOT.jar",
				"com.nsn.aao-1.0.jar",
				"com.nsn.bao-1.0-SNAPSHOT.jar",
				"com.nsn-cao-1.0.jar",
				"com.nsn-eao-1.0-SNAPSHOT.jar",
				"com-nsn-fao-1.0-SNAPSHOT.jar",
				"com.nsn.gao-1.0.jar",
				"com-n-s-n.hao-1.0.jar"
			};
	private SpringDmDeployMojo testObj;
	File dir;
	FileChecker checker;
	
	@Before
	public void setup() {
		testObj = new SpringDmDeployMojo();
		dir = spy(new File("."));
		checker = Mockito.mock(FileChecker.class);
		testObj.setChecker(checker);
	}
	
	@Test
	public void testSnaphostCase() throws Exception {
		when(dir.list()).thenReturn(SAMPLE_DIR);
		when(checker.isFile(Matchers.any(File.class))).thenReturn(true);
		File[] list = testObj.findArtifacts(dir, "com.nsn.bao");
		assertEquals(1, list.length);
		assertEquals("com.nsn.bao-1.0-SNAPSHOT.jar", list[0].getName());
	}
	
	@Test
	public void testNormalCase() throws Exception {
		when(dir.list()).thenReturn(SAMPLE_DIR);
		when(checker.isFile(Matchers.any(File.class))).thenReturn(true);		
		File[] list = testObj.findArtifacts(dir, "com.nsn.aao");
		assertEquals(1, list.length);
		assertEquals("com.nsn.aao-1.0.jar", list[0].getName());
	}
	
	@Test
	public void testNestedCase() throws Exception {
		when(dir.list()).thenReturn(SAMPLE_DIR);
		when(checker.isFile(Matchers.any(File.class))).thenReturn(true);
		File[] list = testObj.findArtifacts(dir, "com.nsn");
		System.out.println(Arrays.toString(list));
		assertEquals(1, list.length);
		assertEquals("com.nsn-1.0-SNAPSHOT.jar", list[0].getName());
	}
}
