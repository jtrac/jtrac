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
import info.jtrac.domain.User;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.link.Link;

/**
 * dashboard page
 */
public class ItemViewPage extends BasePage {
      
    public ItemViewPage(Item tempItem, final ItemListPage previous) {
        
        super("Item View");      
        
        add(new HeaderPanel(null));
                
        final Item item = getJtrac().loadItem(tempItem.getId());
        
        Link link = new Link("back") {
            public void onClick() {
                previous.setSelectedItemId(item.getId());
                setResponsePage(previous);
            }
        };

        if(previous == null) {
            link.setVisible(false);
        }
        
        border.add(link);
        
        border.add(new ItemViewPanel("itemViewPanel", item));
        
        User user = getPrincipal();
        
        if(user.getId() > 0) {        
            border.add(new ItemViewFormPanel("itemViewFormPanel", item, previous));
        } else {
            border.add(new WebMarkupContainer("itemViewFormPanel").setVisible(false));
        }
        
    }
    
}
