package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.SecurityUtils;
import wicket.Component;
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
                User user = SecurityUtils.getPrincipal();
                Space space = getJtrac().loadSpace(1);
                DashboardRowExpandedPanel b = new DashboardRowExpandedPanel("dashboardRow", user, space);
                DashboardRowPanel.this.replaceWith(b);
                target.addComponent(b);
            }
        });          
        
        add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe")));
        add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe")));
        add(new Label("total", new PropertyModel(counts, "total")));
      
    }
    
}
