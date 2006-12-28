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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JtracNewTaskPage extends WizardPage {
	
	private TaskRepository taskRepository;
	private Combo spacesCombo;
		
	protected JtracNewTaskPage(TaskRepository taskRepository) {
		super("JTrac: New Task");		
		setDescription("Select the Space in which to create a new task.");
		this.taskRepository = taskRepository;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		Label label = new Label(composite, SWT.NONE);
		label.setText("Choose Space:");		
		spacesCombo = new Combo(composite, SWT.READ_ONLY);
		spacesCombo.setItems (new String [] {"TEST", "FOO", "BAR"});
		setControl(composite);

	}
	
	public String getSelectedSpaceKey() {
		return spacesCombo.getText();
	}

}
