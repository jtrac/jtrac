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

import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.User;
import info.jtrac.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

/**
 * dashboard page
 */
public class DashboardPage extends BasePage {
      
    public DashboardPage() {
        
        super("Dashboard");
        
        User user = SecurityUtils.getPrincipal();
        CountsHolder countsHolder = getJtrac().loadCountsForUser(user);        
        List<Counts> countsList = new ArrayList<Counts>(countsHolder.getCounts().values());                    
        
        border.add(new ListView("dashboard", countsList) {
            protected void populateItem(final ListItem listItem) {
                Counts counts = (Counts) listItem.getModelObject();
                DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", counts);
                listItem.add(dashboardRow);
            }
        });
        
        DashboardTotalPanel panel = new DashboardTotalPanel("total", countsHolder);
        if(countsList.size() == 1) {
            panel.setVisible(false);
        }
        border.add(panel);      
        
    }
    
}
