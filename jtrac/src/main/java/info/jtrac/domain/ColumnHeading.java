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

package info.jtrac.domain;

import info.jtrac.domain.FilterCriteria.Expression;
import info.jtrac.wicket.ComponentUtils;
import info.jtrac.wicket.JtracCheckBoxMultipleChoice;
import info.jtrac.wicket.yui.YuiCalendar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * used to render columns in the search results table
 * and also in the search filter screen
 */
public class ColumnHeading implements Serializable {
    
    public static final String ID = "id";
    public static final String SUMMARY = "summary";
    public static final String DETAIL = "detail";
    public static final String LOGGED_BY = "loggedBy";
    public static final String STATUS = "status";
    public static final String ASSIGNED_TO = "assignedTo";
    public static final String TIME_STAMP = "timeStamp";
    
    private Field field;
    private String name;
    private String label;   
    
    private FilterCriteria filterCriteria = new FilterCriteria();    
    
    /** used only to remove from collections */
    public ColumnHeading(String name) {
        this.name = name;        
    }    
    
    public ColumnHeading(String name, Component c) {
        this.name = name;
        this.label = localize(name, c);
    }
    
    public ColumnHeading(Field field) {
        this.field = field;
        this.name = field.getName().getText();
        this.label = field.getLabel();
    }
    
    private String localize(String key, Component c) {
        return c.getLocalizer().getString("item_list." + key, c);
    }       
    
    public static List<ColumnHeading> getColumnHeadings(Space s, Component c) {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>();
        list.add(new ColumnHeading(ID, c));
        list.add(new ColumnHeading(SUMMARY, c));        
        list.add(new ColumnHeading(DETAIL, c));                
        list.add(new ColumnHeading(STATUS, c));
        list.add(new ColumnHeading(ASSIGNED_TO, c));
        list.add(new ColumnHeading(LOGGED_BY, c));
        for(Field f : s.getMetadata().getFieldList()) {
            list.add(new ColumnHeading(f));
        }
        list.add(new ColumnHeading(TIME_STAMP, c));
        return list;        
    }          
    
    public List<Expression> getValidFilterExpressions() {        
        return (List<Expression>) process(null, null);        
    }
    
    public Fragment getFilterUiFragment(Component c) {
        return (Fragment) process(c, null);
    }
    
    public void addRestrictions(DetachedCriteria criteria) {
        process(null, criteria);
    }
    
    // TODO use some elegant factory pattern here
    private Object process(Component c, DetachedCriteria criteria) {        
        boolean forFragment = c != null;        
        List<Expression> list = new ArrayList<Expression>();
        Fragment fragment = null;
        if(isField()) {
            switch(field.getName().getType()) {
                case 1:
                case 2:
                case 3:
                    list.add(Expression.IN);
                    list.add(Expression.NOT_IN);
                    if(forFragment) {
                        fragment = new Fragment("fragParent", "multiSelect");
                        final Map<String, String> options = field.getOptions();
                        JtracCheckBoxMultipleChoice choice = new JtracCheckBoxMultipleChoice("values", new ArrayList(options.keySet()), new IChoiceRenderer() {
                            public Object getDisplayValue(Object o) {
                                return options.get(o);
                            }
                            public String getIdValue(Object o, int i) {
                                return o.toString();
                            }
                        });                        
                        fragment.add(choice);
                        choice.setModel(new PropertyModel(this, "filterCriteria.values"));
                    }
                    if(criteria != null && filterCriteria.getValues() != null) {
                        criteria.add(Restrictions.in(name, filterCriteria.getValues()));
                    }
                    break; // drop down list
                case 4: // decimal number
                    list.add(Expression.EQ);
                    list.add(Expression.NOT_EQ);
                    list.add(Expression.GE);
                    list.add(Expression.LE);
                    if(forFragment) {
                        fragment = new Fragment("fragParent", "textField");
                        TextField textField = new TextField("value", Double.class);
                        textField.setModel(new PropertyModel(this, "filterCriteria.value"));
                        fragment.add(textField);                        
                    }
                    break;
                case 6: // date
                    list.add(Expression.EQ);
                    list.add(Expression.GE);
                    list.add(Expression.LE);
                    if(forFragment) {
                        fragment = new Fragment("fragParent", "dateField");                        
                        YuiCalendar calendar = new YuiCalendar("value", new PropertyModel(this, "filterCriteria.value"), false);                                                
                        fragment.add(calendar);
                    }
                    break;
                case 5: // free text
                    list.add(Expression.CONTAINS);
                    if(forFragment) {
                        fragment = new Fragment("fragParent", "textField");
                        TextField textField = new TextField("value", String.class);
                        textField.setModel(new PropertyModel(this, "filterCriteria.value"));
                        fragment.add(textField);                        
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown Column Heading " + name);
            }
        } else {
            if(name.equals(ID)) {
                list.add(Expression.EQ);
                if(forFragment) {
                    fragment = new Fragment("fragParent", "textField");
                        TextField textField = new TextField("value", String.class);
                        textField.setModel(new PropertyModel(this, "filterCriteria.value"));
                        fragment.add(textField);                    
                }                
            } else if(name.equals(SUMMARY)) {
                list.add(Expression.CONTAINS);
                if(forFragment) {
                    fragment = new Fragment("fragParent", "textField");
                        TextField textField = new TextField("value", String.class);
                        textField.setModel(new PropertyModel(this, "filterCriteria.value"));
                        fragment.add(textField);                     
                }                 
            } else if(name.equals(DETAIL)) {
                list.add(Expression.CONTAINS);
                if(forFragment) {
                    fragment = new Fragment("fragParent", "textField");
                        TextField textField = new TextField("value", String.class);
                        textField.setModel(new PropertyModel(this, "filterCriteria.value"));
                        fragment.add(textField);                      
                }                 
            } else if(name.equals(STATUS)) {
                list.add(Expression.IN);
                list.add(Expression.NOT_IN);
                if(forFragment) {
                    fragment = new Fragment("fragParent", "multiSelect");                    
                    final Map<Integer, String> options = ComponentUtils.getCurrentSpace(c).getMetadata().getStates();
                    options.remove(State.NEW);
                    JtracCheckBoxMultipleChoice choice = new JtracCheckBoxMultipleChoice("values", new ArrayList(options.keySet()), new IChoiceRenderer() {
                        public Object getDisplayValue(Object o) {
                            return options.get(o);
                        }
                        public String getIdValue(Object o, int i) {
                            return o.toString();
                        }
                    });
                    fragment.add(choice);
                    choice.setModel(new PropertyModel(this, "filterCriteria.values"));
                }
                if(criteria != null && filterCriteria.getValues() != null) {
                    criteria.add(Restrictions.in(name, filterCriteria.getValues()));
                }                
            } else if(name.equals(ASSIGNED_TO) || name.equals(LOGGED_BY)) {
                list.add(Expression.IN);
                list.add(Expression.NOT_IN);
                if(forFragment) {
                    fragment = new Fragment("fragParent", "multiSelect");
                    List<User> users = ComponentUtils.getJtrac(c).findUsersForSpace(ComponentUtils.getCurrentSpace(c).getId());
                    JtracCheckBoxMultipleChoice choice = new JtracCheckBoxMultipleChoice("values", users, new IChoiceRenderer() {
                        public Object getDisplayValue(Object o) {
                            return ((User) o).getName();
                        }
                        public String getIdValue(Object o, int i) {
                            return ((User) o).getId() + "";
                        }
                    });
                    fragment.add(choice);
                    choice.setModel(new PropertyModel(this, "filterCriteria.values"));
                }
                if(criteria != null && filterCriteria.getValues() != null) {
                    criteria.add(Restrictions.in(name, filterCriteria.getValues()));
                }                
            } else if(name.equals(TIME_STAMP)) {
                list.add(Expression.EQ);
                list.add(Expression.GE);
                list.add(Expression.LE);
                if(forFragment) {
                    fragment = new Fragment("fragParent", "dateField");                    
                    YuiCalendar calendar = new YuiCalendar("value", new PropertyModel(this, "filterCriteria.value"), false);                    
                    fragment.add(calendar);                   
                }
            } else {
                throw new RuntimeException("Unknown Column Heading " + name);
            }
        }
        if(forFragment) {
            return fragment;            
        } else {
            return list;
        }
    }
    
    //==========================================================================
    
    public Field getField() {
        return field;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isField() {
        return field != null;
    }

    public String getLabel() {
        return label;
    }

    public FilterCriteria getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(FilterCriteria filterCriteria) {
        this.filterCriteria = filterCriteria;
    }    
            
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColumnHeading)) {
            return false;
        }
        final ColumnHeading ch = (ColumnHeading) o;
        return ch.getName().equals(name);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name [").append(name);        
        sb.append("]; filterCriteria [").append(filterCriteria);
        sb.append("]");
        return sb.toString();
    }
    
}
