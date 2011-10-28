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

import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.wicket.yui.YuiCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.converters.DoubleConverter;

/**
 * This class is responsible for the panel of custom fields that
 * can be reused in the item-create/item-view forms.
 */
public class CustomFieldsFormPanel extends BasePanel {
    /**
     * Constructor
     * 
     * @param id
     * @param model
     * @param space
     */
    public CustomFieldsFormPanel(String id, BoundCompoundPropertyModel model, Space space) {
        super(id);
        List<Field> fields = space.getMetadata().getFieldList();
        addComponents(model, fields, true);
    }
    
    /**
     * Constructor
     * 
     * @param id
     * @param model
     * @param item
     * @param user
     */
    public CustomFieldsFormPanel(String id, BoundCompoundPropertyModel model, Item item, User user) {
        super(id);
        List<Field> fields = item.getEditableFieldList(user);
        addComponents(model, fields, false);
    }
    
    /**
     * This method allows to add components (custom fields).
     * 
     * @param model
     * @param fields
     * @param isEditMode
     */
    private void addComponents(final BoundCompoundPropertyModel model, List<Field> fields, final boolean isEditMode) {
        ListView listView = new ListView("fields", fields) {
            protected void populateItem(ListItem listItem) {
                final Field field = (Field) listItem.getModelObject();
                boolean isRequired = isEditMode ? false : !field.isOptional();
                listItem.add(new Label("label", field.getLabel()));
                listItem.add(new Label("star", isRequired ? "*" : "&nbsp;").setEscapeModelStrings(false));
                if (field.isDropDownType()) {
                    Fragment f = new Fragment("field", "dropDown", CustomFieldsFormPanel.this);
                    final Map<String, String> options = field.getOptions();
                    List<String> keys; // bound value
                    if (options != null) {
                        keys = new ArrayList(options.keySet());
                    } else {
                        keys = new ArrayList<String>();
                    }
                    DropDownChoice choice = new DropDownChoice("field", keys, new IChoiceRenderer() {
                        public Object getDisplayValue(Object o) {
                            return options.get(o);
                        };
                        
                        public String getIdValue(Object o, int i) {
                            return o.toString();
                        };
                    });
                    choice.setNullValid(true);
                    choice.setLabel(new Model(field.getLabel()));
                    choice.setRequired(isRequired);
                    WebMarkupContainer border = new WebMarkupContainer("border");
                    f.add(border);
                    border.add(new ErrorHighlighter(choice));
                    border.add(model.bind(choice, field.getName().getText()));
                    listItem.add(f);
                } else if (field.isDatePickerType()) {
                    /*
                     * ======================================
                     * Date picker
                     * ======================================
                     */
                    YuiCalendar calendar = new YuiCalendar("field",
                            new PropertyModel(model, field.getName().getText()), isRequired);
                    listItem.add(calendar);
                    calendar.setLabel(new Model(field.getLabel()));
                } else {
                    /*
                     * ======================================
                     * Text field
                     * ======================================
                     */
                    Fragment f = new Fragment("field", "textField", CustomFieldsFormPanel.this);
                    TextField textField = new TextField("field");
                    
                    /*
                     * Check if the field is used to display/edit
                     * Double values.
                     */
                    if (field.isDecimalNumberType()) {
                        /*
                         * The following code overwrites the default
                         * DoubleConverter used by Wicket (getConverter(Class)).
                         * The original implementation rounds after three
                         * digits to the right of the decimal point.
                         * 
                         * As there are requirements to show six digits to
                         * the right of the decimal point the NumberFormat
                         * is enhanced in the code below by the DecimalFormat
                         * class. That has been used because the DecimalFormat
                         * class supports a formatting pattern which allows
                         * to use the digits to the right only if really
                         * needed (currently limited to six digits to the
                         * right of the decimal point).
                         */
                        textField = new TextField("field", Double.class) {
                            public org.apache.wicket.util.convert.IConverter getConverter(Class type) {
                                DoubleConverter converter = (DoubleConverter) DoubleConverter.INSTANCE;
                                java.text.NumberFormat numberFormat = converter.getNumberFormat(getLocale());
                                java.text.DecimalFormat decimalFormat = (java.text.DecimalFormat)numberFormat;
                                decimalFormat.applyPattern("###,##0.######");
                                converter.setNumberFormat(getLocale(), decimalFormat);
                                return converter;
                            };
                        };
                    }
                    
                    textField.add(new ErrorHighlighter());
                    textField.setRequired(isRequired);
                    textField.setLabel(new Model(field.getLabel()));
                    f.add(model.bind(textField, field.getName().getText()));
                    listItem.add(f);
                }
            }
        };
        listView.setReuseItems(true);
        add(listView);
    } // end method addComponents(...)
}