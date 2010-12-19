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

import static info.jtrac.Constants.*;

import info.jtrac.util.XmlUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Element;

/**
 * In addition to definition of custom fields, the Metadata
 * for a Space may contain a bunch of Role definitions as well.
 * Roles do the following
 * - define the State Transitions possible (i.e. from status --> to status)
 * - for each State (from status) define the access permissions that this Role has per Field
 */
public class Role implements Serializable {
    /**
     * Generated UID
     */
    private static final long serialVersionUID = 3661382262397738228L;
    
    private String name;
    private String description;
    private Map<Integer, State> states = new HashMap<Integer, State>();
    
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_GUEST = "ROLE_GUEST";
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role(Element e) {
        name = e.attributeValue(NAME);
        
        for (Object o : e.elements(STATE)) {
            State state = new State((Element) o);
            states.put(state.getStatus(), state);
        } // end for
    }
    
    /**
     * Append this object onto an existing XML document.
     */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(ROLE);
        copyTo(e);
    }
    
    /**
     * Marshal this object into a fresh new XML element.
     * */
    public Element getAsElement() {
        Element e = XmlUtils.getNewElement(ROLE);
        copyTo(e);
        return e;
    }
    
    /**
     * Copy object values into an existing XML element.
     * 
     * @param element The {@link Element} object to append.
     */
    private void copyTo(Element element) {
        // appending empty strings to create new objects for "clone" support
        element.addAttribute(NAME, name + "");
        
        for (State state : states.values()) {
            state.addAsChildOf(element);
        } // end for
    }
    
    /**
     * This method is used to verify if the given roleKey matches the
     * the role {@link #ROLE_ADMIN}.
     * 
     * @param roleKey The roleKey string to check.
     * @return Returns <code>true</code> if the roleKey matches the role
     * {@link #ROLE_ADMIN}, otherwise <code>false</code>.
     */
    public static boolean isAdmin(String roleKey) {
        return ROLE_ADMIN.equals(roleKey);
    }
    
    /**
     * This method is used to verify if the given roleKey matches the
     * the role {@link #ROLE_GUEST}.
     * 
     * @param roleKey The roleKey string to check.
     * @return Returns <code>true</code> if the roleKey matches the role
     * {@link #ROLE_GUEST}, otherwise <code>false</code>.
     */
    public static boolean isGuest(String roleKey) {
        return ROLE_GUEST.equals(roleKey);
    }
    
    /**
     * This method is used to verify if the given roleKey matches one of the
     * reserved roles (system defined roles).
     * 
     * @param roleKey The roleKey string to check.
     * @return Returns <code>true</code> if the roleKey matches the one of the
     * reserved roles, otherwise <code>false</code>.
     */
    public static boolean isReservedRoleKey(String roleKey) {
        return (ROLE_ADMIN.equals(roleKey) || ROLE_GUEST.equals(roleKey));
    }
    
    /**
     * 
     * @param state
     */
    public void add(State state) {
        states.put(state.getStatus(), state);
    }
    
    /**
     * 
     * @param stateId
     */
    public void removeState(int stateId) {
        states.remove(stateId);
        for(State s : states.values()) {
            s.removeTransition(stateId);
        }
    }
    
    /**
     * 
     * @param stateKey
     * @return
     */
    public boolean hasTransitionsFromState(int stateKey) {
        return states.get(stateKey).getTransitions().size() > 0;
    }
    
    /**
     * 
     * @return
     */
    public Map<Integer, State> getStates() {
        return states;
    }
    
    /**
     * 
     * @param states
     */
    public void setStates(Map<Integer, State> states) {
        this.states = states;
    }
    
    /**
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("name [").append(name);
        sb.append("]; states [").append(states);
        sb.append("]");
        return sb.toString();
    }
}
