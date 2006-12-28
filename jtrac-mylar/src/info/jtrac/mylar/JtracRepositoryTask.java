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

import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

public class JtracRepositoryTask extends AbstractRepositoryTask {

	private static final String URI_APP_ITEM = "/app/item/";
	
	public JtracRepositoryTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
	}

	@Override
	public String getRepositoryKind() {
		return JtracRepositoryConnector.REPO_TYPE;
	}
	
	@Override
	public String getIdLabel() {
		return getRefIdFromHandle(handleIdentifier);
	}
	
	public static String getHandleForRefId(String repositoryUrl, String refId) {
		return repositoryUrl + URI_APP_ITEM + refId;
	}
	
	public static String getRefIdFromHandle(String handle) {
		if (handle == null) {
			return null;
		}
		int index = handle.lastIndexOf(URI_APP_ITEM);
		return index == -1 ? null : handle.substring(index + URI_APP_ITEM.length());		
	}
	
	public static String getRepositoryUrlFromHandle(String handle) {
		if (handle == null) {
			return null;
		}
		int index = handle.lastIndexOf(URI_APP_ITEM);
		return index == -1 ? null : handle.substring(0, index);		
	}
	
}
