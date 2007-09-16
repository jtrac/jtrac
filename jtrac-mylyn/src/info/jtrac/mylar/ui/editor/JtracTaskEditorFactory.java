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

package info.jtrac.mylar.ui.editor;

import info.jtrac.mylar.JtracRepositoryConnector;
import info.jtrac.mylar.JtracRepositoryTask;

import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.ITaskEditorFactory;
import org.eclipse.mylar.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylar.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.mylar.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public class JtracTaskEditorFactory implements ITaskEditorFactory {

	public boolean canCreateEditorFor(ITask task) {
		return task instanceof JtracRepositoryTask;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			return existingInput.getTaskData() != null 
				&& JtracRepositoryConnector.REPO_TYPE.equals(existingInput.getRepository().getKind());
		} else if (input instanceof NewTaskEditorInput) {
			NewTaskEditorInput newInput = (NewTaskEditorInput) input;
			return newInput.getTaskData() != null
				&& JtracRepositoryConnector.REPO_TYPE.equals(newInput.getRepository().getKind());
		}
		return false;
	}

	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		if (editorInput instanceof RepositoryTaskEditorInput  || editorInput instanceof TaskEditorInput) {
			return new JtracRepositoryTaskEditor(parentEditor);
		}
		if (editorInput instanceof NewTaskEditorInput) {
			// for item that does not yet exist on the server
			return new JtracNewRepositoryTaskEditor(parentEditor);
		}		
		return null;
	}

	public IEditorInput createEditorInput(ITask task) {
		JtracRepositoryTask repositoryTask = (JtracRepositoryTask) task;
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				JtracRepositoryConnector.REPO_TYPE, repositoryTask.getRepositoryUrl());		
		return new RepositoryTaskEditorInput(repository, 
				repositoryTask.getHandleIdentifier(), repositoryTask.getUrl());
	}

	public String getTitle() {
		return "JTrac";
	}

	public boolean providesOutline() {
		return true;
	}

}
