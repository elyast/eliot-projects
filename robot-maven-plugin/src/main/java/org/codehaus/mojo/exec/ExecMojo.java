package org.codehaus.mojo.exec;

/*
 * Copyright 2005-2006 The Codehaus.
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.exec.platform.Platform;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * A Plugin for executing external programs.
 * 
 * @author Jerome Lacoste <jerome@coffeebreaks.org>
 * @version $Id: ExecMojo.java 8882 2009-01-22 20:47:34Z lacostej $
 * @goal exec
 * @requiresDependencyResolution test
 * @since 1.0
 */
public class ExecMojo extends AbstractExecMojo {
    /**
     * Skip the execution.
     * 
     * @parameter expression="${skip}" default-value="false"
     * @since 1.0.1
     */
    private boolean skip;

    /**
     * The executable. Can be a full path or a the name executable. In the
     * latter case, the executable must be in the PATH for the execution to
     * work.
     * 
     * @parameter expression="${exec.executable}" default-value=jybot
     * @required
     * @since 1.0
     */
    private String executable;

    /**
     * The current working directory. Optional. If not specified, basedir will
     * be used.
     * 
     * @parameter expression="${exec.workingdir}
     * @since 1.0
     */
    private File workingDirectory;

    /**
     * Program standard and error output will be redirected to the file
     * specified by this optional field. If not specified the standard maven
     * logging is used.
     * 
     * @parameter expression="${exec.outputFile}"
     * @since 1.1-beta-2
     */
    private File outputFile;

    /**
     * Can be of type <code>&lt;argument&gt;</code> or
     * <code>&lt;classpath&gt;</code> Can be overriden using "exec.args" env.
     * variable
     * 
     * @parameter
     * @since 1.0
     */
    private List arguments;

    /**
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     * @since 1.0
     */
    private File basedir;

    /**
     * Environment variables to pass to the executed program.
     * 
     * @parameter
     * @since 1.1-beta-2
     */
    private Map environmentVariables = new HashMap();

//    /**
//     * The current build session instance. This is used for toolchain manager
//     * API calls.
//     * 
//     * @parameter expression="${session}"
//     * @required
//     * @readonly
//     */
//    private MavenSession session;

    /**
     * Exit codes to be resolved as successful execution for non-compliant
     * applications (applications not returning 0 for success).
     * 
     * @parameter
     * @since 1.1.1
     */
    private List successCodes;

    /**
     * if exec.args expression is used when invokign the exec:exec goal, any
     * occurence of %classpath argument is replaced by the actual project
     * dependency classpath.
     */
    public static final String CLASSPATH_TOKEN = "%classpath";

    /**
     * priority in the execute method will be to use System properties arguments
     * over the pom specification.
     * 
     * @throws MojoExecutionException
     *             if a failure happens
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
	if (skip) {
	    getLog().info("skipping execute as per configuraion");
	    return;
	}

	if (basedir == null) {
	    throw new IllegalStateException(
		    "basedir is null. Should not be possible.");
	}

	List commandArguments = new ArrayList();

	if (arguments != null) {
	    for (int i = 0; i < arguments.size(); i++) {
		Object argument = arguments.get(i);
		String arg;
		if (argument == null) {
		    throw new MojoExecutionException(
			    "Misconfigured argument, value is null. Set the argument to an empty value"
				    + " if this is the required behaviour.");
		} else {
		    arg = argument.toString();
		}
		commandArguments.add(arg);
	    }
	}

	Commandline commandLine = new Commandline();

	String args = "";
	for (int i = 0; i < commandArguments.size(); i++) {
	    args += " " + (String) commandArguments.get(i);

	}
	args += " " + "--monitorcolors off -d " + outputDirectory + " "
		+ robotTestDirectory;
	File execFile = createFile(executable, args,
		computeClasspath());
	commandLine.setExecutable(execFile.getPath());

	commandLine.addArguments(new String[] {});

	if (workingDirectory == null) {
	    workingDirectory = basedir;
	}

	if (!workingDirectory.exists()) {
	    getLog().debug(
		    "Making working directory '"
			    + workingDirectory.getAbsolutePath() + "'.");
	    if (!workingDirectory.mkdirs()) {
		throw new MojoExecutionException(
			"Could not make working directory: '"
				+ workingDirectory.getAbsolutePath() + "'");
	    }
	}

	commandLine.setWorkingDirectory(workingDirectory.getAbsolutePath());

	if (environmentVariables != null) {
	    Iterator iter = environmentVariables.keySet().iterator();
	    while (iter.hasNext()) {
		String key = (String) iter.next();
		String value = (String) environmentVariables.get(key);
		commandLine.addEnvironment(key, value);
	    }
	}

	final Log outputLog = getExecOutputLog();

	StreamConsumer stdout = new StreamConsumer() {
	    public void consumeLine(String line) {
		outputLog.info(line);
	    }
	};

	StreamConsumer stderr = new StreamConsumer() {
	    public void consumeLine(String line) {
		outputLog.info(line);
	    }
	};

	try {
	    int resultCode = executeCommandLine(commandLine, stdout, stderr);

	    if (isResultCodeAFailure(resultCode)) {
		throw new MojoFailureException("Result of " + commandLine
			+ " execution is: '" + resultCode + "'.");
	    }
	} catch (CommandLineException e) {
	    throw new MojoExecutionException("Command execution failed.", e);
	}

    }

    private File createFile(String execPath, String args, String asClasspath) {
	try {
	    Platform resolve = Platform.resolve();
	    outputDirectory.mkdirs();
	    outputDirectory.setWritable(true, false);
	    outputDirectory.setReadable(true, false);
	    File cmd = new File(outputDirectory, "robotrunscript"
		    + resolve.postfix());
	    String toBeFlushed = resolve.setEnvironemtVariableCommand(
		    "CLASSPATH", asClasspath)
		    + execPath + " " + args + "\n";

	    FileWriter fileWriter = new FileWriter(cmd);
	    fileWriter.write(toBeFlushed);
	    fileWriter.close();
	    cmd.setExecutable(true, false);
	    cmd.setWritable(true, false);
	    cmd.setReadable(true, false);
	    return cmd;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    boolean isResultCodeAFailure(int result) {
	if (successCodes == null || successCodes.size() == 0)
	    return result != 0;
	for (Iterator it = successCodes.iterator(); it.hasNext();) {
	    int code = Integer.parseInt((String) it.next());
	    if (code == result)
		return false;
	}
	return true;
    }

    private Log getExecOutputLog() {
	Log log = getLog();
	if (outputFile != null) {
	    try {
		if (!outputFile.getParentFile().exists()
			&& !outputFile.getParentFile().mkdirs()) {
		    getLog().warn(
			    "Could not create non existing parent directories for log file: "
				    + outputFile);
		}
		PrintStream stream = new PrintStream(new FileOutputStream(
			outputFile));

		log = new StreamLog(stream);
	    } catch (Exception e) {
		getLog().warn(
			"Could not open " + outputFile + ". Using default log",
			e);
	    }
	}

	return log;
    }

    /**
     * Compute the classpath from the specified Classpath. The computed
     * classpath is based on the classpathScope. The plugin cannot know from
     * maven the phase it is executed in. So we have to depend on the user to
     * tell us he wants the scope in which the plugin is expected to be
     * executed.
     * 
     * @param specifiedClasspath
     *            Non null when the user restricted the dependenceis, null
     *            otherwise (the default classpath will be used)
     * @return a platform specific String representation of the classpath
     */
    private String computeClasspath() {
	// TODO we should consider rewriting this bit into something like
	// List<URL> collectProjectClasspathAsListOfURLs( optionalFilter );
	// reusable by both mojos
	List artifacts = new ArrayList();
	List theClasspathFiles = new ArrayList();

	collectProjectArtifactsAndClasspath(artifacts, theClasspathFiles);

	// if ( specifiedClasspath != null &&
	// specifiedClasspath.getDependencies() != null )
	// {
	// artifacts = filterArtifacts( artifacts,
	// specifiedClasspath.getDependencies() );
	// }

	StringBuffer theClasspath = new StringBuffer();

	for (Iterator it = theClasspathFiles.iterator(); it.hasNext();) {
	    File f = (File) it.next();
	    addToClasspath(theClasspath, f.getAbsolutePath());
	}

	for (Iterator it = artifacts.iterator(); it.hasNext();) {
	    Artifact artifact = (Artifact) it.next();
	    getLog().debug("dealing with " + artifact);
	    addToClasspath(theClasspath, artifact.getFile().getAbsolutePath());
	}

	return theClasspath.toString();
    }

    private static void addToClasspath(StringBuffer theClasspath, String toAdd) {
	if (theClasspath.length() > 0) {
	    theClasspath.append(File.pathSeparator);
	}
	theClasspath.append(toAdd);
    }

//    String getExecutablePath() {
//	File execFile = new File(executable);
//	if (execFile.exists()) {
//	    getLog().debug(
//		    "Toolchains are ignored, 'executable' parameter is set to "
//			    + executable);
//	    return execFile.getAbsolutePath();
//	} else {
//	    throw new RuntimeException("Not found " + executable);
//	    // Toolchain tc = getToolchain();
//	    //            
//	    // // if the file doesn't exist & toolchain is null, the exec is
//	    // probably in the PATH...
//	    // // we should probably also test for isFile and canExecute, but
//	    // the second one is only
//	    // // available in SDK 6.
//	    // if ( tc != null )
//	    // {
//	    // getLog().info( "Toolchain in exec-maven-plugin: " + tc );
//	    // executable = tc.findTool( executable );
//	    // }
//	}
//
//    }

    //
    // methods used for tests purposes - allow mocking and simulate automatic
    // setters
    //

    protected int executeCommandLine(Commandline commandLine,
	    StreamConsumer stream1, StreamConsumer stream2)
	    throws CommandLineException {
	return CommandLineUtils.executeCommandLine(commandLine, stream1,
		stream2);
    }

    void setExecutable(String executable) {
	this.executable = executable;
    }

    String getExecutable() {
	return executable;
    }

    void setWorkingDirectory(String workingDir) {
	setWorkingDirectory(new File(workingDir));
    }

    void setWorkingDirectory(File workingDir) {
	this.workingDirectory = workingDir;
    }

    void setArguments(List arguments) {
	this.arguments = arguments;
    }

    void setBasedir(File basedir) {
	this.basedir = basedir;
    }

    void setProject(MavenProject project) {
	this.project = project;
    }

    protected String getSystemProperty(String key) {
	return System.getProperty(key);
    }

    public void setSuccessCodes(List list) {
	this.successCodes = list;
    }

    public List getSuccessCodes() {
	return successCodes;
    }

}
