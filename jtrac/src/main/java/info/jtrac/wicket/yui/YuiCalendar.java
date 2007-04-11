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

package info.jtrac.wicket.yui;

import info.jtrac.wicket.ErrorHighlighter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converters.AbstractConverter;

/**
 * yui date picker panel
 * TODO see if can be made FormComponentPanel for cleaner client code
 */
public class YuiCalendar extends Panel implements IHeaderContributor {
    
    private TextField dateField;
    private WebMarkupContainer container;
            
    public YuiCalendar(String id, BoundCompoundPropertyModel model, String path, boolean required, String label) {
        
        super(id);        
                
        add(HeaderContributor.forJavaScript("resources/yui/yahoo/yahoo.js"));
        add(HeaderContributor.forJavaScript("resources/yui/event/event.js"));
        add(HeaderContributor.forJavaScript("resources/yui/dom/dom.js"));        
        add(HeaderContributor.forJavaScript("resources/yui/calendar/calendar.js"));
        add(HeaderContributor.forCss("resources/yui/calendar/assets/calendar.css"));         
        
        dateField = new TextField("field", Date.class) {
            @Override
            public IConverter getConverter(Class clazz) {
                return new AbstractConverter() {
                    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    public Object convertToObject(String s, Locale locale) {
                        try {
                            return df.parse(s);
                        } catch (Exception e) {
                            throw new ConversionException(e);
                        }                                                
                    }
                    protected Class getTargetType() {
                        return Date.class;
                    } 
                    @Override
                    public String convertToString(Object o, Locale locale) {
                        Date d = (Date) o;                        
                        return df.format(d);
                    }                    
                };
            }
        };
        dateField.setOutputMarkupId(true);
        dateField.setRequired(required);
        // this is only used for substituting ${label} when resolving error message
        if(label != null) {
            dateField.setLabel(new Model(label));
        }
        dateField.add(new ErrorHighlighter());
        if(model != null) {
            add(model.bind(dateField, path));
        } else {
            add(dateField);
        }
        
        final WebMarkupContainer button = new WebMarkupContainer("button");
        button.setOutputMarkupId(true);
        button.add(new AttributeModifier("onclick", true, new AbstractReadOnlyModel() {
            public Object getObject() {                
                return "showCalendar(" + getCalendarId() + ", '" + getInputId() + "');";
            }
        }));
        add(button);
        
        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);
    }
    
    private String getCalendarId() {
        return getMarkupId();
    }
    
    private String getInputId() {
        return dateField.getMarkupId();
    }
    
    private String getContainerId() {
        return container.getMarkupId();
    }

    public void renderHead(IHeaderResponse response) {       
        String calendarId = getCalendarId();
        response.renderOnDomReadyJavascript("init" + calendarId + "()");
        response.renderJavascript(
                  "function init" + calendarId + "() { "
                + calendarId + " = new YAHOO.widget.Calendar('" + calendarId + "', '" + getContainerId() + "'); "
                + calendarId + ".selectEvent.subscribe(handleSelect, [ " + calendarId + ", '" + getInputId() + "' ], true); }", null);
    }        
}
