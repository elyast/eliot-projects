/**
 * 
 */
package org.codehaus.mojo.libdoc4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.robotframework.javalib.library.SpringDocumentedLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fekat
 * 
 */
public class Executor {

    private static final Logger logger = LoggerFactory
	    .getLogger(Executor.class);

    private static final String KEYWORDS_INDICATOR = "%keywords%";
    private static final String LIBRARY_NAME_INDICATOR = "%libraryName%";
    private static final String DATE_INDICATOR = "%date%";

    private static final String DESCRIPTION_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	    + "<keywordspec generated=\""
	    + DATE_INDICATOR
	    + "\" type=\"library\" name=\""
	    + LIBRARY_NAME_INDICATOR
	    + "\">\n"
	    + "<version>&amp;lt;unknown&amp;gt;</version>\n"
	    + "<doc>Documentation for test library `"
	    + LIBRARY_NAME_INDICATOR
	    + "`.</doc>\n" + KEYWORDS_INDICATOR + "</keywordspec>\n";

    private static final String KEYWORD_INDICATOR = "%keyword%";
    private static final String DOCUMENTATION_INDICATOR = "%documentation%";
    private static final String ARGUMENTS_INDICATOR = "%arguments%";

    private static final String KEYWORD_CONTENT = "<kw name=\""
	    + KEYWORD_INDICATOR + "\">\n" + "<doc>" + DOCUMENTATION_INDICATOR
	    + "</doc>\n" + "<arguments>\n" + ARGUMENTS_INDICATOR
	    + "</arguments>\n" + "</kw>\n";

    private static final String ARGUMENT_INDICATOR = "%argument%";

    private static final String ARGUMENTS_CONTENT = "<arg>"
	    + ARGUMENT_INDICATOR + "</arg>\n";

    SpringDocumentedLibrary library;

    public Executor(SpringDocumentedLibrary library) {
	this.library = library;
    }

    public static void main(String[] args) throws Exception {
	generateDocumentation(args, null);

    }

    public static void generateDocumentation(String[] args,
	    List<?> classpathElements) throws Exception {
	try {
	    CommandLine line = parseCommandLineOptions(args);
	    File f = new File(line.getOptionValue("o"));
	    String libraryName = line.getOptionValue("c");

	    SpringDocumentedLibrary library = loadLibraryClass(
		    classpathElements, libraryName);
	    System.out.println("Passed spring doc: " + library.getClass());

	    Executor executor = new Executor(library);
	    InputStream xmlStream = executor.generateDescription(libraryName);

	    flushResult(f, xmlStream);
	} catch (Exception e) {
	    logger.error("Problem during processing", e.getStackTrace());
	    throw e;
	}
    }

    private static void flushResult(File f, InputStream xmlStream)
	    throws FileNotFoundException, IOException {
	FileOutputStream outputStream = new FileOutputStream(f);
	IOUtils.copy(xmlStream, outputStream);
	outputStream.close();
    }

    private static CommandLine parseCommandLineOptions(String[] args)
	    throws ParseException {
	CommandLineParser parser = new PosixParser();

	Options options = new Options();
	options.addOption("o", "output", true, "output file");
	options.addOption("c", "library-class", true,
	    " full library class name");

	CommandLine line = parser.parse(options, args);
	return line;
    }

    private static SpringDocumentedLibrary loadLibraryClass(
	    List<?> classpathElements, String libraryName)
	    throws MalformedURLException, ClassNotFoundException,
	    InstantiationException, IllegalAccessException {
	Class<?> libraryClass = null;
	ClassLoader classLoaderForInput = null;
	if (classpathElements == null) {
	    libraryClass = Class.forName(libraryName);
	    classLoaderForInput = Thread.currentThread().getContextClassLoader();
	} else {
	    ClassLoader parent = Thread.currentThread().getContextClassLoader();
	    URLClassLoader dependenciesClassLoader = new URLClassLoader(toURL(classpathElements),
		    parent);
	    libraryClass = dependenciesClassLoader.loadClass(libraryName);
	    classLoaderForInput = dependenciesClassLoader;
	}
	Object newInstance = libraryClass.newInstance();
	SpringDocumentedLibrary library = (SpringDocumentedLibrary) newInstance;
	library.setClassLoader(classLoaderForInput);
	return library;
    }

    static URL[] toURL(List<?> classpathElements)
	    throws MalformedURLException {
	if (classpathElements == null) {
	    return new URL[0];
	}
	URL[] result = new URL[classpathElements.size()];
	int i = 0;
	for (Object object : classpathElements) {
	    String path = (String) object;
	    result[i++] = new File(path).toURI().toURL();
	}
	return result;
    }

    public InputStream generateDescription(String libraryName) {
	String date = new SimpleDateFormat("yymmdd HH:mm:ss")
		.format(new Date());

	return new ByteArrayInputStream(DESCRIPTION_CONTENT.replaceAll(
		Pattern.quote(KEYWORDS_INDICATOR), generateKeywords())
		.replaceAll(Pattern.quote(LIBRARY_NAME_INDICATOR), libraryName)
		.replaceAll(Pattern.quote(DATE_INDICATOR), date).getBytes());
    }

    protected String generateKeywords() {
	StringBuilder builder = new StringBuilder();
	for (String keyword : library.getKeywordNames()) {
	    System.out.println("Keyword: " + keyword);
	    builder.append(KEYWORD_CONTENT.replaceAll(
		    Pattern.quote(KEYWORD_INDICATOR), keyword).replaceAll(
		    Pattern.quote(DOCUMENTATION_INDICATOR),
		    library.getKeywordDocumentation(keyword)).replaceAll(
		    Pattern.quote(ARGUMENTS_INDICATOR),
		    generateArguments(keyword)));
	}

	return builder.toString();
    }

    protected String generateArguments(String keywordName) {
	StringBuilder builder = new StringBuilder();
	for (String argument : library.getKeywordArguments(keywordName)) {
	    builder.append(ARGUMENTS_CONTENT.replaceAll(Pattern
		    .quote(ARGUMENT_INDICATOR), argument));
	}

	return builder.toString();
    }

}
