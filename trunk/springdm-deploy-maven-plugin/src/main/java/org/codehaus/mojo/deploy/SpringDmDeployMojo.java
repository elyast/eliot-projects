package org.codehaus.mojo.deploy;

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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * A Plugin for executing external programs.
 * 
 * @author Jerome Lacoste <jerome@coffeebreaks.org>
 * @version $Id: ExecMojo.java 8882 2009-01-22 20:47:34Z lacostej $
 * @goal exec
 * @requiresDependencyResolution test
 * @since 1.0
 */
public class SpringDmDeployMojo extends AbstractSpringDmDeployMojo {

	FileChecker checker = new FileChecker();
	
	public void setChecker(FileChecker checker) {
		this.checker = checker;
	}
	/**
	 * priority in the execute method will be to use System properties arguments
	 * over the pom specification.
	 * 
	 * @throws MojoExecutionException
	 *             if a failure happens
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info(this.artifactName);
		File springDmPickup = new File(springDmHome, "pickup");
		if (!springDmPickup.exists() || !springDmPickup.isDirectory()) {
			throw new MojoExecutionException("Spring dm home doesn't exist or is not a dir: " + springDmPickup);
		}
		if (!bundlePath.exists()) {
			getLog().info("OSGi bundle not available to copy..");
			return;
		}
		File[] artifacts = findArtifacts(springDmPickup, artifactName);
		if (artifacts.length == 1) {
			getLog().info("Removed old instance " + artifacts[0] );
			artifacts[0].delete();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				throw new MojoFailureException(e.getMessage());
			}			
		}
		if (artifacts.length > 1) {
			throw new MojoExecutionException("Too many artifacts: " + Arrays.toString(artifacts));
		}
		try {
			org.codehaus.plexus.util.FileUtils.copyFileToDirectory(bundlePath, springDmPickup);
			getLog().info("Copied file to dir: " + bundlePath + " -> " + springDmPickup);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	public File[] findArtifacts(File dir, final String string) {
		return dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {				
				String name = pathname.getName();								
				boolean sameArtifactName = name.startsWith(string);
				if (!sameArtifactName) {
					return false;
				}
				String version = name.substring(string.length());
				
				boolean result = checker.isFile(pathname) && sameArtifactName && isVersionOnly(version);
				return result;
			}
		});
	}
	protected boolean isVersionOnly(String version) {
		String[] tokens = version.split("\\-");
		boolean isSnapshot = tokens[tokens.length-1].startsWith("SNAPSHOT");
		int lastVersionIndex = tokens.length-1;
		if (isSnapshot) {
			lastVersionIndex--;
		}
		return version.startsWith("-") && tokens[lastVersionIndex-1].isEmpty() ;
	}

}
