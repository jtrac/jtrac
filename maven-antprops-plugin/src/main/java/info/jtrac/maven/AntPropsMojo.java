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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

/**
 * @goal antprops
 * @requiresDependencyResolution runtime
 * @description ant build properties file generator
 */
public class AntPropsMojo extends AbstractMojo {

	//======================== MOJO ===============================
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
    
    //======================== PRIVATE ===============================
    
    private Properties props;
	private Map buildProperties = new LinkedHashMap();
	private Map buildClassPaths = new LinkedHashMap();
	private Set runtimeFiles;  
	private String repoBaseDir;
    
    //========================== MAIN ================================
    
	public void execute() throws MojoExecutionException {
		repoBaseDir = localRepository.getBasedir().replace('\\','/');
		try {
			buildProperties.put("m2.repo", repoBaseDir);
			//========================================================
			Collection runtimeArtifacts = project.getArtifacts();
			runtimeFiles = getRelativePaths(getFiles(runtimeArtifacts), repoBaseDir);
			//========================================================
			Set testArtifacts = project.getDependencyArtifacts();
			testArtifacts.addAll(project.getTestArtifacts());
			Collection testFiles = getFiles(testArtifacts);
			Set testPaths = getRelativePaths(testFiles, repoBaseDir);
			buildClassPaths.put("test.jars", testPaths);
			//========================================================
			writeAntPropsFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException(e.getLocalizedMessage());
		}
	}
	
	//========================== HELPER METHODS ======================
	
	/**
	 * instantiate a single artifact using Maven, on the fly
	 */
	private Artifact getArtifact(String groupId, String artifactId, String version) {
	    return artifactFactory.createArtifact(groupId, artifactId, version, "", "jar");      
	}

	/**
	 * resolve dependencies for the given artifact details using Maven, on the fly
	 */
	private Collection resolveDependencies(String groupId, String artifactId, String version) throws Exception {
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
	private File resolveArtifact(Artifact artifact) throws Exception {
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
	private Collection getFiles(Collection artifacts) throws Exception {
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
	private Collection getFiles(String groupId, String artifactId, String version) throws Exception {
	    return getFiles(resolveDependencies(groupId, artifactId, version));
	}	
	
	/**
	 * function to return relative path given base path and the target file
	 */
	private String getRelativePath(File file, String basePath) {
	    String p = basePath.replace('\\','/');
	    int len = p.length() + 1;
	    if (p.endsWith("/")) {
	    	len--;
	    }
	    return file.getPath().substring(len).replace('\\','/');
	}
	
	/**
	 * add path entries for the given bunch of files
	 */
	private Set getRelativePaths(Collection files, String basePath) {
		Set paths = new TreeSet();
		Iterator i = files.iterator();
		while (i.hasNext()) {
			File f = (File) i.next();
			String path = getRelativePath(f, basePath);
			paths.add(path);
		}
		return paths;
	}	
	
	/**
	 * load properties from given file
	 */
	private void loadPropertiesFromFile(String fileName) throws Exception {		
		InputStream is = new FileInputStream(fileName);
		props = new Properties();
		props.load(is);		
		is.close();
	}
	
	/**
	 * get property, file has to have been loaded before calling
	 */
	private String getProperty(String key) {
		return props.getProperty(key);
	}
	
	/**
	 * generate properties file with dependency information, ready to use by ant
	 */
	private void writeAntPropsFile() throws Exception {
		OutputStream os = new FileOutputStream("build-deps.properties");
		Writer out = new PrintWriter(os);
		Date date = new Date();
		out.write("# *** generated by the AntProps Maven2 plugin: " + date + " ***\n\n");
		//==========================================================		
		for (Iterator i = buildProperties.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			out.write(entry.getKey() + "=" + entry.getValue() + "\n\n");
		}
		//===========================================================
		out.write("runtime.jars=");	
		for (Iterator i = runtimeFiles.iterator(); i.hasNext(); ) {			
			String path = (String) i.next();
			out.write("\\\n    " + path + ",");
		}
		out.write("\n\n");
		//===========================================================
		for (Iterator i = buildClassPaths.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			out.write(entry.getKey() + "=");
			Set paths = (Set) entry.getValue();
			for (Iterator j = paths.iterator(); j.hasNext(); ) {
				String path = (String) j.next();
				out.write("\\\n    ${m2.repo}/" + path + ":");
			}
			out.write("\n\n");
		}
		out.close();
		os.close();
	}
	
}
