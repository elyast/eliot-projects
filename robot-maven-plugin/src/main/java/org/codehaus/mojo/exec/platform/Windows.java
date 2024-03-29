/* Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.mojo.exec.platform;


/**
 * @author Lasse Koskela
 */
public class Windows extends Platform {

    public String getName() {
        return "Windows";
    }


    public String postfix() {
        return ".bat";
    }

	public String setEnvironemtVariableCommand(String var, String value) {
		return "set " + var + "=%" + var+"%;"+value + "\n";
	}
}
