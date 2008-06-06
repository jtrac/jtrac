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

import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.exception.JtracSecurityException;
import org.apache.wicket.PageParameters;

/**
 * item list page
 */
public class ItemListPage extends BasePage {        
        
    public ItemListPage(PageParameters params) throws JtracSecurityException {        
        long spaceId = params.getLong("s", -1);
        User user = JtracSession.get().getUser();
        ItemSearch itemSearch = null;
        if(spaceId > 0) {            
            Space space = JtracApplication.get().getJtrac().loadSpace(spaceId);
            if(!user.isAllocatedToSpace(space.getId())) {
                throw new JtracSecurityException("User not allocated to space: " + space.getId() + " in URL: " + params);
            }
            itemSearch = new ItemSearch(space);
            JtracSession.get().setCurrentSpace(space);
        } else {
            itemSearch = new ItemSearch(user);
        }
        itemSearch.initFromPageParameters(params, JtracApplication.get().getJtrac());
        JtracSession.get().setItemSearch(itemSearch);
        addComponents(itemSearch);
    }   
    
    public ItemListPage(ItemSearch itemSearch) {
        addComponents(itemSearch);
    }
    
    private void addComponents(ItemSearch itemSearch) {        
        add(new ItemListPanel("panel", itemSearch));
        add(new ItemRelatePanel("relate", false, itemSearch));        
    }
    
}
