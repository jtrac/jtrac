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

import info.jtrac.mylar.JtracRepositoryQuery;
import info.jtrac.mylar.JtracRepositoryTask;

import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylar.tasks.core.ITask;
import org.w3c.dom.Node;

public class JtracTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_JTRAC = "JTrac";
	private static final String KEY_JTRAC_CATEGORY = KEY_JTRAC + KEY_CATEGORY;
	private static final String KEY_JTRAC_TASK = KEY_JTRAC + KEY_TASK;
	private static final String KEY_JTRAC_QUERY_HIT = KEY_JTRAC + KEY_QUERY_HIT;
	private static final String KEY_JTRAC_QUERY = KEY_JTRAC + KEY_QUERY;	
	
	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(KEY_JTRAC_CATEGORY);
	}	
	
	@Override
	public String getCategoryTagName() {
		return KEY_JTRAC_CATEGORY;
	}	
	
	@Override
	public boolean canCreateElementFor(ITask task) {
		return task instanceof JtracRepositoryTask;
	}
	
	@Override
	public String getTaskTagName() {
		return KEY_JTRAC_TASK;
	}
	
	@Override
	public String getQueryHitTagName() {
		return KEY_JTRAC_QUERY_HIT;
	}	
	
	@Override
	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		if (query instanceof JtracRepositoryQuery) {
			return KEY_JTRAC_QUERY;
		}
		return "";
	}	
	
}
