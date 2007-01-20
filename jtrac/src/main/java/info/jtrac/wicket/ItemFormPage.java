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

import info.jtrac.domain.Item;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.RequiredTextField;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.CompoundPropertyModel;
import wicket.model.PropertyModel;

/**
 * Create / Edit item form page
 */
public class ItemFormPage extends BasePage {
    
    public ItemFormPage(Item item) {
        super("Edit Item");
        border.add(new FeedbackPanel("feedback"));        
        border.add(new ItemForm("form", item));        
    }
    
    private class ItemForm extends Form {
        
        public ItemForm(String id, Item item) {
            super(id, new CompoundPropertyModel(item));
            add(new RequiredTextField("summary"));
            add(new TextArea("detail", new PropertyModel(item, "detail")));
        }
        
        @Override
        protected void onSubmit() {
            info("the form was submitted");
        }
        
    }
    
}
