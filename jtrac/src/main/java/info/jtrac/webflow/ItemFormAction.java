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

package info.jtrac.webflow;

import info.jtrac.domain.Item;
import info.jtrac.domain.Space;
import info.jtrac.util.ValidationUtils;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;

/**
 * Multiaction that backs the "Item Create / Edit" flow
 */
public class ItemFormAction extends AbstractFormAction {
    
    public ItemFormAction() {
        setFormObjectClass(Item.class);
        setFormObjectName("item");
        setFormObjectScope(ScopeType.FLOW);
    }
    
    @Override
    public Object loadFormObject(RequestContext context) {
        String itemId = ValidationUtils.getParameter(context, "itemId");
        if (itemId != null) {
            return jtrac.loadItem(Long.parseLong(itemId));
        } else {
            Item item = new Item();
            String spaceId = ValidationUtils.getParameter(context, "spaceId");
            Space space = jtrac.loadSpace(Integer.parseInt(spaceId));
            item.setSpace(space);
            return item;
        }
    }    
    
    public Event itemFormHandler(RequestContext context) throws Exception {
        Item item = (Item) getFormObject(context);
        Space space = (Space) context.getFlowScope().get("space");
        item.setSpace(space);
        jtrac.storeItem(item);
        return success();
    }        
    
}
