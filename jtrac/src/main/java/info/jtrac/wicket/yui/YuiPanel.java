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

package info.jtrac.wicket.yui;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * custom wicketized yahoo ui panel widget
 */
public class YuiPanel extends Panel {                
        
    public static final String CONTENT_ID = "content";    
    
    private WebMarkupContainer dialog;
    
    public YuiPanel(String id, String heading, Component content) {
        super(id);        
        add(HeaderContributor.forJavaScript("resources/yui/yahoo/yahoo-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/event/event-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/dom/dom-min.js"));  
        add(HeaderContributor.forJavaScript("resources/yui/dragdrop/dragdrop-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/container/container-min.js"));        
        add(HeaderContributor.forCss("resources/yui/container/assets/container.css")); 
                
        dialog = new WebMarkupContainer("dialog"); 
        dialog.setOutputMarkupId(true);       
        add(dialog);                        
        dialog.add(new Label("heading", heading));        
        dialog.add(content); 
        add(new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response) {
                String markupId = dialog.getMarkupId();
                response.renderJavascriptReference("var " + markupId + ";");
                response.renderOnDomReadyJavascript(markupId + " = new YAHOO.widget.Panel('" + markupId + "', " 
                + " { constraintoviewport : true, visible : false }); " + markupId + ".render()");                
            }
        }));        
    }         
    
    public String getShowScript() {                
        return dialog.getMarkupId() + ".show();";
    }        
    
}
