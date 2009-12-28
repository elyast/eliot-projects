package org.robotframework.javalib.library;

import org.robotframework.javalib.library.RobotJavaLibrary;

public interface RobotJavaDocumentedLibrary extends RobotJavaLibrary {
    /**
     * Returns all arguments of the given keyword
     * 
     * @return arguments of given keywords
     */
    public String[] getKeywordArguments(String keywordName);

    /**
     * Returns the documentation of the given keyword
     * 
     * @return documentation of given keywords
     */
    public String getKeywordDocumentation(String keywordName);

}
