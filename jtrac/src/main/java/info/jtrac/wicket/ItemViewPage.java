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
import info.jtrac.domain.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;

/**
 * dashboard page
 */
public class ItemViewPage extends BasePage {              
    
    ItemSearch itemSearch;
    long itemId;

    public long getItemId() {
        return itemId;
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
    
    public ItemViewPage(long itemId, ItemSearch itemSearch) { 
        this.itemSearch = itemSearch;
        this.itemId = itemId;
        Item item = getJtrac().loadItem(itemId);
        addComponents(item);
    }
    
    // for e.g. the item edit scenario, to avoid un-necessary re-load
    public ItemViewPage(Item item, ItemSearch itemSearch) { 
        this.itemSearch = itemSearch;
        this.itemId = item.getId();
        addComponents(item);
    }    
    
    private void addComponents(final Item item) {                            
        
        add(new ItemRelatePanel("relate", itemSearch, true));
        
        Link link = new Link("back") {
            public void onClick() {
                itemSearch.setSelectedItemId(item.getId());
                setResponsePage(new ItemListPage(itemSearch));
            }
        };
        if(itemSearch == null) {
            link.setVisible(false);
        }
        
        add(link);
        
        boolean isRelate = itemSearch != null && itemSearch.getRelatingItemRefId() != null;
        
        User user = getPrincipal();
        
        if(!user.getSpaces().contains(item.getSpace())) {
            logger.debug("user is not allocated to space");
            throw new RestartResponseAtInterceptPageException(ErrorPage.class);
        }
        
        add(new Link("edit") {
            public void onClick() {
                // reload from database, avoid OptimisticLockingFailure
                Item temp = getJtrac().loadItem(item.getId());
                setResponsePage(new ItemFormPage(temp, itemSearch));
            }
        }.setVisible(user.isAdminForAllSpaces()));                        
        
        add(new ItemViewPanel("itemViewPanel", item, isRelate || user.getId() == 0));
        
        if(user.getId() > 0 && !isRelate) {        
            add(new ItemViewFormPanel("itemViewFormPanel", item, itemSearch));
        } else {
            add(new WebMarkupContainer("itemViewFormPanel").setVisible(false));
        }        
    }
    
}
