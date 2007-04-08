package info.jtrac.wicket.yui;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.markup.html.WebPage;

public class TestPage extends WebPage {
    
    public TestPage() {
        TestPanel panel = new TestPanel(YuiDialog.CONTENT_ID);
        final YuiDialog dialog = new YuiDialog("dialog", "Test Heading", panel);
        add(dialog);
        add(new AjaxLink("link") {
            public void onClick(AjaxRequestTarget target) {
                dialog.show(target);
            }
        }.setOutputMarkupId(true));
    }
    
}
