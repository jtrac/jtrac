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

package info.jtrac.mylyn;

import java.io.File;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * the heart of the mylar connector
 */
public class JtracRepositoryConnector extends AbstractRepositoryConnector {
	
	public static final String UI_LABEL = "JTrac (supports version 2.1 and later)";
	public static final String REPO_TYPE = "jtrac";	
	
	private JtracTaskRepositoryListener taskRepositoryListener;
	private JtracTaskDataHandler taskDataHandler = new JtracTaskDataHandler(this);
	private JtracAttachmentHandler attachmentHandler = new JtracAttachmentHandler(this);
		
	public void stop() {
		if (taskRepositoryListener != null) {
			taskRepositoryListener.writeConfig();
		}
	}	
	
	public synchronized JtracTaskRepositoryListener getTaskRepositoryListener() {
		if (taskRepositoryListener == null) {
			File configFile = null;
			if (JtracPlugin.getDefault().getConfigFilePath() != null) {
				configFile = JtracPlugin.getDefault().getConfigFilePath().toFile();
			}
			taskRepositoryListener = new JtracTaskRepositoryListener(configFile);
		}
		return taskRepositoryListener;
	}

	//=========================================================================
	
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String refId, String summary) {
		// TODO set creation date like trac connector
		return new JtracRepositoryTask(repositoryUrl, refId, summary);		
	}

	@Override
	public AbstractAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public String getConnectorKind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean markStaleTasks(TaskRepository repository,
			Set<AbstractTask> tasks, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query,
			TaskRepository repository, IProgressMonitor monitor,
			ITaskCollector resultCollector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository,
			AbstractTask repositoryTask, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository,
			AbstractTask repositoryTask, RepositoryTaskData taskData) {
		// TODO Auto-generated method stub
		
	}
	
}
