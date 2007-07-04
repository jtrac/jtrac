/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
