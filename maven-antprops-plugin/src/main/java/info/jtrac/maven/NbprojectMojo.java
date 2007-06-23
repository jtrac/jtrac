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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * @goal nbproject
 * @description netbeans project files generator
 */
public class NbprojectMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 */
	protected MavenProject project;

	public void execute() {
		String projectName = project.getArtifactId();
		String buildSource = readFile("build.xml").toString();
		getLog().info("finding and replacing project name: " + projectName);
		String buildTarget = buildSource.replace("@@project.name@@", projectName);
		writeFile(buildTarget, "build.xml", false);
		String projectSource = readFile("project.xml").toString();
		String projectTarget = projectSource.replace("@@project.name@@", projectName);
		File nbproject = new File("nbproject");
		nbproject.mkdir();		
		writeFile(projectTarget, "nbproject/project.xml", false);
		File etc = new File("etc");
		etc.mkdir();
		String jettyXml = readFile("jetty.xml").toString();
		writeFile(jettyXml, "etc/jetty.xml", false);
		String webdefaultXml = readFile("webdefault.xml").toString();
		writeFile(webdefaultXml, "etc/webdefault.xml", false);
	}
	
	private StringBuffer readFile(String fileName) {
		InputStream is = getClass().getResourceAsStream(fileName);
		BufferedReader buffer = null;
		StringBuffer sb = new StringBuffer();
		String s = null;
		try {
			try {
				buffer = new BufferedReader(new InputStreamReader(is));
				while ((s = buffer.readLine()) != null) {
					sb.append(s).append('\n');
				}
			} finally {
				is.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb;
	}	
	
	private void writeFile(String content, String fileName, boolean append) {
		FileWriter writer = null;
		try {
			try {
				writer = new FileWriter(fileName, append);
				writer.write(content);
			} finally {
				writer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
