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

import info.jtrac.mylar.JtracRepositoryQuery;
import info.jtrac.mylar.ui.JtracUiPlugin;

import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.tasks.ui.search.SearchHitCollector;
import org.eclipse.ui.forms.editor.FormEditor;

public class JtracNewRepositoryTaskEditor extends AbstractNewRepositoryTaskEditor {

	public JtracNewRepositoryTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	protected SearchHitCollector getDuplicateSearchCollector(String description) {
		JtracRepositoryQuery query = new JtracRepositoryQuery("JTrac Query", 
				TasksUiPlugin.getTaskListManager().getTaskList());
		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, query);
		return collector;
	}

	@Override
	protected String getPluginId() {
		return JtracUiPlugin.PLUGIN_ID;
	}

}
