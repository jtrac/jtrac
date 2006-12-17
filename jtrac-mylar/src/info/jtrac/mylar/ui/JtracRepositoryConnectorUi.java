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

package info.jtrac.mylar.ui;

import info.jtrac.mylar.JtracRepositoryConnector;
import info.jtrac.mylar.ui.wizard.JtracRepositorySettingsPage;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.wizards.AbstractRepositorySettingsPage;

/**
 * main plugin entry point
 */
public class JtracRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public String getRepositoryType() {
		return JtracRepositoryConnector.REPO_TYPE;
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new JtracRepositorySettingsPage(this);
	}

	@Override
	public boolean hasRichEditor() {
		return true;
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository,
			AbstractRepositoryQuery queryToEdit) {
		// TODO Auto-generated method stub
		return null;
	}

}
