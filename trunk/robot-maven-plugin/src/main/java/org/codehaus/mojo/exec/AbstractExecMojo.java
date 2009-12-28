package org.codehaus.mojo.exec;

/*
 * Copyright 2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * This class is used for unifying functionality between the 2 mojo exec plugins ('java' and 'exec').
 * It handles parsing the arguments and adding source/test folders.
 * 
 * @author Philippe Jacot (PJA)
 * @author Jerome Lacoste
 */
public abstract class AbstractExecMojo extends AbstractMojo
{
    /**
     * The enclosing project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

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
     * The directory where Robot should execute tests from.
     * 
     * @parameter default-value="src/test/resources/robot-tests"
     * @required
     */
    File robotTestDirectory = new File(
            "src/test/resources/robot-tests");
    
    /**
     * Defines the scope of the classpath passed to the plugin. Set to compile,test,runtime or system depending
     * on your needs.
     * @parameter expression="${exec.classpathScope}" default-value="compile"
     */
    protected String classpathScope;


    /**
     * Collects the project artifacts in the specified List and the project specific classpath 
     * (build output and build test output) Files in the specified List, depending on the plugin classpathScope value.
     * @param artifacts the list where to collect the scope specific artifacts
     * @param theClasspathFiles the list where to collect the scope specific output directories
     * @throws NullPointerException if at least one of the parameter is null
     */
    protected void collectProjectArtifactsAndClasspath( List artifacts, List theClasspathFiles )
    {
        
        if ( "compile".equals( classpathScope ) )
        {
            artifacts.addAll( project.getCompileArtifacts() );
            theClasspathFiles.add( new File( project.getBuild().getOutputDirectory() ) );
        }
        else if ( "test".equals( classpathScope ) )
        {
            artifacts.addAll( project.getTestArtifacts() );
            theClasspathFiles.add( new File( project.getBuild().getTestOutputDirectory() ) );
            theClasspathFiles.add( new File( project.getBuild().getOutputDirectory() ) );
        }
        else if ( "runtime".equals( classpathScope ) )
        {
            artifacts.addAll( project.getRuntimeArtifacts() );
            theClasspathFiles.add( new File( project.getBuild().getOutputDirectory() ) );
        }
        else if ( "system".equals( classpathScope ) )
        {
            artifacts.addAll( project.getSystemArtifacts() );
        }
        else
        {
            throw new IllegalStateException( "Invalid classpath scope: " + classpathScope );
        }

        getLog().debug( "Collected project artifacts " + artifacts );
        getLog().debug( "Collected project classpath " + theClasspathFiles );
    }


}
