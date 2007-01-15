package info.jtrac.wicket;

import info.jtrac.Jtrac;
import wicket.markup.html.panel.Panel;

public class BasePanel extends Panel {
    
    protected Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }
    
    public BasePanel(String id) {
        super(id);
    } 
    
}
