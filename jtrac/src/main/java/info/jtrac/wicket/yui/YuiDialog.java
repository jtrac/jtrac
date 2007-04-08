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
    private boolean shown;
    
    public YuiDialog(String id, String heading, Component body) {
        super(id);
        setOutputMarkupId(true);        
        dialog = new WebMarkupContainer("dialog");
        dialog.setRenderBodyOnly(true);
        dialog.setVisible(false);
        add(dialog);
        dialog.add(new Label("heading", heading));        
        dialog.add(body);
        HeaderContributor contributor = new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response) {
                String markupId = YuiDialog.this.getMarkupId();
                response.renderJavascript("var " + markupId + ";", null);
            }
        });        
        add(contributor);        
    }         

    public void show(AjaxRequestTarget target) {        
        String markupId = getMarkupId();        
        if(!shown) {
            target.addComponent(this);
            target.appendJavascript(markupId + " = new YAHOO.widget.Dialog('" + markupId + "'); " + markupId + ".render();");
            dialog.setVisible(true);
            shown = true;
        }
        target.appendJavascript(markupId + ".show();");
    }
    
}
