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
 * @description netbeans free form project generator
 */
public class NbFreeFormMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 */
	protected MavenProject project;

	public void execute() throws MojoExecutionException {
		if(FileUtils.exists("build.xml")) {
			throw new MojoExecutionException("build.xml already exists");
		}
		if(FileUtils.exists("nbproject/project.xml")) {
			throw new MojoExecutionException("NetBeans project file 'nbproject/project.xml' already exists");
		}
		String projectName = project.getArtifactId();
		String projectNameTitleCase = Character.toUpperCase(projectName.charAt(0)) + projectName.substring(1);
		String buildSource = FileUtils.readFile(getClass(), "build.xml").toString();		
		String buildTarget = buildSource.replace("@@project.name@@", projectName);
		buildTarget = buildTarget.replace("@@project.name.titleCase@@", projectNameTitleCase);
		FileUtils.writeFile(buildTarget, "build.xml", false);
		getLog().info("created 'build.xml'");
		String projectSource = FileUtils.readFile(getClass(), "project-freeform.xml").toString();
		String projectTarget = projectSource.replace("@@project.name@@", projectName);
		File nbproject = new File("nbproject");
		nbproject.mkdir();
		FileUtils.writeFile(projectTarget, "nbproject/project.xml", false);
		getLog().info("created 'nbproject/project.xml'");
		File etc = new File("etc");
		etc.mkdir();
		String jettyXml = FileUtils.readFile(getClass(), "jetty.xml").toString();
		FileUtils.writeFile(jettyXml, "etc/jetty.xml", false);
		getLog().info("created 'etc/jetty.xml'");
		String webdefaultXml = FileUtils.readFile(getClass(), "webdefault.xml").toString();
		FileUtils.writeFile(webdefaultXml, "etc/webdefault.xml", false);
		getLog().info("created 'etc/webdefault.xml'");
	}

}
