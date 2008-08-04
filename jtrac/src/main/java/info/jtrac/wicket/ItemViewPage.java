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
        
    private long itemId;

    public long getItemId() {
        return itemId;
    }    
    
    public ItemViewPage(PageParameters params) {        
        String refId = params.getString("0");
        logger.debug("item id parsed from url = '" + refId + "'");
        Item item;
        if(refId.indexOf('-') != -1) { 
            // this in the form SPACE-123
            item = getJtrac().loadItemByRefId(refId);
        } else {
            // internal id of type long
            item = getJtrac().loadItem(Long.parseLong(refId));
        }
        itemId = item.getId(); // required for itemRelatePanel
        addComponents(item);
    }  
    
    private void addComponents(final Item item) {  
        final ItemSearch itemSearch = JtracSession.get().getItemSearch();
        add(new ItemRelatePanel("relate", true, itemSearch));        
        Link link = new Link("back") {
            public void onClick() {
                itemSearch.setSelectedItemId(item.getId());
                if(itemSearch.getRefId() != null) {
                     // user had entered item id directly, go back to search page
                     setResponsePage(new ItemSearchFormPage(itemSearch));
                } else {
                     setResponsePage(new ItemListPage(itemSearch));
                }
            }
        };
        if(itemSearch == null) {
            link.setVisible(false);
        }
        
        add(link);
        
        boolean isRelate = itemSearch != null && itemSearch.getRelatingItemRefId() != null;
        
        User user = getPrincipal();
        
        if(!user.isAllocatedToSpace(item.getSpace().getId())) {
            logger.debug("user is not allocated to space");
            throw new RestartResponseAtInterceptPageException(ErrorPage.class);
        }                
        
        add(new Link("edit") {
            public void onClick() {
                setResponsePage(new ItemFormPage(item.getId()));
            }
        }.setVisible(user.isSuperUser() || user.isAdminForSpace(item.getSpace().getId())));                        
        
        add(new ItemViewPanel("itemViewPanel", item, isRelate || user.getId() == 0));
        
        if(user.isGuestForSpace(item.getSpace()) || isRelate) {        
            add(new WebMarkupContainer("itemViewFormPanel").setVisible(false));
        } else {            
            add(new ItemViewFormPanel("itemViewFormPanel", item, itemSearch));
        }        
    }
    
}
