package info.jtrac.wicket.yui;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;

public class TestPage extends WebPage {
    
    public TestPage() {        
        final YuiDialog dialog = new YuiDialog("dialog");
        add(dialog);
        add(new AjaxLink("link") {
            public void onClick(AjaxRequestTarget target) {
                TestPanel panel = new TestPanel(YuiDialog.CONTENT_ID);
                dialog.show(target, "Test Heading", panel);
            }
        });
        add(new YuiCalendar("cal", null, null, false, null));
        add(new YuiCalendar("cal2", null, null, false, null));
    }
    
}
