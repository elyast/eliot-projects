package org.robotframework.springdoc;

import org.robotframework.javalib.keyword.Keyword;


public class KeywordMock implements Keyword {
    public Object execute(Object[] arguments) {
        return "Old Spring Keyword";
    }
}