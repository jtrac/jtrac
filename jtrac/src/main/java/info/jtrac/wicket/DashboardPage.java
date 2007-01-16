package info.jtrac.wicket;

import java.util.ArrayList;
import java.util.List;
import wicket.AttributeModifier;
import wicket.Component;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.Model;

public class DashboardPage extends BasePage {
      
    public DashboardPage() {
        
        super("Dashboard");
        
        List<String[]> data = new ArrayList<String[]>();
        
        data.add(new String[]{ "foo", "bar", "baz"});
        data.add(new String[]{ "goo", "gar", "gaz"});
        data.add(new String[]{ "hoo", "har", "haz"});
        
        border.add(new ListView("dashboard", data) {
            protected void populateItem(final ListItem listItem) {
                String[] cols = (String[]) listItem.getModelObject();
                DashboardRowPanel a = new DashboardRowPanel("dashboardCollapsed", cols);               
                DashboardRowPanel b = new DashboardRowPanel("dashboardExpanded", cols);
                a.setOutputMarkupId(true);
                b.setOutputMarkupId(true);
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
