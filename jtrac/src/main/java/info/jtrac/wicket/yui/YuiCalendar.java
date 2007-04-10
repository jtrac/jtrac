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
import wicket.AttributeModifier;
import wicket.behavior.HeaderContributor;
import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.IHeaderResponse;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Panel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;
import wicket.model.Model;
import wicket.util.convert.ConversionException;
import wicket.util.convert.IConverter;
import wicket.util.convert.converters.AbstractConverter;

/**
 * yui date picker panel
 * TODO see if can be made FormComponentPanel for cleaner client code
 */
public class YuiCalendar extends Panel {
    
    public YuiCalendar(String id, BoundCompoundPropertyModel model, String path, boolean required, String label) {
        super(id);
        final TextField dateField = new TextField("field", Date.class) {
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
                String markupId = YuiCalendar.this.getMarkupId();
                return markupId + ".render(); " + markupId + ".show()";
            }
        }));
        add(button);
        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);
        HeaderContributor contributor = new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response) {
                String markupId = YuiCalendar.this.getMarkupId();
                response.renderOnDomReadyJavascript("init" + markupId + "()");
                response.renderJavascript(
                          "function init" + markupId + "() { "
                        + markupId + " = new YAHOO.widget.Calendar('" + markupId + "', '" + container.getMarkupId() + "', { close: true }); "
                        + markupId + ".selectEvent.subscribe(handleSelect, document.getElementById('" + dateField.getMarkupId() + "'), true); }", null
                );
            }
        });
        add(contributor);
    }
    
}
