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

public class DashboardPage extends BasePage {
      
    public DashboardPage() {
        
        super("Dashboard");
        
        User user = SecurityUtils.getPrincipal();
        CountsHolder countsHolder = getJtrac().loadCountsForUser(user);        
        List<Counts> countsList = new ArrayList<Counts>(countsHolder.getCounts().values());                    
        
        border.add(new ListView("dashboard", countsList) {
            protected void populateItem(final ListItem listItem) {
                Counts counts = (Counts) listItem.getModelObject();
                DashboardRowPanel a = new DashboardRowPanel("dashboardRow", counts);
                listItem.add(a);
            }
        });        
    }
    
}
