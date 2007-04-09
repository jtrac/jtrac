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
import wicket.AttributeModifier;
import wicket.behavior.HeaderContributor;
import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.IHeaderResponse;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.FormComponentPanel;
import wicket.markup.html.form.TextField;
import wicket.model.AbstractReadOnlyModel;
import wicket.util.convert.ConversionException;
import wicket.util.convert.IConverter;
import wicket.util.convert.SimpleConverterAdapter;

/**
 * date picker panel
 */
public class YuiCalendar extends FormComponentPanel {
    
    public YuiCalendar(String id) {
        super(id);
        final TextField dateField = new TextField("field", Date.class) {
            @Override
            public IConverter getConverter(Class clazz) {
                return new SimpleConverterAdapter() {
                    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    public String toString(Object o) {
                        Date d = (Date) o;                        
                        return df.format(d);
                    }
                    public Object toObject(String s) {
                        try {
                            return df.parse(s);
                        } catch (Exception e) {
                            throw new ConversionException(e);
                        }
                    }                    
                };
            }
        };
        dateField.setOutputMarkupId(true);
        // dateField.setRequired(required);
        // this is only used for substituting ${label} when resolving error message
        // dateField.setLabel(new Model(label));
        dateField.add(new ErrorHighlighter());
        // add(model.bind(dateField, path));
        add(dateField);
        final WebMarkupContainer button = new WebMarkupContainer("button");
        button.setOutputMarkupId(true);
        button.add(new AttributeModifier("onclick", true, new AbstractReadOnlyModel() {
            public Object getObject() {
                return YuiCalendar.this.getMarkupId() + ".show()";
            }
        }));
        add(button);
        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);
        HeaderContributor contributor = new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response) {
                String markupId = YuiCalendar.this.getMarkupId();
                response.renderOnDomReadyJavascript(
                        markupId + " = new YAHOO.widget.Calendar('" + markupId + "', '" + container.getMarkupId() + "', { close: true }); "
                        + markupId + ".selectEvent.subscribe(handle" + markupId + ", '" + markupId + "', true); "
                        + markupId + ".render();");
            }
        });
        add(contributor);
        Label script = new Label("handle", new AbstractReadOnlyModel() {
            public Object getObject() {
                String markupId = YuiCalendar.this.getMarkupId();
                return "function handle" + markupId + "(type, args, obj) { "
                        + "var dates = args[0]; var date = dates[0]; "
                        + "var year = date[0], month = date[1], day = date[2]; "
                        + "document.getElementById('" + dateField.getMarkupId() + "').value = year + '-' + month + '-' + day; }";
            }
        });
        script.setEscapeModelStrings(false);
        add(script);
    }
    
}
