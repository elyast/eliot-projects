package org.codehaus.mojo.deploy;

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

import org.apache.maven.plugin.AbstractMojo;

/**
 * This class is used for unifying functionality between the 2 mojo exec plugins
 * ('java' and 'exec'). It handles parsing the arguments and adding source/test
 * folders.
 * 
 * @author Philippe Jacot (PJA)
 * @author Jerome Lacoste
 */
public abstract class AbstractSpringDmDeployMojo extends AbstractMojo {

	/**
	 * This folder is added to the list of those folders containing source to be
	 * compiled. Use this if your plugin generates source code.
	 * 
	 * @parameter 
	 *            expression="${project.build.directory}/${project.build.finalName}.jar"
	 * @required
	 */
	File bundlePath;

	/**
	 * This folder is added to the list of those folders containing source to be
	 * compiled. Use this if your plugin generates source code.
	 * 
	 * @parameter expression="${project.artifactId}"
	 * @required
	 */
	String artifactName;

	/**
	 * The directory where Sprindm home is.
	 * 
	 * @parameter
	 * @required
	 */
	File springDmHome;

}
