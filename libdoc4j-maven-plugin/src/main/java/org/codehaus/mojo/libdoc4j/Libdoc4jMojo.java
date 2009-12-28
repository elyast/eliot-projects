package org.codehaus.mojo.libdoc4j;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @author Sasnal.net
 * @goal generate-doc
 * @phase compile
 * @executionStrategy once-per-session
 * @requiresDependencyResolution compile
 */
public class Libdoc4jMojo extends AbstractMojo {

    /**
     * This folder is added to the list of those folders
     * containing source to be compiled. Use this if your
     * plugin generates source code.
     *
     * @parameter expression="${project.build.directory}/robot"
     * @required
     */
    File outputDirectory;
    
    /**
     * Library class of spring robotframework library
     * 
     * @parameter
     * @required
     */
    String libraryClass;
    
    /**
     * <i>Maven Internal</i>: The Project descriptor.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project;
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
	outputDirectory.mkdirs();
	if (!outputDirectory.exists()) {
	    throw new MojoFailureException(outputDirectory.toString() + " doesn't exist");
	}
	File libraryClassFile = new File(outputDirectory, libraryClass + ".xml");
	if (libraryClassFile.exists()) {
	    getLog().info("Libdoc4j nothing to be run result already exists");
	    return;
	}
	String[] parameters = new String[] {"--output", libraryClassFile.getPath(), 
		"--library-class", libraryClass};	
	try {
	    List<?> classpathElements = project.getCompileClasspathElements();
	    Executor.generateDocumentation(parameters, classpathElements);
	} catch (Exception e) {
	   throw new MojoExecutionException(e.getMessage(), e);
	}
	
    }

}
