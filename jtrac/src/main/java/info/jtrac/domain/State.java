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

import info.jtrac.util.XmlUtils;

import java.util.HashSet;

import static info.jtrac.Constants.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.dom4j.Element;

/**
 * State as in "State Transition"
 * holds a set of possible future states to transition to
 * also holds a map of [ field name = integer "mask" ]
 * to represent permissions (view or edit) that the role owning this state
 * has for each field for an item which is in this particular state
 *
 * For example, consider a state FOO and a role BAR.  
 * When a user with role BAR views an item that is having the status FOO:
 * ie. when item.status == FOO.status, the fields that can be viewed on screen
 * will be the entries in FOO.fields where the value == MASK_VIEW (or 1)
 */
public class State implements Serializable {
    
    private int status;
    private Set<Integer> transitions = new HashSet<Integer>();
    private Map<Field.Name, Integer> fields = new HashMap<Field.Name, Integer>();
    
    public static final int NEW = 0;
    public static final int OPEN = 1;
    public static final int CLOSED = 99;
    
    public static final int MASK_HIDE = 0;
    public static final int MASK_VIEW = 1;
    public static final int MASK_EDIT = 2;
    
    public State() {
        // zero arg constructor
    }
    
    public State(int s) {
        this.status = s;        
    }
    
    public State(Element e) {
        this.status = Integer.parseInt(e.attributeValue(STATUS));
        for (Object o : e.elements(TRANSITION)) {
            Element t = (Element) o;
            transitions.add(new Integer(t.attributeValue(STATUS)));
        }
        for (Object o : e.elements(FIELD)) {
            Element f = (Element) o;
            String fieldName = f.attributeValue(NAME);
            fields.put(Field.convertToName(fieldName), new Integer(f.attributeValue(MASK)));
        }         
    }
    
    /* append this object onto an existing XML document */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(STATE);
        copyTo(e);
    }    
    
    /* marshal this object into a fresh new XML Element */
    public Element getAsElement() {
        Element e = XmlUtils.getNewElement(STATE);
        copyTo(e);
        return e;
    }
    
    /* copy object values into an existing XML Element */
    private void copyTo(Element e) {
        // appending empty strings to create new objects for "clone" support
        e.addAttribute(STATUS, status + "");
        for (Integer toStatus : transitions) {                
            Element t = e.addElement(TRANSITION);
            t.addAttribute(STATUS, toStatus + "");
        }
        for (Map.Entry<Field.Name, Integer> entry : fields.entrySet()) {                            
            Element f = e.addElement(FIELD);
            f.addAttribute(NAME, entry.getKey() + "");
            f.addAttribute(MASK, entry.getValue() + "");
        }        
    }
    
    //=======================================================================
    
    public void add(Collection<Field.Name> fieldNames) {
        for (Field.Name fieldName : fieldNames) {
            add(fieldName);
        }
    }    
    
    public void add(Field.Name fieldName) {
        int mask = MASK_VIEW;
        // for NEW states, normally all Fields on the Item are editable
        if (status == NEW) {
            mask = MASK_EDIT;
        }
        fields.put(fieldName, mask);
    }
    
    public void remove(Field.Name fieldName) {
        fields.remove(fieldName);
    }
    
    public void addTransition(int toStatus) {
        transitions.add(toStatus);
    }
    
    public void removeTransition(int toStatus) {
        transitions.remove(toStatus);
    }
    
    /**
     * to make JSTL EL easier
     * create Map on the fly but with boolean true values for keys that are present
     */
    public Map<Integer, Boolean> getTransitionMap() {
        Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (Integer i : transitions) {
            map.put(i, true);
        }
        return map;
    }    
    
    //=======================================================================   

    public Map<Field.Name, Integer> getFields() {
        return fields;
    }

    public void setFields(Map<Field.Name, Integer> fields) {
        this.fields = fields;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public Set<Integer> getTransitions() {
        return transitions;
    }

    public void setTransitions(Set<Integer> transitions) {
        this.transitions = transitions;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("status [").append(status);
        sb.append("]; transitions [").append(transitions);
        sb.append("]; fields [").append(fields);
        sb.append("]");
        return sb.toString();
    }
    
}
