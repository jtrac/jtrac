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
import wicket.markup.html.WebPage;

/**
 * special variant of search page without header and footer
 */
public class ItemSearchModalPage extends WebPage {        
        
    public ItemSearchModalPage() {
        setVersioned(false);
        Space space = ((JtracSession) getSession()).getCurrentSpace();
        if (space != null) {
            add(new ItemSearchFormPanel("panel", space));
        } else {
            User user = ((JtracSession) getSession()).getUser();   
            add(new ItemSearchFormPanel("panel", user));
        }
    }       
    
    /**
     * here we are returning to the filter criteria screen from
     * the search results screen
     */
    public ItemSearchModalPage(ItemSearch itemSearch) {
        setVersioned(false);
        itemSearch.setCurrentPage(0);        
        add(new ItemSearchFormPanel("panel", itemSearch));      
    }    

    
}
