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

import info.jtrac.mylyn.JtracRepositoryTask;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.w3c.dom.Element;

// TODO rename to TaskListFactory
public class JtracTaskExternalizer extends AbstractTaskListFactory {

	private static final String KEY_JTRAC = "JTrac";	
	private static final String KEY_JTRAC_TASK = KEY_JTRAC + KEY_TASK;	
	private static final String KEY_JTRAC_QUERY = KEY_JTRAC + KEY_QUERY;
	
	@Override
	public boolean canCreate(AbstractTask task) {
		return task instanceof JtracRepositoryTask;
	}
	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId,
			String label, Element element) {
		return new JtracRepositoryTask(repositoryUrl, taskId, label);
	}
	@Override
	public String getTaskElementName() {
		return KEY_JTRAC_TASK;
	}	
	
	
	
}
