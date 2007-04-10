package info.jtrac.wicket.yui;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.markup.html.WebPage;

public class TestPage extends WebPage {
    
    public TestPage() {        
        final YuiDialog dialog = new YuiDialog("dialog", "Test Heading");
        add(dialog);
        add(new AjaxLink("link") {
            public void onClick(AjaxRequestTarget target) {
                TestPanel panel = new TestPanel(YuiDialog.CONTENT_ID);
                dialog.show(target, panel);
            }
        });
        add(new YuiCalendar("cal", null, null, false, null));
        add(new YuiCalendar("cal2", null, null, false, null));
    }
    
}
