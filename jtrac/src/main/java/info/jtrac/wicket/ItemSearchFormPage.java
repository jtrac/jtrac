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
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import java.util.List;
import wicket.markup.html.form.Form;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * dashboard page
 */
public class ItemSearchFormPage extends BasePage {
      
    public ItemSearchFormPage(Space space) {
        
        super("Item Search");
        border.add(new FeedbackPanel("feedback"));
        border.add(new ItemSearchForm("form", space));
        
    }

    private class ItemSearchForm extends Form {
        
        public ItemSearchForm(String id, Space space) {
            super(id);
            ItemSearch itemSearch = new ItemSearch(space);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(itemSearch);
            setModel(model);
        }
        
        @Override
        protected void onSubmit() {
            ItemSearch itemSearch = (ItemSearch) getModelObject();
            List<Item> items = getJtrac().findItems(itemSearch);
            setResponsePage(new ItemListPage(items, itemSearch));
        }        
            
    }
    
}
