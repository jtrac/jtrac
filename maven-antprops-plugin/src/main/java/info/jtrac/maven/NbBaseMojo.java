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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class NbBaseMojo extends AntPropsMojo {
	
	protected StringBuffer getFileReferences(boolean isForWebProject) {
		StringBuffer sb = new StringBuffer();
		sb.append("m2.repo=" + buildProperties.get("m2.repo") + "\n\n");
		Set fileReferences = new TreeSet();		
		for (Iterator i = testClassPaths.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();			
			String key = (String) entry.getKey();
			if(!key.equals("m2.repo")) {
				continue;
			}
			Set paths = (Set) entry.getValue();
			for (Iterator j = paths.iterator(); j.hasNext(); ) {
				String path = (String) j.next();				
				String fileReference = path.substring(path.lastIndexOf('/') + 1);
				sb.append("file.reference." + fileReference + "=${" + key + "}/" + path + "\n");
				fileReferences.add(fileReference);
			}
		}
		sb.append("\n");
		//===============================================================
		sb.append("javac.classpath=");
		for (Iterator i = fileReferences.iterator(); i.hasNext(); ) {
			String fileReference = (String) i.next();
			sb.append("\\\n    ${file.reference." + fileReference + "}:");
		}
		sb.append("\n");
		if(isForWebProject) {
			sb.append("\n");
			sb.append("war.content.additional=");
			for(Iterator i = runtimeFiles.iterator(); i.hasNext(); ) {
				String path = (String) i.next();
				String fileReference = path.substring(path.lastIndexOf('/') + 1);
				sb.append("\\\n    ${file.reference." + fileReference + "}:");
			}
			sb.append("\n");
		}
		return sb;
	}

}
