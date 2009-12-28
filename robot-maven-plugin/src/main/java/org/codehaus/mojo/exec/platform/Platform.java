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
public abstract class Platform {


    public static Platform resolve() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") != -1) {
            return new Windows();
        } else {
            return new Unix();
        }
    }
    public abstract String postfix();
    public abstract String setEnvironemtVariableCommand(String var, String value);
}
