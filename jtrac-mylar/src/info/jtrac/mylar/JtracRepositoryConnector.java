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

import info.jtrac.mylar.util.ExceptionUtils;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * the heart of the mylar connector
 */
public class JtracRepositoryConnector extends AbstractRepositoryConnector {
	
	public static final String UI_LABEL = "JTrac (supports version 2.1 and later)";
	public static final String REPO_TYPE = "jtrac";
	private static final String URI_APP_ITEM = "/app/item/";
	
	private JtracTaskRepositoryListener taskRepositoryListener;
	private JtracTaskDataHandler taskDataHandler = new JtracTaskDataHandler(this);
	private JtracAttachmentHandler attachmentHandler = new JtracAttachmentHandler(this);
	
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public AbstractRepositoryTask createTaskFromExistingKey(TaskRepository repository, String id) throws CoreException {
		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), id);
		JtracRepositoryTask task;
		ITask existingTask = taskList.getTask(handle);
		if (existingTask instanceof JtracRepositoryTask) {
			task = (JtracRepositoryTask) existingTask;
		} else {
			RepositoryTaskData taskData = taskDataHandler.getTaskData(repository, id);
			task = new JtracRepositoryTask(handle, taskData.getLabel(), true);
			task.setTaskData(taskData);
			taskList.addTask(task);
		}
		return task;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
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
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(URI_APP_ITEM);
		return index == -1 ? null : url.substring(0, index);
	}

	@Override
	public List<String> getSupportedVersions() {
		return null;
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(URI_APP_ITEM);
		return index == -1 ? null : url.substring(index + URI_APP_ITEM.length());
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + URI_APP_ITEM + taskId;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor, QueryHitCollector resultCollector) {
		// JtracClient client = taskRepositoryListener.getClient(repository);
		try {
			resultCollector.accept(new JtracQueryHit(taskList, query.getRepositoryUrl(), "Test Description", "TEST-123"));
		} catch (Exception e) {
			return ExceptionUtils.toStatus(e);
		}
		return Status.OK_STATUS;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTask(TaskRepository repository, AbstractRepositoryTask repositoryTask) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
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

}
