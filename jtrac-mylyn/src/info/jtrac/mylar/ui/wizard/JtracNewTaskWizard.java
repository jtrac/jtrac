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

package info.jtrac.mylar.ui.wizard;

import info.jtrac.mylar.JtracPlugin;
import info.jtrac.mylar.JtracRepositoryConnector;
import info.jtrac.mylar.JtracTaskDataHandler;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.mylar.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class JtracNewTaskWizard extends Wizard implements INewWizard {

	private TaskRepository taskRepository;
	private JtracNewTaskPage newTaskPage;
		
	public JtracNewTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
		setWindowTitle("JTrac: New Task Wizard");
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}	
	
	@Override
	public void addPages() {
		newTaskPage = new JtracNewTaskPage(taskRepository);
		newTaskPage.setWizard(this);
		addPage(newTaskPage);
	}	
	
	@Override
	public boolean canFinish() {
		return true;
	}	
	
	@Override
	public boolean performFinish() {
		JtracRepositoryConnector connector = JtracPlugin.getDefault().getConnector();
		JtracTaskDataHandler taskDataHandler = (JtracTaskDataHandler) connector.getTaskDataHandler();
		RepositoryTaskData repositoryTaskData = new RepositoryTaskData(taskDataHandler.getAttributeFactory(), 
				JtracRepositoryConnector.REPO_TYPE, taskRepository.getUrl(), 
				TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
		NewTaskEditorInput editorInput = new NewTaskEditorInput(taskRepository, repositoryTaskData);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TasksUiUtil.openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
		return true;
	}


}
