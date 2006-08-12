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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @goal setenv
 * @description load properties files before AntPropsMojo execution
 */
public class AntPropsSetenvMojo extends AbstractMojo {
	
    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;	

	public void execute() throws MojoExecutionException {
		getLog().info("executing antprops:setup");
		InputStream is = null;
		try {
			try {
				is = new FileInputStream("build.properties");
				Properties props = new Properties();
				props.load(is);
				Properties mavenProps = project.getProperties();
				for (Iterator i = props.entrySet().iterator(); i.hasNext(); ) {
					Map.Entry entry = (Map.Entry) i.next();					
					mavenProps.put((String) entry.getKey(), (String) entry.getValue());
				}
			} finally {
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException(e.getLocalizedMessage());			
		}
	}

}
