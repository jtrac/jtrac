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

package info.jtrac.mylyn;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * JTrac Mylar Connector core plugin class, headless
 */
public class JtracPlugin extends Plugin {

	public static final String PLUGIN_ID = "info.jtrac.mylar";
	
	private static JtracPlugin plugin;	
	private JtracRepositoryConnector connector;	
	
	public JtracRepositoryConnector getConnector() {
		return connector;
	}

	public void setConnector(JtracRepositoryConnector connector) {
		this.connector = connector;
	}

	public static JtracPlugin getDefault() {
		return plugin;
	}	

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		connector = new JtracRepositoryConnector();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (connector != null) {
			connector.stop();
			connector = null;
		}		
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the path of the local config file used for storing repository details
	 */
	protected IPath getConfigFilePath() {
		IPath stateLocation = Platform.getStateLocation(JtracPlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("repositories.xml");
		return configFile;
	}	

}
