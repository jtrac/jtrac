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

import java.util.Arrays;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

/**
 * reusable confirm / warning dialog page
 */
public abstract class ConfirmPage extends BasePage {      
        
    private String warning;
    private String[] lines;
    private WebPage back; 
    
    public ConfirmPage(WebPage back, String heading, String warning, String[] lines) {              
        this.back = back;
        this.warning = warning;
        this.lines = lines;        
        add(new Label("heading", heading));
        add(new ConfirmForm("form"));                
    }
    
    public abstract void onConfirm();
    
    private class ConfirmForm extends Form {              
        
        public ConfirmForm(String id) {
            super(id);
            ListView listView = new ListView("lines", Arrays.asList(lines)) {
                protected void populateItem(ListItem listItem) {
                    String line = (String) listItem.getModelObject();
                    listItem.add(new Label("line", line));
                }                
            };
            add(listView);
            add(new Label("warning", warning));
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(back);
                }
            });
        }
        
        @Override
        protected void onSubmit() {
            onConfirm();
        }          
        
    }
    
}
