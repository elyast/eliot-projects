package org.robotframework.springdoc;

import java.util.Arrays;

import org.robotframework.javalib.library.SpringDocumentedLibrary;
import org.robotframework.javalib.library.SpringLibrary;

import junit.framework.TestCase;

public class SpringDocumentedLibraryIntegrationTest extends TestCase {
    private SpringDocumentedLibrary springLibrary;

    protected void setUp() throws Exception {
        springLibrary = new SpringDocumentedLibrary("org/robotframework/test/keywords.xml");
    }

    public void testFindsKeywords() throws Exception {
        String[] keywordNames = springLibrary.getKeywordNames();
        assertTrue(Arrays.equals(new String[] { "Keyword Wired From Spring", "Conflicting Keyword", "Keyword With Unnormalized Name", "oldkeywordwithoutdocumentation" }, keywordNames));
    }
    
    public void testGetKeywordArguments() throws Exception {
        Object returnValue = springLibrary.getKeywordArguments("Keyword Wired From Spring");
        assertTrue(Arrays.equals(new String[] {"Keyword Wired From Spring _ arg1", "Keyword Wired From Spring _ arg2"}, (String[]) returnValue));
    }
    
    public void testGetKeywordArgumentsOldKeyword() throws Exception {
        Object returnValue = springLibrary.getKeywordArguments("Old Keyword Without Documentation");
        assertTrue(Arrays.equals(new String[0], (String[]) returnValue));
    }
    
    public void testGetKeywordDocumentation() throws Exception {
        Object returnValue = springLibrary.getKeywordDocumentation("Keyword Wired From Spring");
        assertEquals("Keyword Wired From Spring _ documentation", returnValue.toString());    	
    }
    
    public void testGetKeywordDocumentationOldKeyword() throws Exception {
        Object returnValue = springLibrary.getKeywordDocumentation("Old Keyword Without Documentation");
        assertEquals("", returnValue.toString());    	
    }

    public void testRunsKeyword() throws Exception {
        Object returnValue = springLibrary.runKeyword("Keyword Wired From Spring", null);
        assertEquals("Keyword Wired From Spring _ Spring Keyword", returnValue.toString());
    }
    
    public void testRunsKeywordOldKeyword() throws Exception {
        Object returnValue = springLibrary.runKeyword("Old Keyword Without Documentation", null);
        assertEquals("Old Spring Keyword", returnValue.toString());
    }

    public void testUsesProvidedPattern() throws Exception {
        assertTrue(springLibrary.getKeywordNames().length > 0);

        springLibrary = new SpringDocumentedLibrary();
        springLibrary.setConfigFilePattern("com/nonexistent/**.xml");
        assertEquals(0, springLibrary.getKeywordNames().length);
    }

    public void testThrowsExceptionIfTheConfigFilePatternIsNotSet() throws Exception {
        try {
            new SpringLibrary().getKeywordNames();
            fail("Expected IllegalStateException to be thrown.");
        } catch (IllegalStateException e) {
            assertEquals("Config file pattern must be set before calling getKeywordNames.", e.getMessage());
        }
    }
}