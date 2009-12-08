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

package info.jtrac.wicket;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;


/**
 * header navigation
 */
public class IndividualHeadPanel extends BasePanel {    
    
    /**
	 * Default serialVersionID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public IndividualHeadPanel() {
        super("individuel");
        
        final Map<String, String> configMap = getJtrac().loadAllConfig();
		Image img= new Image( "icon");
		img.add(new AttributeModifier("src", true, new AbstractReadOnlyModel() {
			private static final long serialVersionUID = 1L;
			public final Object getObject() {
				// based on some condition return the image source
				String url = configMap.get("jtrac.header.picture");
				if ((url == null) ||("".equals(url)))
					return "../resources/jtrac-logo.gif";
				else
  				    return url;
			}
		}));
		add(img);
		String message = configMap.get("jtrac.header.text");
		if ((message == null) ||("".equals(message)))
   		    add(new Label("message", "JTrac - Open Source Issue Tracking System"));
		else if ((message != null) && ("no".equals(message)))
   		    add(new Label("message", ""));
		else
			add(new Label("message", message));
    }
}
