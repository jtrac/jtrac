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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * the heart of the mylar connector
 */
public class JtracRepositoryConnector extends AbstractRepositoryConnector {
	
	private final static String UI_LABEL = "JTrac";
	private final static String REPO_TYPE = "jtrac";
	
	private JtracTaskRepositoryListener taskRepositoryListener;

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return false;
	}

	@Override
	public AbstractRepositoryTask createTaskFromExistingKey(TaskRepository repository, String id) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		return UI_LABEL;
	}

	@Override
	public String getRepositoryType() {
		return REPO_TYPE;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSupportedVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query,
			TaskRepository repository, IProgressMonitor monitor,
			QueryHitCollector resultCollector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTask(TaskRepository repository,
			AbstractRepositoryTask repositoryTask) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() {
		if (taskRepositoryListener != null) {
			taskRepositoryListener.writeConfig();
		}
	}	
	
	public JtracTaskRepositoryListener getTaskRepositoryListener() {
		return taskRepositoryListener;
	}

}
