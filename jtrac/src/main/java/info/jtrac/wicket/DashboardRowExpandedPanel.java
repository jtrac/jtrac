package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

public class DashboardRowExpandedPanel extends BasePanel {    
    
    public DashboardRowExpandedPanel(String id, final Counts counts) {        
        
        super(id);
        setOutputMarkupId(true);    

        SimpleAttributeModifier sam = new SimpleAttributeModifier("rowspan", counts.getTotalMap().size() + "");
        
        Map<Integer, String> states = new TreeMap(counts.getSpace().getMetadata().getStates());    
        states.remove(State.NEW);        
        List<Integer> stateKeys = new ArrayList<Integer>(states.keySet());
        
        int first = stateKeys.get(0);
        
        add(new Label("space", "SPACE").add(sam));
        add(new Label("new", "NEW").add(sam));
        add(new Label("search", "SEARCH").add(sam));
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                DashboardRowPanel a = new DashboardRowPanel("dashboardRow", counts);
                DashboardRowExpandedPanel.this.replaceWith(a);
                target.addComponent(a);
            }
        }.add(sam));
        
        add(new Label("status", first + ""));
        add(new Label("loggedByMe", counts.getLoggedByMeMap().get(first) + ""));
        add(new Label("assignedToMe", counts.getAssignedToMeMap().get(first) + ""));
        add(new Label("total", counts.getTotalMap().get(first) + ""));                      
        
        stateKeys.remove(0);
        
        add(new ListView("rows", stateKeys) {
            protected void populateItem(ListItem listItem) {
                Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", i + ""));
                listItem.add(new Label("loggedByMe", counts.getLoggedByMeMap().get(i) + ""));
                listItem.add(new Label("assignedToMe", counts.getAssignedToMeMap().get(i) + ""));
                listItem.add(new Label("total", counts.getTotalMap().get(i) + ""));                
            }
            
        });
        
    }
    
}
