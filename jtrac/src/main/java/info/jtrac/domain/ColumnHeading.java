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

import info.jtrac.Jtrac;
import info.jtrac.JtracDao;
import info.jtrac.domain.FilterCriteria.Expression;
import info.jtrac.util.DateUtils;
import info.jtrac.wicket.JtracCheckBoxMultipleChoice;
import info.jtrac.wicket.yui.YuiCalendar;

import static info.jtrac.domain.ColumnHeading.Name.*;
import static info.jtrac.domain.FilterCriteria.Expression.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.PropertyModel;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * used to render columns in the search results table
 * and also in the search filter screen
 */
public class ColumnHeading implements Serializable {
    
    private static final Map<String, Name> NAMES_MAP;
    
    // set up a static Map to resolve a String to our ColumnHeading.Name enum value
    static {
        NAMES_MAP = new HashMap<String, Name>();
        for (Name n : Name.values()) {
            NAMES_MAP.put(n.text, n);
        }
    }        
    
    /**
     * Resolve a String to a valid enum value for ColumnHeading.Name
     */
    private static Name convertToName(String text) {
        Name n = NAMES_MAP.get(text);
        if (n == null) {
            throw new RuntimeException("Bad name " + text);
        }
        return n;
    }
    
    /**
     * test if a given string is a valid column heading name
     */
    public static boolean isValidName(String text) {
        return NAMES_MAP.containsKey(text);
    }    
    
    public static boolean isValidFieldOrColumnName(String text) {
        return isValidName(text) || Field.isValidName(text);
    }
    
    public enum Name {
        
        ID("id"),
        SUMMARY("summary"),
        DETAIL("detail"),
        LOGGED_BY("loggedBy"),
        STATUS("status"),
        ASSIGNED_TO("assignedTo"),
        TIME_STAMP("timeStamp"),
        SPACE("space");
        
        private String text;
        
        Name(String text) {            
            this.text = text;
        }     
        
        public String getText() {
            return text;
        }         
        
        @Override
        public String toString() {
            return text;
        }        
        
    }
    
    private Field field;
    private Name name;    
    private String label;
    private boolean visible = true;
    
    private FilterCriteria filterCriteria = new FilterCriteria();             
    
    private ColumnHeading(Name name) {
        this.name = name;    
        if(name == DETAIL || name == SPACE) {
            visible = false;
        }        
    }
    
    public ColumnHeading(Field field) {
        this.field = field;        
        this.label = field.getLabel();
    }           
    
    public boolean isField() {
        return field != null;
    }
    
    public static List<ColumnHeading> getColumnHeadings(Space s) {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>();
        list.add(new ColumnHeading(ID));
        list.add(new ColumnHeading(SUMMARY));        
        list.add(new ColumnHeading(DETAIL));                
        list.add(new ColumnHeading(STATUS));
        list.add(new ColumnHeading(LOGGED_BY));
        list.add(new ColumnHeading(ASSIGNED_TO));        
        for(Field f : s.getMetadata().getFieldList()) {
            list.add(new ColumnHeading(f));
        }
        list.add(new ColumnHeading(TIME_STAMP));
        return list;        
    }
    
    public static List<ColumnHeading> getColumnHeadings() {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>();
        list.add(new ColumnHeading(ID));
        list.add(new ColumnHeading(SPACE));        
        list.add(new ColumnHeading(SUMMARY));        
        list.add(new ColumnHeading(DETAIL));                            
        list.add(new ColumnHeading(LOGGED_BY));
        list.add(new ColumnHeading(ASSIGNED_TO));
        list.add(new ColumnHeading(TIME_STAMP));        
        return list;        
    }            
    
    // global variables
    private List<Expression> validFilterExpressions;
    private Fragment fragment;    
    private boolean returnFragment;        
    private DetachedCriteria criteria;
    private MarkupContainer markupContainer;
    private String queryString;
    private boolean returnQueryString;
    private List<String> queryStringTokens;
    private User user;
    private Space space;
    // TODO this is a really bad hack
    private transient Jtrac jtrac;
    private transient JtracDao jtracDao;
    
    public List<Expression> getValidFilterExpressions(Jtrac jtrac) {   
        queryStringTokens = null;
        this.jtrac = jtrac;
        doTheSwitchCase();
        this.jtrac = null;
        return validFilterExpressions;        
    }
    
    public Fragment getFilterUiFragment(MarkupContainer c, User user, Space space, Jtrac jtrac) {
        this.markupContainer = c;
        this.jtrac = jtrac;
        this.user = user;
        this.space = space;
        returnFragment = true;
        doTheSwitchCase();
        this.jtrac = null;
        return fragment;
    }
    
    public void addRestrictions(DetachedCriteria c, JtracDao jtracDao) {        
        this.criteria = c;
        this.jtracDao = jtracDao;
        doTheSwitchCase();
        this.jtracDao = null;
    }    
    
    public String getQueryString() {
        returnFragment = false;
        returnQueryString = true;
        doTheSwitchCase();
        if(queryString == null) {
            return null;
        }
        return filterCriteria.getExpression().getKey() + "_" + queryString;
    }
    
    public void loadFromQueryString(String s, Jtrac jtrac) {
        this.jtrac = jtrac;
        String [] tokens = s.split("_");
        filterCriteria.setExpression(FilterCriteria.convertToExpression(tokens[0]));
        queryStringTokens = new ArrayList<String>();
        // ignore first token, this has been parsed as Expression above
        for(int i = 1; i < tokens.length; i++ ) {
            queryStringTokens.add(tokens[i]);
        }
        doTheSwitchCase();
        this.jtrac = null;
    }
    
    // TODO use some elegant factory pattern here if possible    
    // this routine is a massive if-then construct that has 5 responsibilities
    // based on column type: (column in the search results table)
    //  - return the possible expressions (equals, greater-than etc) to show on filter UI for selection
    //  - return the wicket ui fragment that will be shown over ajax (based on selected expression)
    //  - convert filter criteria into hibernate restrictions that will be used to query the database
    //  - return a querystring representation of the filter criteria to create a bookmarkable url
    //  - load a querystring representation and initialize filter critera when acting on a bookmarkable url
    // putting all these things into one place, makes it easy to maintain, as each of these things
    // are closely interdependent
    private void doTheSwitchCase() {                                        
        List values = filterCriteria.getValues();
        Object value = filterCriteria.getValue();
        Object value2 = filterCriteria.getValue2();
        Expression expression = filterCriteria.getExpression();
        boolean returnCriteria = criteria != null;
        if(isField()) {            
            switch(field.getName().getType()) {
                //==============================================================
                case 1:
                case 2:
                case 3:                    
                    setValidFilterExpressions(IN);                    
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "multiSelect", markupContainer);
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
                        choice.setModel(new PropertyModel(filterCriteria, "values"));                        
                    }
                    if(filterHasValueList()) {
                        if(returnCriteria) {
                            List<Integer> keys = new ArrayList<Integer>(values.size());
                            for(Object o : values) {
                                keys.add(new Integer(o.toString()));
                            }
                            criteria.add(Restrictions.in(getNameText(), keys));
                        }
                        setQueryStringFromValueList();                        
                    }
                    setValueListFromQueryString();
                    break; // drop down list
                //==============================================================
                case 4: // decimal number                    
                    setValidFilterExpressions(EQ, NOT_EQ, GT, LT, BETWEEN);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "textField", markupContainer);
                        TextField textField = new TextField("value", Double.class);
                        textField.setModel(new PropertyModel(filterCriteria, "value"));
                        fragment.add(textField);
                        if(expression == BETWEEN) {
                            TextField textField2 = new TextField("value2", Double.class);
                            textField.setModel(new PropertyModel(filterCriteria, "value2"));
                            fragment.add(textField2);                            
                        } else {
                            fragment.add(new WebMarkupContainer("value2").setVisible(false));
                        }
                    }
                    if(filterHasValue()) {
                        if(returnCriteria) {
                            switch(expression) {
                                case EQ: criteria.add(Restrictions.eq(getNameText(), value)); break;
                                case NOT_EQ: criteria.add(Restrictions.not(Restrictions.eq(name.text, value))); break;
                                case GT: criteria.add(Restrictions.gt(getNameText(), value)); break;
                                case LT: criteria.add(Restrictions.lt(getNameText(), value)); break;
                                case BETWEEN: 
                                    criteria.add(Restrictions.gt(getNameText(), value));
                                    criteria.add(Restrictions.lt(getNameText(), value2));
                                    break;
                                default:                            
                            }
                        }
                        setQueryStringFromValue(Double.class);                        
                    } 
                    setValueFromQueryString(Double.class);
                    break;
                //==============================================================
                case 6: // date
                    setValidFilterExpressions(EQ, NOT_EQ, GT, LT, BETWEEN);                    
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "dateField", markupContainer);                        
                        YuiCalendar calendar = new YuiCalendar("value", new PropertyModel(filterCriteria, "value"), false);                                                
                        fragment.add(calendar);
                        if(filterCriteria.getExpression() == BETWEEN) {
                            YuiCalendar calendar2 = new YuiCalendar("value2", new PropertyModel(filterCriteria, "value2"), false);                                                
                            fragment.add(calendar2);                            
                        } else {
                            fragment.add(new WebMarkupContainer("value2").setVisible(false));
                        }
                    }
                    if(filterHasValue()) {
                        if(returnCriteria) {
                            switch(expression) {
                                case EQ: criteria.add(Restrictions.eq(getNameText(), value)); break;
                                case NOT_EQ: criteria.add(Restrictions.not(Restrictions.eq(getNameText(), value))); break;
                                case GT: criteria.add(Restrictions.gt(getNameText(), value)); break;
                                case LT: criteria.add(Restrictions.lt(getNameText(), value)); break;
                                case BETWEEN: 
                                    criteria.add(Restrictions.gt(getNameText(), value));
                                    criteria.add(Restrictions.lt(getNameText(), value2));
                                    break;
                                default:                            
                            }
                        }
                        setQueryStringFromValue(Date.class);                        
                    }
                    setValueFromQueryString(Date.class);
                    break;
                //==============================================================
                case 5: // free text                    
                    setValidFilterExpressions(CONTAINS);                    
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "textField", markupContainer);
                        TextField textField = new TextField("value", String.class);
                        textField.setModel(new PropertyModel(filterCriteria, "value"));
                        fragment.add(textField);
                        fragment.add(new WebMarkupContainer("value2").setVisible(false));
                    }
                    if(filterHasValue()) {
                        if(returnCriteria) {
                            criteria.add(Restrictions.ilike(getNameText(), (String) value, MatchMode.ANYWHERE));
                        }
                        setQueryStringFromValue(String.class);                        
                    }
                    setValueFromQueryString(String.class);
                    break;
                //==============================================================
                default:
                    throw new RuntimeException("Unknown Column Heading " + name);
            }        
        } else { // this is not a custom field but one of the "built-in" columns           
            switch(name) {
                //==============================================================
                case ID:
                    setValidFilterExpressions(EQ);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "textField", markupContainer);
                            TextField textField = new TextField("value", String.class);
                            textField.setModel(new PropertyModel(filterCriteria, "value"));
                            fragment.add(textField);
                            fragment.add(new WebMarkupContainer("value2").setVisible(false));
                    }
                    // should never come here for criteria: see ItemSearch#getRefId()
                    if(filterHasValue()) {
                        setQueryStringFromValue(String.class);                        
                    }
                    setValueFromQueryString(String.class);
                    break;
                //==============================================================
                case SUMMARY:               
                    setValidFilterExpressions(CONTAINS);                
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "textField", markupContainer);
                            TextField textField = new TextField("value", String.class);
                            textField.setModel(new PropertyModel(filterCriteria, "value"));
                            fragment.add(textField);
                            fragment.add(new WebMarkupContainer("value2").setVisible(false));
                    }
                    if(filterHasValue()) {
                        if(returnCriteria) {
                            criteria.add(Restrictions.ilike(getNameText(), (String) value, MatchMode.ANYWHERE));
                        }
                        setQueryStringFromValue(String.class);                        
                    }
                    setValueFromQueryString(String.class);
                    break;
                //==============================================================
                case DETAIL:
                    setValidFilterExpressions(CONTAINS);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "textField", markupContainer);
                            TextField textField = new TextField("value", String.class);
                            textField.setModel(new PropertyModel(filterCriteria, "value"));
                            fragment.add(textField);
                            fragment.add(new WebMarkupContainer("value2").setVisible(false));
                    }
                    // should never come here for criteria: see ItemSearch#getSearchText()
                    if(filterHasValue()) {
                        setQueryStringFromValue(String.class);                        
                    }
                    setValueFromQueryString(String.class);
                    break;
                //==============================================================
                case STATUS:
                    setValidFilterExpressions(IN);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "multiSelect", markupContainer); 
                        // status selectable only when context space is not null
                        final Map<Integer, String> options = space.getMetadata().getStates();
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
                        choice.setModel(new PropertyModel(filterCriteria, "values"));
                    }
                    if(filterHasValueList()) {
                        if(returnCriteria) {
                            criteria.add(Restrictions.in(getNameText(), values));
                        }
                        setQueryStringFromValueList();                        
                    }
                    setStatusListFromQueryString();
                    break;
                //==============================================================
                case ASSIGNED_TO:
                case LOGGED_BY:                
                    setValidFilterExpressions(IN);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "multiSelect", markupContainer);
                        List<User> users = null;                        
                        if(space == null) {                            
                            users = jtrac.findUsersForUser(user);
                        } else {
                            users = jtrac.findUsersForSpace(space.getId());
                        }
                        JtracCheckBoxMultipleChoice choice = new JtracCheckBoxMultipleChoice("values", users, new IChoiceRenderer() {
                            public Object getDisplayValue(Object o) {
                                return ((User) o).getName();
                            }
                            public String getIdValue(Object o, int i) {
                                return ((User) o).getId() + "";
                            }
                        });
                        fragment.add(choice);
                        choice.setModel(new PropertyModel(filterCriteria, "values"));
                    }
                    if(filterHasValueList()) {
                        if(returnCriteria) {
                            criteria.add(Restrictions.in(getNameText(), filterCriteria.getValues()));
                        }
                        setQueryStringFromUserList();                        
                    }
                    setUserListFromQueryString();
                    break;
                //==============================================================
                case TIME_STAMP:
                    setValidFilterExpressions(BETWEEN, GT, LT);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "dateField", markupContainer);                    
                        YuiCalendar calendar = new YuiCalendar("value", new PropertyModel(filterCriteria, "value"), false);                    
                        fragment.add(calendar);
                        if(expression == BETWEEN) {
                            YuiCalendar calendar2 = new YuiCalendar("value2", new PropertyModel(filterCriteria, "value2"), false);                                                
                            fragment.add(calendar2);                            
                        }  else {
                            fragment.add(new WebMarkupContainer("value2").setVisible(false));
                        }                   
                    }
                    if(filterHasValue()) {
                        if(returnCriteria) {
                            switch(expression) {
                                case GT: criteria.add(Restrictions.gt(getNameText(), value)); break;
                                case LT: criteria.add(Restrictions.lt(getNameText(), value)); break;
                                case BETWEEN: 
                                    criteria.add(Restrictions.gt(getNameText(), value));
                                    criteria.add(Restrictions.lt(getNameText(), value2));
                                    break;
                                default:                            
                            }
                        }
                        setQueryStringFromValue(Date.class);                        
                    }
                    setValueFromQueryString(Date.class);
                    break;
                //==============================================================
                case SPACE:
                    setValidFilterExpressions(IN);
                    if(returnFragment) {
                        fragment = new Fragment("fragParent", "multiSelect", markupContainer);
                        List<Space> spaces = new ArrayList(user.getSpaces());
                        JtracCheckBoxMultipleChoice choice = new JtracCheckBoxMultipleChoice("values", spaces, new IChoiceRenderer() {
                            public Object getDisplayValue(Object o) {
                                return ((Space) o).getName();
                            }
                            public String getIdValue(Object o, int i) {
                                return ((Space) o).getId() + "";
                            }
                        });
                        fragment.add(choice);
                        choice.setModel(new PropertyModel(filterCriteria, "values"));
                    }
                    // should never come here for criteria: see ItemSearch#getSelectedSpaces()
                    if(filterHasValueList()) {
                        setQueryStringFromSpaceList();                        
                    }
                    setSpaceListFromQueryString();
                    break;
                //==============================================================
                default:
                    throw new RuntimeException("Unknown Column Heading " + name);   
            }
        }
    }
    
    private boolean filterHasValueList() {        
        if(filterCriteria.getExpression() != null
            && filterCriteria.getValues() != null 
            && filterCriteria.getValues().size() > 0) {
            return true;
        }
        return false;        
    }
    
    private boolean filterHasValue() {          
        Object value = filterCriteria.getValue();
        if(filterCriteria.getExpression() != null && value != null && value.toString().trim().length() > 0) {
            return true;
        }                               
        return false;
    }  
    
    private void setValidFilterExpressions(Expression... expressions) {
        validFilterExpressions = new ArrayList<Expression>();               
        for(Expression e : expressions) {
            validFilterExpressions.add(e);
        }
    }
    
    private void setQueryStringFromValueList() {
        if(!returnQueryString) {
            return;
        }
        String temp = "";
        for(Object o : filterCriteria.getValues()) {
            if(temp.length() > 0) {
                temp = temp + "_";
            }
            temp = temp + o;
        }
        queryString = temp;        
    }
    
    private void setQueryStringFromValue(Class clazz) {
        if(!returnQueryString) {
            return;
        }      
        String temp = "";
        if(clazz.equals(Date.class)) {
            temp = DateUtils.format((Date) filterCriteria.getValue());
            if(filterCriteria.getValue2() != null) {
                temp = temp + "_" + DateUtils.format((Date) filterCriteria.getValue2());
            }            
        } else {
            temp = filterCriteria.getValue() + "";
            if(filterCriteria.getValue2() != null) {
                temp = temp + "_" + filterCriteria.getValue2();
            }
        }
        queryString = temp;
    }
    
    // TODO refactor code duplication
    private void setQueryStringFromUserList() {
        if(!returnQueryString) {
            return;
        }
        String temp = "";
        for(User u : (List<User>) filterCriteria.getValues()) {
            if(temp.length() > 0) {
                temp = temp + "_";
            }
            temp = temp + u.getId();
        }
        queryString = temp;        
    }    
    
    // TODO refactor code duplication
    private void setQueryStringFromSpaceList() {
        if(!returnQueryString) {
            return;
        }
        String temp = "";
        for(Space s : (List<Space>) filterCriteria.getValues()) {
            if(temp.length() > 0) {
                temp = temp + "_";
            }
            temp = temp + s.getId();
        }
        queryString = temp;        
    }    
    
    private void setValueListFromQueryString() {
        if(queryStringTokens != null) {            
            filterCriteria.setValues(queryStringTokens);
        }        
    }
    
    // TODO refactor with more methods in filtercriteria
    private void setValueFromQueryString(Class clazz) {
        if(queryStringTokens != null) { 
            String v1 = queryStringTokens.get(0);
            String v2 = queryStringTokens.size() > 1 ? queryStringTokens.get(1) : null;            
            if(clazz.equals(Double.class)) {
                filterCriteria.setValue(new Double(v1));
                if(v2 != null) {
                    filterCriteria.setValue2(new Double(v2));
                }
            } else if(clazz.equals(Date.class)) {
                filterCriteria.setValue(DateUtils.convert(v1));
                if(v2 != null) {
                    filterCriteria.setValue2(DateUtils.convert(v2));
                }                 
            } else { // String
                filterCriteria.setValue(v1);
                if(v2 != null) {
                    filterCriteria.setValue2(v2);
                }                
            }            
        }
    }
    
    private void setUserListFromQueryString() {
        if(queryStringTokens != null) {            
            List<User> users = null;
            if(jtrac != null) {
                users = jtrac.findUsersWhereIdIn(getAsListOfLong());
            } else {
                users = jtracDao.findUsersWhereIdIn(getAsListOfLong());
            }
            filterCriteria.setValues(users);
        }        
    }  
        
    private void setSpaceListFromQueryString() {
        if(queryStringTokens != null) {            
            List<Space> temp = null;
            if(jtrac != null) {
                temp = jtrac.findSpacesWhereIdIn(getAsListOfLong());
            } else {
                temp = jtracDao.findSpacesWhereIdIn(getAsListOfLong());
            }
            // for security, prevent URL spoofing to show spaces not allocated to user            
            List<Space> spaces = new ArrayList<Space>();
            for(Space s : temp) {
                if(user.isAllocatedToSpace(s.getId())) {
                    spaces.add(s);
                }
            }
            filterCriteria.setValues(spaces);
        }        
    }     
        
    private void setStatusListFromQueryString() {
        if(queryStringTokens != null) {            
            List<Integer> statuses = new ArrayList<Integer>();
            for(String s : queryStringTokens) {
                statuses.add(new Integer(s));
            }
            filterCriteria.setValues(statuses);
        }        
    }     
    
    private List<Long> getAsListOfLong() {        
        List<Long> ids = new ArrayList<Long>();
        for(String s : queryStringTokens) {
            ids.add(new Long(s));
        } 
        return ids;
    }
    
    /* custom accessor */
    public void setName(String nameAsString) {        
        name = convertToName(nameAsString);
    }

    public String getNameText() {
        if(isField()) {
            return field.getName().getText();
        }
        return name.text;
    }

    //==========================================================================              
    
    public Name getName() {
        return name;
    }    
    
    public Field getField() {
        return field;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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
        return ch.name.equals(name);
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
