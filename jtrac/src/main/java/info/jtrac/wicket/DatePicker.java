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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Panel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;
import wicket.model.Model;
import wicket.util.convert.ConversionException;
import wicket.util.convert.IConverter;
import wicket.util.convert.SimpleConverterAdapter;

/**
 * date picker panel
 */
public class DatePicker extends Panel {
    
    public DatePicker(String id, BoundCompoundPropertyModel model, String path, boolean required, String label) {
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
        dateField.setRequired(required);
        // this is only used for substituting ${label} when resolving error message
        dateField.setLabel(new Model(label));
        dateField.add(new ErrorHighlighter());
        add(model.bind(dateField, path));
        final WebMarkupContainer button = new WebMarkupContainer("button");
        button.setOutputMarkupId(true);
        add(button);
        Label script = new Label("script", new AbstractReadOnlyModel() {
            public Object getObject() {
                return "Calendar.setup({"
                    + " inputField : '" + dateField.getMarkupId() + "',"
                    + " ifFormat : '%Y-%m-%d',"
                    + " button : '" + button.getMarkupId() + "',"
                    + " step : 1"
                    + " });";                
            }
        });
        script.setEscapeModelStrings(false);
        add(script);
    }
    
}
