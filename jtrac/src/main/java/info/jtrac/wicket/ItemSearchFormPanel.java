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

import info.jtrac.domain.ColumnHeading;
import info.jtrac.domain.FilterCriteria;
import info.jtrac.domain.FilterCriteria.Expression;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.DateUtils;
import info.jtrac.wicket.yui.YuiCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * item search form panel
 */
public class ItemSearchFormPanel extends BasePanel {        
            
    private ItemSearch itemSearch;
    
    private FilterCriteria filterCriteria = new FilterCriteria(null);
    
    private List<Expression> expressionChoices;
    
    private BoundCompoundPropertyModel model;
    
    public ItemSearchFormPanel(String id, User user) {
        super(id);
        this.itemSearch = new ItemSearch(user);
        addComponents();        
    }
    
    public ItemSearchFormPanel(String id) {
        super(id);
        Space s = getCurrentSpace();
        if(s != null) {
            this.itemSearch = new ItemSearch(s, this);
        } else {
            this.itemSearch = new ItemSearch(getPrincipal());
        }        
        addComponents();        
    }    
    
    public ItemSearchFormPanel(String id, ItemSearch itemSearch) {
        super(id);
        this.itemSearch = itemSearch;
        addComponents();        
    }    
    
    private void addComponents() {
        final Form form = new Form("form");
        add(form);
        model = new BoundCompoundPropertyModel(filterCriteria);
        form.setModel(model);
        // column ==============================================================
        List<ColumnHeading> columnChoiceList = itemSearch.getSearchColumnHeadings();
        DropDownChoice columnChoice = new DropDownChoice("columnHeading", columnChoiceList, new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                return ((ColumnHeading) o).getLabel();
            }
            public String getIdValue(Object o, int i) {
                return ((ColumnHeading) o).getName();
            }
        });
        form.add(columnChoice);
        filterCriteria.setColumnHeading(columnChoiceList.get(0));
        // values ==============================================================
        final Fragment frag = initChoices();
        form.add(frag);
        // expression ==========================================================
        final DropDownChoice expressionChoice = new DropDownChoice("expression", expressionChoices, new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                String key = ((Expression) o).getKey();
                return localize("item_filter." + key);
            }
            public String getIdValue(Object o, int i) {
                return ((Expression) o).getKey();
            }
        });
        expressionChoice.setOutputMarkupId(true);
        form.add(expressionChoice);
        // ajax ================================================================
        columnChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
            protected void onUpdate(AjaxRequestTarget target) {
                ColumnHeading ch = (ColumnHeading) getFormComponent().getConvertedInput();
                filterCriteria.setColumnHeading(ch);
                Fragment temp = initChoices();
                form.replace(temp);
                expressionChoice.setChoices(expressionChoices);
                target.addComponent(expressionChoice);
                target.addComponent(temp);
            }
        });
        // list ================================================================
        final AjaxListView listView = new AjaxListView("filters");
        form.add(listView);
        form.add(new AjaxButton("add") {
            @Override
            protected void onError(AjaxRequestTarget target, Form unused) {
                logger.debug("ajax form validation error");
                return;
            }
            protected void onSubmit(AjaxRequestTarget target, Form unused) {
                if((filterCriteria.getValues() == null || filterCriteria.getValues().size() == 0)
                && filterCriteria.getValue() == null) {
                    return;
                }
                Item newItem = listView.addItem();
                target.prependJavascript("var myTr = document.createElement('tr');"
                        + " myTr.id = '" + newItem.getMarkupId() + "';"
                        + " document.getElementById('container').appendChild(myTr);");
                target.addComponent(newItem);
            }
        });
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                setCurrentItemSearch(itemSearch);
                setResponsePage(ItemListPage.class);            
            }              
        });
    }
    
    public Fragment initChoices() {
        expressionChoices = new ArrayList<Expression>();
        ColumnHeading ch = filterCriteria.getColumnHeading();
        Fragment fragment = null;
        if(ch.isField()) {
            switch(ch.getField().getName().getType()) {
                case 1:
                case 2:
                case 3:
                    expressionChoices.add(Expression.IN);
                    expressionChoices.add(Expression.NOT_IN);
                    fragment = new Fragment("fragParent", "multiSelect");
                    final Map<String, String> options = ch.getField().getOptions();
                    fragment.add(new JtracCheckBoxMultipleChoice("values", new ArrayList(options.keySet()), new IChoiceRenderer() {
                        public Object getDisplayValue(Object o) {
                            return options.get(o);
                        }
                        public String getIdValue(Object o, int i) {
                            return o.toString();
                        }
                    }));
                    break; // drop down list
                case 4: // decimal number
                    expressionChoices.add(Expression.EQ);
                    expressionChoices.add(Expression.NOT_EQ);
                    expressionChoices.add(Expression.GE);
                    expressionChoices.add(Expression.LE);
                    fragment = new Fragment("fragParent", "textField");
                    fragment.add(new TextField("value", Double.class));
                    break;
                case 6: // date
                    expressionChoices.add(Expression.EQ);
                    expressionChoices.add(Expression.GE);
                    expressionChoices.add(Expression.LE);
                    fragment = new Fragment("fragParent", "dateField");
                    fragment.add(new YuiCalendar("value", model, "value", false, ch.getLabel()));
                    break;
                case 5: // free text
                    expressionChoices.add(Expression.CONTAINS);
                    fragment = new Fragment("fragParent", "textField");
                    fragment.add(new TextField("value", String.class));
                    break;
                default:
                    throw new RuntimeException("Unknown Column Heading " + ch.getName());
            }
        } else {
            if(ch.getName().equals(ColumnHeading.STATUS)) {
                expressionChoices.add(Expression.IN);
                expressionChoices.add(Expression.NOT_IN);
                fragment = new Fragment("fragParent", "multiSelect");
                final Map<Integer, String> options = getCurrentSpace().getMetadata().getStates();
                fragment.add(new JtracCheckBoxMultipleChoice("values", new ArrayList(options.keySet()), new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return options.get(o);
                    }
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }
                }));
            } else if(ch.getName().equals(ColumnHeading.ASSIGNED_TO) || ch.getName().equals(ColumnHeading.LOGGED_BY)) {
                expressionChoices.add(Expression.IN);
                expressionChoices.add(Expression.NOT_IN);
                fragment = new Fragment("fragParent", "multiSelect");
                List<User> users = getJtrac().findUsersForSpace(getCurrentSpace().getId());
                fragment.add(new JtracCheckBoxMultipleChoice("values", users, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return ((User) o).getName();
                    }
                    public String getIdValue(Object o, int i) {
                        return ((User) o).getId() + "";
                    }
                }));
            } else if(ch.getName().equals(ColumnHeading.TIME_STAMP)) {
                expressionChoices.add(Expression.EQ);
                expressionChoices.add(Expression.GE);
                expressionChoices.add(Expression.LE);
                fragment = new Fragment("fragParent", "dateField");
                fragment.add(new YuiCalendar("value", model, "value", false, ch.getLabel()));
            } else {
                throw new RuntimeException("Unknown Column Heading " + ch.getName());
            }
        }
        filterCriteria.setExpression(expressionChoices.get(0));
        filterCriteria.setValue(null);
        filterCriteria.setValues(null);
        fragment.setOutputMarkupId(true);
        return fragment;
    }
    
    public class AjaxListView extends RefreshingView {
        
        public AjaxListView(String id) {
            super(id);
        }
        
        public Item addItem() {
            String uniqueId = newChildId();
            Map map = itemSearch.getFilterCriteriaMap();
            Item item = newItem(uniqueId, map.size(), new Model(uniqueId));
            map.put(uniqueId, filterCriteria);
            populateItem(item);
            add(item);
            return item;
        }
        
        public void removeItem(Item item) {
            itemSearch.getFilterCriteriaMap().remove(item.getModelObject());
        }
        
        protected Iterator getItemModels() {
            List<IModel> models = new ArrayList<IModel>();
            for(String s : itemSearch.getFilterCriteriaMap().keySet()) {
                models.add(new Model(s));
            }
            return models.iterator();
        }
        
        protected void populateItem(final Item item) {
            FilterCriteria fc = itemSearch.getFilterCriteriaMap().get(item.getModelObject());
            final ColumnHeading ch = fc.getColumnHeading();
            item.add(new Label("columnHeading", ch.getLabel()));
            item.add(new Label("expression", localize("item_filter." + fc.getExpression().getKey())));
            item.add(new ListView("values", fc.getValues()) {
                protected void populateItem(ListItem item) {
                    Object o = item.getModelObject();
                    if(o instanceof User) {
                        item.add(new Label("value", new PropertyModel(o, "name")));
                    } else if(ch.getName().equals(ColumnHeading.STATUS)) {
                        String label = getCurrentSpace().getMetadata().getStatusValue((Integer) o);
                        item.add(new Label("value", label));
                    } else {
                        item.add(new Label("value", ch.getField().getOptions().get(o)));
                    }
                }
            });
            if(fc.getValue() != null && fc.getValue() instanceof Date) {
                item.add(new Label("value", DateUtils.format((Date) fc.getValue())));
            } else {
                item.add(new Label("value", new PropertyModel(fc, "value")));
            }
            item.add((new AjaxButton("remove") {
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    AjaxListView.this.removeItem(item);
                    target.appendJavascript("var myTr = document.getElementById('" + item.getMarkupId() + "');"
                            + " myTr.parentNode.removeChild(myTr);");
                }
            }));
            item.setOutputMarkupId(true);
        }
    }    
    
}
