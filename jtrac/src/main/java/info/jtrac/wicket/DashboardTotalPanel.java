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

import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.User;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.model.PropertyModel;

/**
 * panel for showing the totals row on the dashboard
 * this will be visible only if user has more than one space assigned
 */
public class DashboardTotalPanel extends BasePanel {    
    
    public DashboardTotalPanel(String id, final CountsHolder countsHolder, final User user) {
        
        super(id);    
        
        add(new Link("search") {
            public void onClick() {
                setResponsePage(new ItemSearchFormPage(user));
            }
        });       
        
        add(new Label("loggedByMe", new PropertyModel(countsHolder, "totalLoggedByMe")));
        add(new Label("assignedToMe", new PropertyModel(countsHolder, "totalAssignedToMe")));
        add(new Label("total", new PropertyModel(countsHolder, "totalTotal")));
      
    }
    
}
