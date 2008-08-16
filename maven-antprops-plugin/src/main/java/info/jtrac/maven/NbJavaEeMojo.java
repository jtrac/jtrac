package info.jtrac.maven;

import java.io.File;
import java.util.Iterator;

/**
 * @goal nbjavaee
 * @requiresDependencyResolution runtime 
 */
public class NbJavaEeMojo extends NbBaseMojo {
	
	private StringBuffer getWarFileReferences() {
		StringBuffer sb = new StringBuffer();
		sb.append("<web-module-additional-libraries>\n");
		for(Iterator i = runtimeFiles.iterator(); i.hasNext(); ) {
			String path = (String) i.next();
			String fileReference = path.substring(path.lastIndexOf('/') + 1);
			sb.append("                <library files=\"1\">\n");
			sb.append("                    <file>${file.reference." + fileReference + "}</file>\n");
			sb.append("                    <path-in-war>WEB-INF/lib</path-in-war>\n");
			sb.append("                </library>\n");
		}
		sb.append("            </web-module-additional-libraries>");
		return sb;
	}
	
	protected void generate() throws Exception {
		File nbProjDir = new File("nbproject");
		if(!nbProjDir.exists()) {
			nbProjDir.mkdir();
		}
		File nbProjPropsFile = new File("nbproject/project.properties");
		if(!nbProjPropsFile.exists()) {
			String projectName = project.getArtifactId();
			String propsSource = FileUtils.readFile(getClass(), "project-javaee.properties").toString();
			String propsTarget = propsSource.replace("@@project.name@@", projectName);
			propsTarget = propsTarget + getFileReferences(true);
			FileUtils.writeFile(propsTarget, "nbproject/project.properties", false);
			getLog().info("created file 'nbproject/project.properties'");
			String projectSource = FileUtils.readFile(getClass(), "project-javaee.xml").toString();						
			String projectTarget = projectSource.replace("@@project.name@@", projectName);
			projectTarget = projectTarget.replace("<web-module-additional-libraries/>", getWarFileReferences());
			FileUtils.writeFile(projectTarget, "nbproject/project.xml", false);
			getLog().info("created file 'nbproject/project.xml'");
		} else {
			getLog().info("'nbproject/project.properties' already exists, modifying contents");
			String propsContents = FileUtils.readFile("nbproject/project.properties").toString();
			propsContents = propsContents.replaceAll("\\njavac\\.classpath.*(\\n\\s+.*)*", "");
			propsContents = propsContents.replaceAll("\\nwar\\.content\\.additional.*(\\n\\s+.*)*", "");
			propsContents = propsContents.replaceAll("\\nfile\\.reference.*", "");
			propsContents = propsContents.replaceAll("\\nm2\\.repo.*", "");
			propsContents = propsContents.replaceAll("\\n+", "\n");
			propsContents = propsContents + getFileReferences(true);
			FileUtils.writeFile(propsContents, "nbproject/project.properties", false);
			getLog().info("'nbproject/project.xml' already exists, modifying contents");
			String xmlContents = FileUtils.readFile("nbproject/project.xml").toString();
			String regex = "<web-module-additional-libraries>(.*\n.*)</web-module-additional-libraries>";
			xmlContents = xmlContents.replace(regex, getWarFileReferences());
			FileUtils.writeFile(xmlContents, "nbproject/project.xml", false);
		}
	}	

}
