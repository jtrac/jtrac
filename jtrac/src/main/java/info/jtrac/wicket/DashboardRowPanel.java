package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.markup.html.basic.Label;
import wicket.model.PropertyModel;

public class DashboardRowPanel extends BasePanel {
    
    public DashboardRowPanel(String id, Counts counts) {
        
        super(id);
        setOutputMarkupId(true);        
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {                
            }
        });          
        
        add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe")));
        add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe")));
        add(new Label("total", new PropertyModel(counts, "total")));
                
      
    }
    
}
