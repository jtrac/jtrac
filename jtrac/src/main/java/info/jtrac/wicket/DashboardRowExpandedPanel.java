package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.Space;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        
        add(new Label("space", "SPACE").add(sam));
        add(new Label("new", "NEW").add(sam));
        add(new Label("search", "SEARCH").add(sam));
        add(new Label("pink", "(-)").add(sam));                
        add(new Label("status", "XXX"));
        add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMeMap[1]")));
        add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMeMap[1]")));
        add(new Label("total", new PropertyModel(counts, "totalMap[1]")));                
        
        // Map<Integer, String> states = space.getMetadata().getStates();

        List<Integer> stateKeys = new ArrayList<Integer>();
        stateKeys.add(1);
        stateKeys.add(99);
        add(new ListView("rows", stateKeys) {
            protected void populateItem(ListItem listItem) {
                Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", listItem.getIndex() + ""));
                listItem.add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMeMap[" + listItem.getIndex() + "]")));
                listItem.add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMeMap[" + listItem.getIndex() + "]")));
                listItem.add(new Label("total", new PropertyModel(counts, "totalMap[" + listItem.getIndex() + "]")));                 
            }
            
        });
        
    }
    
}
