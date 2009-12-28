package org.codehaus.mojo.libdoc4j;

import org.robotframework.javalib.library.SpringDocumentedLibrary;

public class SpringDocumentedLibraryMock extends SpringDocumentedLibrary {

	@Override
	public String[] getKeywordArguments(String kn) {
		if (kn.equals("keyword 1")) {
			return new String[] {"argument 1", "argument 2"};
		} else {
			throw new IllegalArgumentException();
		}
		
	}

	@Override
	public String getKeywordDocumentation(String kn) {
		if (kn.equals("keyword 1")) {
			return "Documentation for keyword 1";
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String[] getKeywordNames() {
		return new String[] {"keyword 1"};	
	}
}
