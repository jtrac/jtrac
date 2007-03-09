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
        this.previous = previous;
        Item item = getJtrac().loadItem(itemId);
        addComponents(item);
    }
    
    // specially for the item edit scenario, to avoid un-necessary re-load
    public ItemViewPage(Item item, final ItemListPage previous) { 
        this.previous = previous;        
        addComponents(item);
    }    
    
    private void addComponents(final Item item) {                            
        
        Link link = new Link("back") {
            public void onClick() {
                previous.setSelectedItemId(item.getId());
                setResponsePage(previous);
            }
        };
        if(previous == null) {
            link.setVisible(false);
        }
        
        add(link);
                        
        User user = getPrincipal();
        
        add(new Link("edit") {
            public void onClick() {
                // reload from database, avoid OptimisticLockingFailure
                Item temp = getJtrac().loadItem(item.getId());
                setResponsePage(new ItemFormPage(temp, ItemViewPage.this));
            }
        }.setVisible(user.isAdminForAllSpaces()));        
        
        add(new ItemViewPanel("itemViewPanel", item));
        
        if(user.getId() > 0) {        
            add(new ItemViewFormPanel("itemViewFormPanel", item, previous));
        } else {
            add(new WebMarkupContainer("itemViewFormPanel").setVisible(false));
        }        
    }
    
}
