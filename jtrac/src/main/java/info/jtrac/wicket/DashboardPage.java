package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.User;
import info.jtrac.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.AttributeModifier;
import wicket.Component;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.Model;

public class DashboardPage extends BasePage {
      
    public DashboardPage() {
        
        super("Dashboard");
        
        User user = SecurityUtils.getPrincipal();
        CountsHolder countsHolder = getJtrac().loadCountsForUser(user);        
        List<Counts> countsList = new ArrayList<Counts>(countsHolder.getCounts().size());        
        for(Map.Entry<Long, Counts> entry : countsHolder.getCounts().entrySet()) {
            countsList.add(entry.getValue());
        }               
        
        border.add(new ListView("dashboard", countsList) {
            protected void populateItem(final ListItem listItem) {
                Counts counts = (Counts) listItem.getModelObject();
                DashboardRowPanel a = new DashboardRowPanel("dashboardCollapsed", counts);               
                DashboardRowPanel b = new DashboardRowPanel("dashboardExpanded", counts);
                b.add(new AttributeModifier("style", true, new Model(){
                    public Object getObject(Component c) {
                        return "display:none";
                    }
                }));
                b.setOther(a);
                a.setOther(b);
                listItem.add(a);
                listItem.add(b);
            }
        });        
    }
    
}
