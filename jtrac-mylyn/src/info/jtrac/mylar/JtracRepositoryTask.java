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

package info.jtrac.mylar;

import org.eclipse.mylyn.tasks.core.AbstractTask;

// TODO rename to JtracTask
public class JtracRepositoryTask extends AbstractTask {

	private static final String URI_APP_ITEM = "/app/item/";
	
	public JtracRepositoryTask(String repositoryUrl, String refId, String label) {
		super(repositoryUrl, refId, label);
		setUrl(repositoryUrl + URI_APP_ITEM + refId);
	}	

	@Override
	public String getConnectorKind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}
	

	
}
