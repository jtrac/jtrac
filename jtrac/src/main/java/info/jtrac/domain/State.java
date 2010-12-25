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
 * <p>
 * State as in "State Transition" holds a set of possible future states to
 * transition. It also holds a map of <code>[field name = integer "mask"]</code>
 * to represent permissions (view or edit) that the {@link Role} owning this
 * state has for each field for an item which is in this particular state.
 * </p>
 * <p>
 * For example, consider a state FOO and a role BAR.<br />
 * When a {@link User} with {@link Role} BAR views an item that is having the
 * status FOO:
 * ie. when <code>item.status == FOO.status</code>, the fields that can be viewed on screen
 * will be the entries in FOO.fields where the value == {@link #MASK_READONLY} (1)
 * </p>
 */
public class State implements Serializable {
    /**
     * Generated UID
     */
    private static final long serialVersionUID = -6321200313541210811L;
    
    /**
     * The status of the state.
     */
    private int status;
    
    /**
     * The {@link Set} of transitions to other {@link State} objects.
     */
    private Set<Integer> transitions = new HashSet<Integer>();
    
    /**
     * The {@link Map} of {@link Field} objects.
     */
    private Map<Field.Name, Integer> fields = new HashMap<Field.Name, Integer>();
    
    /**
     * The predefined NEW state.
     */
    public static final int NEW = 0;
    
    /**
     * The predefined OPEN state.
     */
    public static final int OPEN = 1;
    
    /**
     * The predefined CLOSED state.
     */
    public static final int CLOSED = 99;
    
    /**
     * The predefined HIDDEN mask state.
     */
    public static final int MASK_HIDDEN = 0;
    public static final int MASK_READONLY = 1;
    public static final int MASK_OPTIONAL = 2;
    public static final int MASK_MANDATORY = 3;
    
    /**
     * Default empty constructor.
     */
    public State() {
    }
    
    /**
     * This constructor will set the {@link #status} of this state.
     * 
     * @param status The status of the state.
     */
    public State(int status) {
        this.status = status;
    }
    
    /**
     * This constructor will read the status of the state from the given 
     * {@link Element} attribute. Then all Transitions elements are read
     * to add them to the map of {@link #transitions}. Finally all Field
     * elements are read to add them to the map of {@link #fields}.
     * 
     * @param element The {@link Element} to read and process.
     */
    public State(Element element) {
        this.status = Integer.parseInt(element.attributeValue(STATUS));
        
        for (Object o : element.elements(TRANSITION)) {
            Element t = (Element) o;
            transitions.add(new Integer(t.attributeValue(STATUS)));
        } // end for each
        
        for (Object o : element.elements(FIELD)) {
            Element f = (Element) o;
            String fieldName = f.attributeValue(NAME);
            fields.put(Field.convertToName(fieldName), new Integer(f.attributeValue(MASK)));
        } // end for each
    }
    
    /**
     * This method will append this object to an existing XML document.
     * 
     * @param parent The parent to apply this state to.
     */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(STATE);
        copyTo(e);
    }    
    
    /**
     * This method will marshal this object into a fresh new XML element.
     * 
     * @return Returns the state as XML {@link Element}.
     */
    public Element getAsElement() {
        Element e = XmlUtils.getNewElement(STATE);
        copyTo(e);
        return e;
    }
    
    /**
     * Copy object values into an existing XML element.
     * 
     * @param element The {@link Element} object to append.
     */
    private void copyTo(Element element) {
        // Appending empty strings to create new objects for "clone" support
        element.addAttribute(STATUS, status + "");
        
        for (Integer toStatus : transitions) {
            Element t = element.addElement(TRANSITION);
            t.addAttribute(STATUS, toStatus + "");
        } // end for each
        
        for (Map.Entry<Field.Name, Integer> entry : fields.entrySet()) {
            Element f = element.addElement(FIELD);
            f.addAttribute(NAME, entry.getKey() + "");
            f.addAttribute(MASK, entry.getValue() + "");
        } // end for each
    }
    
    /**
     * This method allows to add a {@link State} to the map of {@link #fields}.
     * 
     * @param fieldNames The {@link Collection} of {@link Field.Name} objects
     * to add to the map.
     */
    public void add(Collection<Field.Name> fieldNames) {
        for (Field.Name fieldName : fieldNames) {
            add(fieldName);
        } // end for each
    }
    
    /**
     * This method allows to add a {@link State} to the map of {@link #fields}.
     * 
     * @param fieldName The {@link Field.Name} to add to the map.
     */
    public void add(Field.Name fieldName) {
        int mask = MASK_READONLY;
        // For NEW states, normally all Fields on the Item are editable
        if (status == NEW) {
            mask = MASK_MANDATORY;
        }
        fields.put(fieldName, mask);
    }
    
    /**
     * This method allows to remove the specified field name from the map of
     * {@link #fields}.
     * 
     * @param fieldName The field name to remove from the map of {@link #fields}.
     */
    public void remove(Field.Name fieldName) {
        fields.remove(fieldName);
    }
    
    /**
     * This method allows to add a transition to the set of {@link #transitions}.
     * 
     * @param toStatus The transition to add to the set.
     */
    public void addTransition(int toStatus) {
        transitions.add(toStatus);
    }
    
    /**
     * This method allows to remove the specified transition from the set of
     * {@link #transitions}.
     * 
     * @param toStatus The transition to remove from the set of {@link #transitions}.
     */
    public void removeTransition(int toStatus) {
        transitions.remove(toStatus);
    }
    
    /**
     * This method returns the map of {@link #fields}.
     * 
     * @return Returns {@link #fields}.
     */
    public Map<Field.Name, Integer> getFields() {
        return fields;
    }
    
    /**
     * This method allow to store a map of {@link #fields}.
     * 
     * @param fields The map of {@link #fields} to store.
     */
    public void setFields(Map<Field.Name, Integer> fields) {
        this.fields = fields;
    }
    
    /**
     * This method returns the {@link #status} of the state.
     * 
     * @return Returns {@link #status}.
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * This method allows to store the {@link #status} of the state.
     * 
     * @param status The {@link #status} of the state.
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * This method returns the set of {@link #transitions}.
     * 
     * @return Returns {@link #transitions}.
     */
    public Set<Integer> getTransitions() {
        return transitions;
    }
    
    /**
     * This method allow to store a set of {@link #transitions}.
     * 
     * @param fields The set of {@link #transitions} to store.
     */
    public void setTransitions(Set<Integer> transitions) {
        this.transitions = transitions;
    }
    
    /**
     * This method overrides the default {@link Object#toString()} method to
     * return the string representation of this object.
     * 
     * @return Returns a string representation of the object.
     */
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
