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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Component;

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
    
    // TODO remove this constructor
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
    
}
