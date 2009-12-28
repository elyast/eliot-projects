package org.codehaus.mojo.libdoc4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.robotframework.javalib.library.SpringDocumentedLibrary;

@RunWith(MockitoJUnitRunner.class)
public class TestExectutor {
	private static final String CLASSPATH_ELEMENT_1 = "1";
	private static final String CLASSPATH_ELEMENT_2 = "2";
	@Mock SpringDocumentedLibrary library;
	Executor testObj;

	@Test
	public void testFunctionality() throws Exception {
		testObj = new Executor(library);
		
		final String keyword1 = "keyword 1";
		final String[] args = new String[] {"argument 1", "argument 2"};
		final String doc = "Documentation for keyword 1";
		final String libraryName = "library name";
		
		when(library.getKeywordNames()).thenReturn(new String[] {keyword1});
		when(library.getKeywordArguments(keyword1)).thenReturn(args);
		when(library.getKeywordDocumentation((keyword1))).thenReturn(doc);
		
		String[] expectedXmlStreamContent = new String[] {
				Pattern.quote("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"),
				Pattern.quote("<keywordspec generated=\"") + "\\d{6} \\d\\d:\\d\\d:\\d\\d" + Pattern.quote("\" type=\"library\" name=\"" + libraryName + "\">"),
				Pattern.quote("<version>&amp;lt;unknown&amp;gt;</version>"),
				Pattern.quote("<doc>Documentation for test library `" + libraryName + "`.</doc>"),
				Pattern.quote("<kw name=\"" + keyword1 + "\">"),
				Pattern.quote("<doc>" + doc + "</doc>"),
				Pattern.quote("<arguments>"),
				Pattern.quote("<arg>" + args[0] + "</arg>"),
				Pattern.quote("<arg>" + args[1] + "</arg>"),
				Pattern.quote("</arguments>"),
				Pattern.quote("</kw>"),
				Pattern.quote("</keywordspec>")
		};
		
		InputStream xmlStream = testObj.generateDescription(libraryName);
		assertNotNull("Not null", xmlStream);
		assertTrue(xmlStream instanceof InputStream);
		assertTrue(new InputStreamMatcher(expectedXmlStreamContent, true).matches(xmlStream));
	}
	
	@Test
	public void testMainSuccessful() throws Exception {
		final String keyword1 = "keyword 1";
		final String[] args = new String[] {"argument 1", "argument 2"};
		final String doc = "Documentation for keyword 1";
		final String libraryName = "org.codehaus.mojo.libdoc4j.SpringDocumentedLibraryMock";
		
		String[] expectedXmlStreamContent = new String[] {
				Pattern.quote("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"),
				Pattern.quote("<keywordspec generated=\"") + "\\d{6} \\d\\d:\\d\\d:\\d\\d" + Pattern.quote("\" type=\"library\" name=\"" + libraryName + "\">"),
				Pattern.quote("<version>&amp;lt;unknown&amp;gt;</version>"),
				Pattern.quote("<doc>Documentation for test library `" + libraryName + "`.</doc>"),
				Pattern.quote("<kw name=\"" + keyword1 + "\">"),
				Pattern.quote("<doc>" + doc + "</doc>"),
				Pattern.quote("<arguments>"),
				Pattern.quote("<arg>" + args[0] + "</arg>"),
				Pattern.quote("<arg>" + args[1] + "</arg>"),
				Pattern.quote("</arguments>"),
				Pattern.quote("</kw>"),
				Pattern.quote("</keywordspec>")
		};
		
		String[] mainArgs = new String[] {"--output", "file.tmp", "--library-class", "org.codehaus.mojo.libdoc4j.SpringDocumentedLibraryMock"};
		Executor.main(mainArgs);
		File tmpFile = new File("file.tmp");
		assertTrue(tmpFile.exists());
		FileInputStream inputStream = new FileInputStream(tmpFile);
		assertTrue(new InputStreamMatcher(expectedXmlStreamContent, true).matches(inputStream));
		
		//clean
		inputStream.close();
		tmpFile.delete();
	}
	
	@Test
	public void testMainLibraryNotFound() throws Exception {
		String[] mainArgs = new String[] {"--output", "file.tmp", "--library-class", "not existing lib"};
		try {
			Executor.main(mainArgs);
			assertTrue(false); //should not happen
		} catch (Exception e) {
			assertTrue(e instanceof ClassNotFoundException);
		}
		
		//clean	
		new File("file.tmp").delete();
	}
	
	@Test
	public void testToUrl_Null() throws Exception{
	    URL[] expected = new URL[0];
	    URL[] result = testObj.toURL(null);
	    assertArrayEquals(expected, result);
	}
	
	@Test
	public void testToUrl_AbsolutePath() throws Exception{
	    URL[] expected = new URL[1];
	    expected[0] = new File(".").toURI().toURL();
	    List<String> path = new ArrayList<String>();
	    path.add(new File(".").getAbsolutePath());
	    URL[] result = testObj.toURL(path);
	    assertArrayEquals(expected, result);
	}

	@Test
	public void testToUrl_RelativePath() throws Exception{
	    URL[] expected = new URL[1];
	    expected[0] = new File(".").toURI().toURL();
	    List<String> path = new ArrayList<String>();
	    path.add(new File(".").getPath());
	    URL[] result = testObj.toURL(path);
	    assertArrayEquals(expected, result);
	}
	
}
