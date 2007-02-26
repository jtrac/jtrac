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
import wicket.PageParameters;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.link.Link;

/**
 * dashboard page
 */
public class ItemViewPage extends BasePage {
          
    ItemListPage previous;

    public ItemListPage getPrevious() {
        return previous;
    }    
    
    public ItemViewPage(PageParameters params) {
        super("Item View");        
        String itemId = params.getString("0");
        logger.debug("item id parsed from url = '" + itemId + "'");
        Item item;
        if(itemId.indexOf('-') != -1) { 
            // this in the form SPACE-123
            item = getJtrac().loadItemByRefId(itemId);
        } else {
            // internal id of type long
            item = getJtrac().loadItem(Long.parseLong(itemId));
        }
        addComponents(item);
    }
    
    public ItemViewPage(long itemId, final ItemListPage previous) {
        super("Item View");  
        this.previous = previous;
        final Item item = getJtrac().loadItem(itemId);
        addComponents(item);
    }
    
    private void addComponents(final Item item) {
        
        add(new HeaderPanel(null));                
        
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
                        
        User user = getPrincipal();
        
        border.add(new Link("edit") {
            public void onClick() {
                setResponsePage(new ItemFormPage(item, ItemViewPage.this));
            }
        }.setVisible(user.isAdminForAllSpaces()));        
        
        border.add(new ItemViewPanel("itemViewPanel", item));
        
        if(user.getId() > 0) {        
            border.add(new ItemViewFormPanel("itemViewFormPanel", item, previous));
        } else {
            border.add(new WebMarkupContainer("itemViewFormPanel").setVisible(false));
        }        
    }
    
}
