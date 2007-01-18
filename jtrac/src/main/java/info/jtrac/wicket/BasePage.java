package info.jtrac.wicket;

import info.jtrac.Jtrac;
import wicket.Component;
import wicket.MarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.border.Border;

public abstract class BasePage extends WebPage {
    
    protected Border border;        
    
    protected Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }  
    
    public BasePage(String title) {
        add(new Label("title", title));
        border = new TemplateBorder();
        add(border);    
    }
    
}
