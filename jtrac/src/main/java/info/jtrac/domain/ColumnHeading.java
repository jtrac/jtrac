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
    private Processor processor;
    
    private FilterCriteria filterCriteria = new FilterCriteria();             
    
    private ColumnHeading(Name name) {
        this.name = name;    
        if(name == DETAIL || name == SPACE) {
            visible = false;
        }      
        processor = getProcessor();
    }
    
    public ColumnHeading(Field field) {
        this.field = field;        
        this.label = field.getLabel();
        processor = getProcessor();
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
    
    public List<Expression> getValidFilterExpressions() {
        return processor.getValidFilterExpressions();
    }
        
    public Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
        return processor.getFilterUiFragment(container, user, space, jtrac);        
    } 
    
    public void addRestrictions(DetachedCriteria criteria) {
        processor.addRestrictions(criteria);
    }
    
    public String getAsQueryString() {
        return processor.getAsQueryString();
    }
    
    public void loadFromQueryString(String s, User user, Jtrac jtrac) {
        processor.loadFromQueryString(s, user, jtrac);
    }    
    
    /**
     * also see description below for the private getProcessor() method
     */
    private abstract class Processor implements Serializable {  
        
        /* return the possible expressions (equals, greater-than etc) to show on filter UI for selection */
        abstract List<Expression> getValidFilterExpressions();
        
        /* return the wicket ui fragment that will be shown over ajax (based on selected expression) */
        abstract Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac);
        
        /* get as hibernate restriction and append to passed in criteria that will be used to query the database */
        abstract void addRestrictions(DetachedCriteria criteria);
        
        /* return a querystring representation of the filter criteria to create a bookmarkable url */
        abstract String getAsQueryString();                
        
        /* load a querystring representation and initialize filter critera when acting on a bookmarkable url */
        abstract void loadFromQueryString(String s, User user, Jtrac jtrac);     
        
    }    
        
    /**
     * this routine is a massive if-then construct that acts as a factory for the
     * right implementation of the responsibilities defined in the "Processor" class (above)
     * based on the type of ColumnHeading - the right implementation will be returned.
     * having everything in one place below, makes it easy to maintain, as the
     * logic of each of the methods are closely interdependent for a given column type
     * for e.g. the kind of hibernate criteria needed depends on what is made available on the UI
     */
    private Processor getProcessor() {                                              
        if(isField()) {            
            switch(field.getName().getType()) {
                //==============================================================
                case 1:
                case 2:
                case 3:                
                    return new Processor() {                        
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(IN);
                        }                        
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "multiSelect", container);
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
                            return fragment;
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValueList()) {      
                                List values = filterCriteria.getValues();
                                List<Integer> keys = new ArrayList<Integer>(values.size());
                                for(Object o : values) {
                                    keys.add(new Integer(o.toString()));
                                }
                                criteria.add(Restrictions.in(getNameText(), keys));
                            }
                        }
                        String getAsQueryString() {                            
                            return getQueryStringFromValueList();                             
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueListFromQueryString(s);
                        }                        
                    };
                //==============================================================
                case 4: // decimal number  
                    return new Processor() {                        
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(EQ, NOT_EQ, GT, LT, BETWEEN);
                        }                        
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "textField", container);
                            TextField textField = new TextField("value", Double.class);
                            textField.setModel(new PropertyModel(filterCriteria, "value"));
                            fragment.add(textField);
                            if(filterCriteria.getExpression() == BETWEEN) {
                                TextField textField2 = new TextField("value2", Double.class);
                                textField.setModel(new PropertyModel(filterCriteria, "value2"));
                                fragment.add(textField2);                            
                            } else {
                                fragment.add(new WebMarkupContainer("value2").setVisible(false));
                            }
                            return fragment;
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValue()) {
                                Object value = filterCriteria.getValue();
                                switch(filterCriteria.getExpression()) {
                                    case EQ: criteria.add(Restrictions.eq(getNameText(), value)); break;
                                    case NOT_EQ: criteria.add(Restrictions.not(Restrictions.eq(name.text, value))); break;
                                    case GT: criteria.add(Restrictions.gt(getNameText(), value)); break;
                                    case LT: criteria.add(Restrictions.lt(getNameText(), value)); break;
                                    case BETWEEN: 
                                        criteria.add(Restrictions.gt(getNameText(), value));
                                        criteria.add(Restrictions.lt(getNameText(), filterCriteria.getValue2()));
                                        break;
                                    default:                            
                                }                                
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(Double.class);
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, Double.class);
                        }
                        
                    };                    
                //==============================================================
                case 6: // date
                    return new Processor() {                        
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(EQ, NOT_EQ, GT, LT, BETWEEN);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "dateField", container);                        
                            YuiCalendar calendar = new YuiCalendar("value", new PropertyModel(filterCriteria, "value"), false);                                                
                            fragment.add(calendar);
                            if(filterCriteria.getExpression() == BETWEEN) {
                                YuiCalendar calendar2 = new YuiCalendar("value2", new PropertyModel(filterCriteria, "value2"), false);                                                
                                fragment.add(calendar2);                            
                            } else {
                                fragment.add(new WebMarkupContainer("value2").setVisible(false));
                            }     
                            return fragment;
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValue()) {
                                Object value = filterCriteria.getValue();
                                switch(filterCriteria.getExpression()) {
                                    case EQ: criteria.add(Restrictions.eq(getNameText(), value)); break;
                                    case NOT_EQ: criteria.add(Restrictions.not(Restrictions.eq(getNameText(), value))); break;
                                    case GT: criteria.add(Restrictions.gt(getNameText(), value)); break;
                                    case LT: criteria.add(Restrictions.lt(getNameText(), value)); break;
                                    case BETWEEN: 
                                        criteria.add(Restrictions.gt(getNameText(), value));
                                        criteria.add(Restrictions.lt(getNameText(), filterCriteria.getValue2()));
                                        break;
                                    default:                            
                                }                                
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(Date.class);
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, Date.class);
                        }                        
                    };                  
                //==============================================================
                case 5: // free text 
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(CONTAINS);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            return getTextFieldFragment(container);
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValue()) {
                                criteria.add(Restrictions.ilike(getNameText(), 
                                        (String) filterCriteria.getValue(), MatchMode.ANYWHERE));
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(String.class);
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, String.class);
                        }                        
                    };                                      
                //==============================================================
                default:
                    throw new RuntimeException("Unknown Column Heading " + name);
            }        
        } else { // this is not a custom field but one of the "built-in" columns           
            switch(name) {
                //==============================================================
                case ID:
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(EQ);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            return getTextFieldFragment(container);
                        }
                        void addRestrictions(DetachedCriteria criteria) {   
                            if(filterHasValue()) {
                                // should never come here for criteria: see ItemSearch#getRefId()
                                throw new RuntimeException("should not come here for 'id'");
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(String.class);
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, String.class);
                        }                        
                    };                                                            
                //==============================================================
                case SUMMARY:  
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(CONTAINS);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            return getTextFieldFragment(container);
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValue()) {
                                criteria.add(Restrictions.ilike(getNameText(), (String) filterCriteria.getValue(), MatchMode.ANYWHERE));
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(String.class);
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, String.class);
                        }
                    };                               
                //==============================================================
                case DETAIL:
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(CONTAINS);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            return getTextFieldFragment(container);
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValue()) {
                                // should never come here for criteria: see ItemSearch#getSearchText()
                                throw new RuntimeException("should not come here for 'detail'");
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(String.class);
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, String.class);
                        }
                    };

                //==============================================================
                case STATUS:
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(IN);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "multiSelect", container); 
                            // status selectable only when context space is not null
                            final Map<Integer, String> options = space.getMetadata().getStatesMap();
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
                            return fragment;
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValueList()) {
                                criteria.add(Restrictions.in(getNameText(), filterCriteria.getValues()));
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValueList(); 
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setStatusListFromQueryString(s);
                        }
                    };                    
                //==============================================================
                case ASSIGNED_TO:
                case LOGGED_BY:           
                    return new Processor() {                        
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(IN);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "multiSelect", container);
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
                            return fragment;
                        }                                                    
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValueList()) {
                                criteria.add(Restrictions.in(getNameText(), filterCriteria.getValues()));
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromUserList();
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setUserListFromQueryString(s, jtrac);
                        }                        
                    };                    
                //==============================================================
                case TIME_STAMP:
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(BETWEEN, GT, LT);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "dateField", container);                    
                            YuiCalendar calendar = new YuiCalendar("value", new PropertyModel(filterCriteria, "value"), false);                    
                            fragment.add(calendar);
                            if(filterCriteria.getExpression() == BETWEEN) {
                                YuiCalendar calendar2 = new YuiCalendar("value2", new PropertyModel(filterCriteria, "value2"), false);                                                
                                fragment.add(calendar2);                            
                            }  else {
                                fragment.add(new WebMarkupContainer("value2").setVisible(false));
                            }              
                            return fragment;
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValue()) {
                                Object value = filterCriteria.getValue();
                                switch(filterCriteria.getExpression()) {
                                    case GT: criteria.add(Restrictions.gt(getNameText(), value)); break;
                                    case LT: criteria.add(Restrictions.lt(getNameText(), value)); break;
                                    case BETWEEN: 
                                        criteria.add(Restrictions.gt(getNameText(), value));
                                        criteria.add(Restrictions.lt(getNameText(), filterCriteria.getValue2()));
                                        break;
                                    default:                            
                                }                                
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromValue(Date.class);  
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setValueFromQueryString(s, Date.class);
                        }
                    };
                //==============================================================
                case SPACE:
                    return new Processor() {
                        List<Expression> getValidFilterExpressions() {
                            return getAsList(IN);
                        }
                        Fragment getFilterUiFragment(MarkupContainer container, User user, Space space, Jtrac jtrac) {
                            Fragment fragment = new Fragment("fragParent", "multiSelect", container);
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
                            return fragment;
                        }
                        void addRestrictions(DetachedCriteria criteria) {
                            if(filterHasValueList()) {
                                // should never come here for criteria: see ItemSearch#getSelectedSpaces()
                                throw new RuntimeException("should not come here for 'space'");
                            }
                        }
                        String getAsQueryString() {
                            return getQueryStringFromSpaceList();
                        }
                        void loadFromQueryString(String s, User user, Jtrac jtrac) {
                            setSpaceListFromQueryString(s, user, jtrac);
                        }                        
                    };
                //==============================================================
                default:
                    throw new RuntimeException("Unknown Column Heading " + name);   
            }
        }        
    }
    
    private List<Expression> getAsList(Expression... expressions) {
        List<Expression> list = new ArrayList<Expression>();               
        for(Expression e : expressions) {
            list.add(e);
        }
        return list;
    }        
    
    private Fragment getTextFieldFragment(MarkupContainer container) {
        Fragment fragment = new Fragment("fragParent", "textField", container);
        TextField textField = new TextField("value", String.class);
        textField.setModel(new PropertyModel(filterCriteria, "value"));
        fragment.add(textField);
        fragment.add(new WebMarkupContainer("value2").setVisible(false));       
        return fragment;
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
    
    private String prependExpression(String s) {
        return filterCriteria.getExpression().getKey() + "_" + s;
    }
    
    private String getQueryStringFromValueList() {
        if(!filterHasValueList()) {
            return null;
        }        
        String temp = "";
        for(Object o : filterCriteria.getValues()) {
            if(temp.length() > 0) {
                temp = temp + "_";
            }
            temp = temp + o;
        }
        return prependExpression(temp);       
    }
    
    private String getQueryStringFromValue(Class clazz) {    
        if(!filterHasValue()) {
            return null;
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
        return prependExpression(temp);
    }
    
    // TODO refactor code duplication
    private String getQueryStringFromUserList() {
        if(!filterHasValueList()) {
            return null;
        }          
        String temp = "";
        for(User u : (List<User>) filterCriteria.getValues()) {
            if(temp.length() > 0) {
                temp = temp + "_";
            }
            temp = temp + u.getId();
        }
        return prependExpression(temp);        
    }    
    
    // TODO refactor code duplication
    private String getQueryStringFromSpaceList() {
        if(!filterHasValueList()) {
            return null;
        }          
        String temp = "";
        for(Space s : (List<Space>) filterCriteria.getValues()) {
            if(temp.length() > 0) {
                temp = temp + "_";
            }
            temp = temp + s.getId();
        }
        return prependExpression(temp);        
    }    
    
    private List<String> setExpressionAndGetRemainingTokens(String s) {
        String [] tokens = s.split("_");
        filterCriteria.setExpression(FilterCriteria.convertToExpression(tokens[0]));
        List<String> remainingTokens = new ArrayList<String>();
        // ignore first token, this has been parsed as Expression above
        for(int i = 1; i < tokens.length; i++ ) {
            remainingTokens.add(tokens[i]);    
        }
        return remainingTokens;
    }     
    
    private void setValueListFromQueryString(String raw) {        
        filterCriteria.setValues(setExpressionAndGetRemainingTokens(raw));        
    }
    
    // TODO refactor with more methods in filtercriteria
    private void setValueFromQueryString(String raw, Class clazz) {        
        List<String> tokens = setExpressionAndGetRemainingTokens(raw);
        String v1 = tokens.get(0);
        String v2 = tokens.size() > 1 ? tokens.get(1) : null;            
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
    
    private void setUserListFromQueryString(String raw, Jtrac jtrac) {
        List<String> tokens = setExpressionAndGetRemainingTokens(raw);                    
        List<User> users = jtrac.findUsersWhereIdIn(getAsListOfLong(tokens));
        filterCriteria.setValues(users);        
    }  
        
    private void setSpaceListFromQueryString(String raw, User user, Jtrac jtrac) {
        List<String> tokens = setExpressionAndGetRemainingTokens(raw);                    
        List<Space> temp = jtrac.findSpacesWhereIdIn(getAsListOfLong(tokens));
        // for security, prevent URL spoofing to show spaces not allocated to user            
        List<Space> spaces = new ArrayList<Space>();
        for(Space s : temp) {
            if(user.isAllocatedToSpace(s.getId())) {
                spaces.add(s);
            }
        }
        filterCriteria.setValues(spaces);        
    }     
        
    private void setStatusListFromQueryString(String raw) {
        List<String> tokens = setExpressionAndGetRemainingTokens(raw);           
        List<Integer> statuses = new ArrayList<Integer>();
        for(String s : tokens) {
            statuses.add(new Integer(s));
        }
        filterCriteria.setValues(statuses);        
    }     
    
    private List<Long> getAsListOfLong(List<String> tokens) {        
        List<Long> ids = new ArrayList<Long>();
        for(String s : tokens) {
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
