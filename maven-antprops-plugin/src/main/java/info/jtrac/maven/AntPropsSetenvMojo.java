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
