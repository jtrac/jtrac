package info.jtrac.wicket;

import info.jtrac.Jtrac;
import wicket.markup.html.WebPage;

public class BasePage extends WebPage {
    protected Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }
}
