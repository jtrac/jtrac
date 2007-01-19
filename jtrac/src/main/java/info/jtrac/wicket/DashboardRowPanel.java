package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.SecurityUtils;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.markup.html.basic.Label;
import wicket.model.PropertyModel;

public class DashboardRowPanel extends BasePanel {    
    
    private Counts counts;
    
    public DashboardRowPanel(String id, Counts counts) {
        
        super(id);
        setOutputMarkupId(true);        
        
        this.counts = counts;
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                Counts temp = DashboardRowPanel.this.counts;
                User user = SecurityUtils.getPrincipal();
                if (!temp.isDetailed()) {
                    // space instance held in Counts originated from Acegi
                    // so incompatible with open session in view, get proper one
                    Space space = getJtrac().loadSpace(temp.getSpace().getId());
                    temp = getJtrac().loadCountsForUserSpace(user, space);
                }
                DashboardRowExpandedPanel b = new DashboardRowExpandedPanel("dashboardRow", temp);
                DashboardRowPanel.this.replaceWith(b);
                target.addComponent(b);
            }
        });          
        
        add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe")));
        add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe")));
        add(new Label("total", new PropertyModel(counts, "total")));
      
    }
    
}
