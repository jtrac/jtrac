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

package info.jtrac.mylyn.ui.editor;

import info.jtrac.mylyn.JtracRepositoryConnector;
import info.jtrac.mylyn.JtracRepositoryTask;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public class JtracTaskEditorFactory extends AbstractTaskEditorFactory {

	@Override
	public boolean canCreateEditorFor(AbstractTask task) {
		return task instanceof JtracRepositoryTask;
	}

	@Override
	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			return existingInput.getTaskData() != null 
				&& JtracRepositoryConnector.REPO_TYPE.equals(existingInput.getRepository().getConnectorKind());
		} else if (input instanceof TaskEditorInput) {
			TaskEditorInput taskInput = (TaskEditorInput) input;
			return taskInput.getTask() instanceof JtracRepositoryTask;
		}
		return false;
	}

	@Override
	public IEditorPart createEditor(TaskEditor parentEditor,
			IEditorInput editorInput) {
		// TODO look at trac
		if (editorInput instanceof RepositoryTaskEditorInput  || editorInput instanceof TaskEditorInput) {
			return new JtracRepositoryTaskEditor(parentEditor);
		}
		if (editorInput instanceof TaskEditorInput) {
			// for item that does not yet exist on the server
			return new JtracNewRepositoryTaskEditor(parentEditor);
		}		
		return null;
	}

	@Override
	public IEditorInput createEditorInput(AbstractTask task) {
		JtracRepositoryTask repositoryTask = (JtracRepositoryTask) task;
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				JtracRepositoryConnector.REPO_TYPE, repositoryTask.getRepositoryUrl());		
		return new RepositoryTaskEditorInput(repository, 
				repositoryTask.getHandleIdentifier(), repositoryTask.getUrl());
	}

	@Override
	public String getTitle() {
		return "JTrac";
	}

}
