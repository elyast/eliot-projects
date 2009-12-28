package org.robotframework.springdoc;

import org.robotframework.javalib.keyword.EnhancedDocumentedKeyword;


public class EnhancedDocumentedKeywordMock implements EnhancedDocumentedKeyword {
    public Object execute(Object[] arguments) {
        return getName() + " _ Spring Keyword";
    }
    
    private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String[] getArgumentNames() {
		return new String[] {getName() + " _ arg1", getName() + " _ arg2"};
	}

	@Override
	public String getDocumentation() {
		return getName() + " _ documentation";
	}
}