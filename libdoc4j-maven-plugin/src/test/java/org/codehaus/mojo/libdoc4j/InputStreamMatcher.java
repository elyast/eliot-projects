package org.codehaus.mojo.libdoc4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamMatcher extends BaseMatcher<InputStream>{
	String[] fileContent;
	boolean useRegex;
	
	private static Logger logger = LoggerFactory.getLogger(InputStreamMatcher.class);
	
	public InputStreamMatcher(String[] fileContent) {
		this(fileContent, false);
	}
	
	public InputStreamMatcher(String[] fileContent, boolean useRegex) {
		this.fileContent = fileContent;
		this.useRegex = useRegex;		
	}
	
	@Override
	public boolean matches(Object obj) {
		if (!(obj instanceof InputStream)) {
			logger.info("Not matching: not instance of InputStream");
			return false;
		}
		
		try {
			InputStream inputStream = (InputStream) obj;
			BufferedReader reader;
			InputStreamReader isr = new InputStreamReader(inputStream);
			reader = new BufferedReader(isr);
			

			int cnt = 0;
			String line;
			while ((line=reader.readLine()) != null) {
				if (cnt < fileContent.length) {
					if (useRegex) {
						if (!line.matches(fileContent[cnt])) {
							logger.info("Expected regex: " + fileContent[cnt] + ", given string: " + line);
							return false;
						}
					} else {
						if (!line.equals(fileContent[cnt])) {
							logger.info("Expected string: " + fileContent[cnt] + ", given string: " + line);
							return false;
						}					
					}
				}
				cnt++;
			}
			
			if (cnt != fileContent.length) {
				logger.info("Incorrect number of lines in file: " + cnt + ", expected: " + fileContent.length);
				return false;
			}
			
			return true;
		} catch (IOException e) {
			logger.info("Not matching - exception occured: " + e.getStackTrace());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("matching InputStream");
	}
}
