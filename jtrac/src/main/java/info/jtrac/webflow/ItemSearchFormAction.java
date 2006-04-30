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

import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import info.jtrac.util.UserEditor;
import info.jtrac.util.ValidationUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;

/**
 * Multiaction that backs the "Item Search" flow
 */
public class ItemSearchFormAction extends AbstractFormAction {
    
    public ItemSearchFormAction() {
        setFormObjectClass(ItemSearch.class);
        setFormObjectName("itemSearch");
        setFormObjectScope(ScopeType.FLOW);
    }
    
    @Override
    protected void initBinder(RequestContext request, DataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(User.class, new UserEditor(jtrac));       
    }     
    
    @Override
    public Object loadFormObject(RequestContext context) {
        String spaceId = ValidationUtils.getParameter(context, "spaceId");
        Space space = jtrac.loadSpace(Integer.parseInt(spaceId));            
        context.getFlowScope().put("space", space);
        List<UserRole> userRoles = jtrac.findUsersForSpace(space.getId());
        context.getFlowScope().put("userRoles", userRoles);        
        return new ItemSearch(space);
    }      
    
    public Event itemSearchFormHandler(RequestContext context) throws Exception {
        ItemSearch itemSearch = (ItemSearch) getFormObject(context);
        context.getRequestScope().put("items", jtrac.findItems(itemSearch));
        return success();
    }        
    
}
