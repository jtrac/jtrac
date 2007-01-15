package info.jtrac.wicket;

import info.jtrac.Version;
import wicket.markup.html.basic.Label;
import wicket.markup.html.border.Border;

public class TemplateBorder extends Border {

    public TemplateBorder() {
        super("border");
        add(new HeaderPanel());
        add(new Label("version", Version.VERSION));
    }
    
}
