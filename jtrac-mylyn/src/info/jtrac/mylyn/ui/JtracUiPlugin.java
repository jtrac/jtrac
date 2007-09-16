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

import info.jtrac.mylyn.JtracPlugin;

import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class JtracUiPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "info.jtrac.mylyn.ui";
	
	private static JtracUiPlugin uiPlugin;
	
	public JtracUiPlugin() {
		uiPlugin = this;
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		JtracPlugin plugin = new JtracPlugin();  // this has to be refactored into headless later
		plugin.start(context);
		TasksUiPlugin.getRepositoryManager().addListener(JtracPlugin.getDefault().getConnector().getTaskRepositoryListener());
	}	
	
	@Override
	public void stop(BundleContext context) throws Exception {
		TasksUiPlugin.getRepositoryManager().removeListener(JtracPlugin.getDefault().getConnector().getTaskRepositoryListener());		
		uiPlugin = null;
		super.stop(context);
		JtracPlugin.getDefault().stop(context); // this has to be refactored into headless later
	}	
	
	public static JtracUiPlugin getDefault() {
		return uiPlugin;
	}	

}
