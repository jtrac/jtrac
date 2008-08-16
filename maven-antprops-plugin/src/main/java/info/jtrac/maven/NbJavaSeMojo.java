/*
 * Copyright 2002-2005 the original author or authors.
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

package info.jtrac.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

/**
 * @goal nbjavase
 * @requiresDependencyResolution runtime 
 */
public class NbJavaSeMojo extends NbBaseMojo {
	
	protected void generate() throws Exception {
		File nbProjDir = new File("nbproject");
		if(!nbProjDir.exists()) {
			nbProjDir.mkdir();
		}
		File nbProjPropsFile = new File("nbproject/project.properties");
		if(!nbProjPropsFile.exists()) {
			String projectName = project.getArtifactId();
			String propsSource = FileUtils.readFile(getClass(), "project-javase.properties").toString();
			String propsTarget = propsSource.replace("@@project.name@@", projectName);
			propsTarget = propsTarget + getFileReferences(false);
			FileUtils.writeFile(propsTarget, "nbproject/project.properties", false);
			getLog().info("created file 'nbproject/project.properties'");
			String projectSource = FileUtils.readFile(getClass(), "project-javase.xml").toString();						
			String projectTarget = projectSource.replace("@@project.name@@", projectName);
			FileUtils.writeFile(projectTarget, "nbproject/project.xml", false);
			getLog().info("created file 'nbproject/project.xml'");
		} else {
			getLog().info("'nbproject/project.properties' already exists, modifying contents");
			String contents = FileUtils.readFile("nbproject/project.properties").toString();
			contents = contents.replaceAll("\\njavac\\.classpath.*(\\n\\s+.*)*", "");
			contents = contents.replaceAll("\\nfile\\.reference.*", "");
			contents = contents.replaceAll("\\nm2\\.repo.*", "");
			contents = contents.replaceAll("\\n+", "\n");
			contents = contents + getFileReferences(false);
			FileUtils.writeFile(contents, "nbproject/project.properties", false);
		}
	}
	
}
