package info.jtrac.wicket;

import java.util.List;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.ajax.markup.html.WicketAjaxIndicatorAppender;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * wraps an ajax drop down so that the ajax "spinner" image shows
 */
public class IndicatingDropDownChoice extends DropDownChoice implements IAjaxIndicatorAware {
    
    private final WicketAjaxIndicatorAppender indicatorAppender = new WicketAjaxIndicatorAppender();
    
    public IndicatingDropDownChoice(String id, List list, IChoiceRenderer cr){
        super(id, list, cr);
        add(indicatorAppender);
    }
    
    public java.lang.String getAjaxIndicatorMarkupId(){
        return indicatorAppender.getMarkupId();
    }
    
}
