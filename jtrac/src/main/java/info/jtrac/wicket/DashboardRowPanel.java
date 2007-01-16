package info.jtrac.wicket;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.markup.html.basic.Label;

public class DashboardRowPanel extends BasePanel {    
    
    private DashboardRowPanel other;
    
    public DashboardRowPanel(String id, String[] data) {
        super(id);
        add(new Label("col1", data[0] + "detail"));
        add(new Label("col2", data[1] + "detail"));
        add(new Label("col3", data[2] + "detail"));
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
