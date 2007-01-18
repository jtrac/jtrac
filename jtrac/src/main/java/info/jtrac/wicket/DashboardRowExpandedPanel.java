package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

public class DashboardRowExpandedPanel extends BasePanel {    
    
    public DashboardRowExpandedPanel(String id, final Counts counts, Space space) {
        
        super(id);
        setOutputMarkupId(true);
        
        SimpleAttributeModifier sam = new SimpleAttributeModifier("rowspan", counts.getTotalMap().size() + "");
        
        Map<Integer, String> states = new TreeMap(space.getMetadata().getStates());
        states.remove(State.NEW);        
        List<Integer> stateKeys = new ArrayList<Integer>(states.keySet());
        
        int first = stateKeys.get(0);
        
        add(new Label("space", "SPACE").add(sam));
        add(new Label("new", "NEW").add(sam));
        add(new Label("search", "SEARCH").add(sam));
        add(new Label("pink", "(-)").add(sam));                
        add(new Label("status", "XXX"));
        add(new Label("loggedByMe", counts.getLoggedByMeMap().get(first) + ""));
        add(new Label("assignedToMe", counts.getAssignedToMeMap().get(first) + ""));
        add(new Label("total", counts.getTotalMap().get(first) + ""));                      
        
        stateKeys.remove(0);
        
        add(new ListView("rows", stateKeys) {
            protected void populateItem(ListItem listItem) {
                Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", listItem.getIndex() + ""));
                listItem.add(new Label("loggedByMe", counts.getLoggedByMeMap().get(i) + ""));
                listItem.add(new Label("assignedToMe", counts.getAssignedToMeMap().get(i) + ""));
                listItem.add(new Label("total", counts.getTotalMap().get(i) + ""));                
            }
            
        });
        
    }
    
}
