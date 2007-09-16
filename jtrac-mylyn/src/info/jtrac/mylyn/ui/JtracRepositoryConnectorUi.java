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

package info.jtrac.mylyn.ui;

import info.jtrac.mylyn.JtracRepositoryConnector;
import info.jtrac.mylyn.ui.wizard.JtracNewTaskWizard;
import info.jtrac.mylyn.ui.wizard.JtracQueryWizard;
import info.jtrac.mylyn.ui.wizard.JtracRepositorySettingsPage;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;

/**
 * main plugin entry point
 */
public class JtracRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public String getConnectorKind() {
		return JtracRepositoryConnector.REPO_TYPE;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return new JtracNewTaskWizard(taskRepository);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery queryToEdit) {
		return new JtracQueryWizard();
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new JtracRepositorySettingsPage(this);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}



}
