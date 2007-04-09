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

import wicket.Component;
import wicket.ajax.AjaxRequestTarget;
import wicket.behavior.HeaderContributor;
import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.IHeaderResponse;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;

/**
 * custom wicketized yahoo ui dialog widget
 */
public class YuiDialog extends Panel {                
        
    public static String CONTENT_ID = "content";        
    private WebMarkupContainer dialog;        
    
    public YuiDialog(String id, String heading) {
        super(id);        
        setOutputMarkupId(true);  // for Wicket Ajax
        dialog = new WebMarkupContainer("dialog"); 
        dialog.setOutputMarkupId(true); // for Yahoo Dialog 
        dialog.setVisible(false);
        add(dialog);                        
        dialog.add(new Label("heading", heading));        
        dialog.add(new WebMarkupContainer(CONTENT_ID));      
    }         
    
    public void show(AjaxRequestTarget target, Component content) {                             
        target.addComponent(this); 
        dialog.setVisible(true);        
        dialog.replace(content);        
        final String markupId = dialog.getMarkupId();
        // using the contributor and the onDomReady Wicket helper handles the rare case that
        // the dialog is visible and the user refreshes the backing page (possible as dialog is not modal!)
        // so in that special case, this javascript is called at page load
        // but in the usual Ajax request case, this behaves just like AjaxRequestTarget.appendJavascript()
        HeaderContributor contributor = new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response) {
                response.renderOnDomReadyJavascript("var " + markupId + " = new YAHOO.widget.Dialog('" + markupId + "'); " 
                + markupId + ".render(); " + markupId + ".show();");
            }
        });
        add(contributor);
    }
    
}
