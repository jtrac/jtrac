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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
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
     * @parameter
     */
    private String file;

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

    //  ========================== MAIN ========================
    
	public void execute() throws MojoExecutionException {

		String temp = System.getProperty("bsh.file");

		if (temp != null) {
			file = temp;
		}

		if (file == null) {
			throw new MojoExecutionException("no file specified in plugin config and bsh.file system property not set");
		}

		File f = new File(file);

		if (!f.exists()) {
			throw new MojoExecutionException("file does not exist: " + f);
		}

		Interpreter i = new Interpreter();

		try {
			i.set("mvn", this);
			i.set("project", project);
			getLog().info("executing beanshell script file: " + f);
			i.source(file);
			getLog().info("beanshell script executed successfully");
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException(e.getLocalizedMessage());
		}

	}
	
	//========================== HELPER METHODS ======================
	
	/**
	 * get the repository base folder converting to a java friendly path
	 */
	public String getBaseDir() {
		return localRepository.getBasedir().replace('\\','/');
	}	
	
	/**
	 * instantiate a single artifact using Maven, on the fly
	 */
	public Artifact getArtifact(String groupId, String artifactId, String version) {
	    return artifactFactory.createArtifact(groupId, artifactId, version, "", "jar");      
	}

	/**
	 * resolve dependencies for the given artifact details using Maven, on the fly
	 */
	public Collection resolveDependencies(String groupId, String artifactId, String version) throws Exception {
	    Artifact pomArtifact = getArtifact(groupId, artifactId, version); 
	    MavenProject pomProject = mavenProjectBuilder.buildFromRepository(pomArtifact, remoteArtifactRepositories, localRepository);    
	    Collection artifacts = pomProject.createArtifacts(artifactFactory, Artifact.SCOPE_TEST, new ScopeArtifactFilter(Artifact.SCOPE_TEST));
	    Iterator i = artifacts.iterator();
	    while(i.hasNext()) {
	    	Artifact a = (Artifact) i.next();
	        resolveArtifact(a);
	    }
	    artifacts.add(pomArtifact);
	    return artifacts;      
	}

	/**
	 * resolve single artifact to file, and resolve fully from repository if required
	 */
	public File resolveArtifact(Artifact artifact) throws Exception {
	    File f = artifact.getFile();
	    if (f != null) {
	    	return f;
	    }
	    getLog().info("resolving artifact: " + artifact);
	    artifactResolver.resolve(artifact, remoteArtifactRepositories, localRepository);
	    return artifact.getFile();    
	}	
	
	/**
	 * convert a collection of maven artifacts into a collection of files
	 */
	public Collection getFiles(Collection artifacts) throws Exception {
	    Collection files = new ArrayList();
	    Iterator i = artifacts.iterator();
	    while(i.hasNext()) {
	    	Artifact a = (Artifact) i.next();
	        files.add(resolveArtifact(a));
	    }
	    return files;
	}
	
	/**
	 * convenience combination of resolving and getting a bunch of files
	 */
	public Collection getFiles(String groupId, String artifactId, String version) throws Exception {
	    return getFiles(resolveDependencies(groupId, artifactId, version));
	}	
	
	/**
	 * function to return relative path given base path and the target file
	 */
	public String getRelativePath(File file, String basePath) {
	    String p = basePath.replace('\\','/');
	    int len = p.length() + 1;
	    if (p.endsWith("/")) {
	    	len--;
	    }
	    return file.getPath().substring(len).replace('\\','/');
	}
	
	/**
	 * generate path entries for the given bunch of files
	 */
	public String getPaths(Collection files, String basePath, String baseKey) {
		StringBuffer sb = new StringBuffer();
		Iterator i = files.iterator();
		while (i.hasNext()) {
			File f = (File) i.next();
			String p = getRelativePath(f, basePath);
			sb.append("\\\n    ");
			if (baseKey == null) {
				sb.append(p + ",");
			} else {
				sb.append(baseKey + "/" + p + ":");
			}
		}
		return sb.toString();
	}
	
}
