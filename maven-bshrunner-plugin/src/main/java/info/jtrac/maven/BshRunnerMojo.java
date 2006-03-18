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
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import bsh.Interpreter;

/**
 * @goal bshrunner
 * @requiresDependencyResolution runtime
 * @description ant helper script generator
 */
public class BshRunnerMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;
    
    /**
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;
    
    /**
     * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
     */
    private ArtifactResolver artifactResolver;    
    
    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     */
    private List remoteArtifactRepositories;
    
    /**
     * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
     */
    private ArtifactFactory artifactFactory;    
    
    /**
     * @component role="org.apache.maven.project.MavenProjectBuilder"
     */
    private MavenProjectBuilder mavenProjectBuilder;  
    
	public void execute() throws MojoExecutionException {
		
		String bshFile = System.getProperty("bsh.file");
		if (bshFile == null) {
			throw new MojoExecutionException("bsh.file system property not set");
		}
		File file = new File(bshFile);
		if (!file.exists()) {
			throw new MojoExecutionException("file does not exist: " + bshFile);
		}
		
		Interpreter i = new Interpreter();
		try {
			i.set("project", project);
			i.set("localRepository", localRepository);
			i.set("artifactResolver", artifactResolver);
			i.set("remoteArtifactRepositories", remoteArtifactRepositories);
			i.set("artifactFactory", artifactFactory);
			i.set("mavenProjectBuilder", mavenProjectBuilder);
			getLog().info("executing beanshell script file: " + bshFile);
			i.source(bshFile);
			getLog().info("beanshell script executed successfully");
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException(e.getLocalizedMessage());
		}
		
	}
	
}
