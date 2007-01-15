package info.jtrac.wicket;

import wicket.markup.html.basic.Label;

public class DashboardPage extends BasePage {
      
    public DashboardPage() {
        super("Dashboard");
        border.add(new Label("dashboard", "Hello World!"));        
    }
    
}
