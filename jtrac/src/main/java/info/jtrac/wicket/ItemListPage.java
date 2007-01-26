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
import java.util.List;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * dashboard page
 */
public class ItemListPage extends BasePage {
      
    public ItemListPage(List<Item> items) {
        
        super("Item Search Results");
        
        ListView listView = new ListView("itemList", items) {
            protected void populateItem(ListItem listItem) {
                Item item = (Item) listItem.getModelObject();
                listItem.add(new Label("refId", new PropertyModel(item, "refId")));
                listItem.add(new Label("summary", new PropertyModel(item, "summary")));                
                listItem.add(new Label("loggedBy", new PropertyModel(item, "loggedBy.name")));
                listItem.add(new Label("status", new PropertyModel(item, "statusValue")));
                listItem.add(new Label("assignedTo", new PropertyModel(item, "assignedTo.name")));
                listItem.add(new Label("timeStamp", new PropertyModel(item, "timeStamp")));
            }            
        };
        
        border.add(listView);        
        
    }

    
}
