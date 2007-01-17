package info.jtrac.wicket;

import info.jtrac.domain.Counts;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.markup.html.basic.Label;
import wicket.model.PropertyModel;

public class DashboardRowPanel extends BasePanel {    
    
    private DashboardRowPanel other;
    
    public DashboardRowPanel(String id, Counts counts) {
        super(id);
        
        add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe")));
        add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe")));
        add(new Label("total", new PropertyModel(counts, "total")));
        
        setOutputMarkupId(true);
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                target.addComponent(getParent());
                target.appendJavascript("Element.hide('" + getParent().getMarkupId() + "');");
                target.addComponent(other);
                target.appendJavascript("Element.show('" + other.getMarkupId() + "');");
            }
        });        
    }

    public void setOther(DashboardRowPanel other) {
        this.other = other;
    }
    
}
