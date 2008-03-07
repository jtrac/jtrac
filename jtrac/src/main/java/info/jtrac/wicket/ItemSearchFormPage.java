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

/**
 * item search form page
 */
public class ItemSearchFormPage extends BasePage {
            
    public ItemSearchFormPage() {
        ItemSearch itemSearch = null;
        Space s = getCurrentSpace();
        if(s == null) {
            itemSearch = new ItemSearch(JtracSession.get().getUser());
        } else {
            itemSearch = new ItemSearch(s);
        }
        addComponents(itemSearch);
    }
    
    public ItemSearchFormPage(ItemSearch itemSearch) {                        
        addComponents(itemSearch);
    }    
    
    private void addComponents(ItemSearch itemSearch) {
        add(new ItemSearchFormPanel("panel", itemSearch));        
        add(new ItemRelatePanel("relate", false, itemSearch));        
    }
    
}
