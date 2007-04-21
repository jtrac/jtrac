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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * custom wicketized yahoo ui dialog widget
 */
public class YuiDialog extends Panel {                
        
    public static final String CONTENT_ID = "content";        
    private WebMarkupContainer dialog;
    private String heading;    

    public String getHeading() {
        return heading;
    }
    
    public void setHeading(String heading) {
        this.heading = heading;
    }
    
    public YuiDialog(String id) {
        super(id);         
        
        add(HeaderContributor.forJavaScript("resources/yui/yahoo/yahoo-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/event/event-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/dom/dom-min.js"));  
        add(HeaderContributor.forJavaScript("resources/yui/dragdrop/dragdrop-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/container/container-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/container/resize-dialog.js"));
        add(HeaderContributor.forCss("resources/yui/container/assets/container.css")); 
        
        setOutputMarkupId(true);  // for Wicket Ajax
        dialog = new WebMarkupContainer("dialog"); 
        dialog.setOutputMarkupId(true); // for Yahoo Dialog 
        dialog.setVisible(false);
        add(dialog);                        
        dialog.add(new Label("heading", new PropertyModel(this, "heading")));        
        dialog.add(new WebMarkupContainer(CONTENT_ID));      
    }         
    
    public void show(AjaxRequestTarget target, String heading, Component content) {
        this.heading = heading;
        target.addComponent(this); 
        dialog.setVisible(true);        
        dialog.replace(content);        
        final String markupId = dialog.getMarkupId();
        // using the contributor and the onDomReady Wicket helper handles the rare case that
        // the dialog is visible and the user refreshes the backing page (possible as dialog is not modal!)
        // so in that special case, this javascript is called at page load
        // but in the usual Ajax request case, this behaves just like AjaxRequestTarget.appendJavascript()
        add(new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response) {
                response.renderOnDomReadyJavascript("var " + markupId + " = new YAHOO.widget.ResizeDialog('" + markupId + "', " 
                + " { constraintoviewport : true }); " 
                + markupId + ".render(); " + markupId + ".show();");
            }
        }));
    }    
    
}
