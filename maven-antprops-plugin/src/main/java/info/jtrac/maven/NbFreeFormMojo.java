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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @goal nbfreeform
 * @requiresDependencyResolution runtime
 * @description netbeans free form project generator
 */
public class NbFreeFormMojo extends GenerateMojo {

	public void generate() throws MojoExecutionException {
		try {
			super.generate();
		} catch(Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage());
		}
		File nbProjectDir = new File("nbproject");
		if(!nbProjectDir.exists()) {
			nbProjectDir.mkdir();
		}
		if(FileUtils.exists("nbproject/ide-targets.xml")) {
			throw new MojoExecutionException("NetBeans project file 'nbproject/ide-targets.xml' already exists");
		}
		String ideTargets = FileUtils.readFile(getClass(), "ide-targets.xml").toString();
		FileUtils.writeFile(ideTargets, "nbproject/ide-targets.xml", false);
		getLog().info("created 'nbproject/ide-targets.xml'");
		if(FileUtils.exists("nbproject/project.xml")) {
			throw new MojoExecutionException("NetBeans project file 'nbproject/project.xml' already exists");
		}
		String projectName = project.getArtifactId();
		String projectSource = FileUtils.readFile(getClass(), "project-freeform.xml").toString();
		String projectTarget = projectSource.replace("@@project.name@@", projectName);
		FileUtils.writeFile(projectTarget, "nbproject/project.xml", false);
		getLog().info("created 'nbproject/project.xml'");
	}

}
