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
 * <p>
 * In addition to the definition of custom fields, the {@link Metadata}
 * for a {@link Space} may contain a bunch of Role definitions as well.
 * </p>
 * Roles do the following:
 * <ul>
 *   <li>define the {@link State} Transitions possible (i.e. from status --> to status)</li>
 *   <li>for each {@link State} (from status) define the access permissions that this Role has per field</li>
 * </ul>
 */
public class Role implements Serializable {
    /**
     * Generated UID
     */
    private static final long serialVersionUID = 3661382262397738228L;
    
    /**
     * The name of the role.
     */
    private String name;
    
    /**
     * The description of this role.
     */
    private String description;
    
    /**
     * A {@link Map} of states assigned to this role.
     */
    private Map<Integer, State> states = new HashMap<Integer, State>();
    
    /**
     * The predefined admin role.
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    /**
     * The predefined guest role.
     */
    public static final String ROLE_GUEST = "ROLE_GUEST";
    
    /**
     * This constructor will set the {@link #name} of the role.
     * 
     * @param name The name of the role.
     */
    public Role(String name) {
        this.name = name;
    }
    
    /**
     * This constructor will read the name of the role from the given 
     * {@link Element} attribute and then read all {@link State} elements
     * to add them to the map of {@link #states}.
     * 
     * @param element The {@link Element} to read and process.
     */
    public Role(Element element) {
        this.name = element.attributeValue(NAME);
        
        for (Object o : element.elements(STATE)) {
            State state = new State((Element) o);
            states.put(state.getStatus(), state);
        } // end for each
    }
    
    /**
     * This method will append this object to an existing XML document.
     * 
     * @param parent The parent to apply this role to.
     */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(ROLE);
        copyTo(e);
    }
    
    /**
     * This method will marshal this object into a fresh new XML element.
     * 
     * @return Returns the role as XML {@link Element}.
     */
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
        // Appending empty strings to create new objects for "clone" support.
        element.addAttribute(NAME, name + "");
        
        for (State state : states.values()) {
            state.addAsChildOf(element);
        } // end for each
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
     * reserved roles (predefined system roles).
     * 
     * @param roleKey The roleKey string to check.
     * @return Returns <code>true</code> if the roleKey matches the one of the
     * reserved roles, otherwise <code>false</code>.
     */
    public static boolean isReservedRoleKey(String roleKey) {
        return (ROLE_ADMIN.equals(roleKey) || ROLE_GUEST.equals(roleKey));
    }
    
    /**
     * This method allows to add a {@link State} to the map of {@link #states}.
     * 
     * @param state The {@link State} to add to the map.
     */
    public void add(State state) {
        states.put(state.getStatus(), state);
    }
    
    /**
     * This method allows to remove the specified state id from the map of
     * {@link #states}.
     * 
     * @param stateId The state id to remove from the map of {@link #states}.
     */
    public void removeState(int stateId) {
        states.remove(stateId);
        for (State s : states.values()) {
            s.removeTransition(stateId);
        } // end for each
    }
    
    /**
     * This method will return the information if the given state key has one
     * or more transitions to other States.
     * 
     * @param stateKey The state key to check.
     * @return Returns <code>true</code> if there are one or more transitions
     * to other states, otherwise <code>false</code>.
     */
    public boolean hasTransitionsFromState(int stateKey) {
        return states.get(stateKey).getTransitions().size() > 0;
    }
    
    /**
     * This method returns the map of {@link #states}.
     * 
     * @return Returns {@link #states}.
     */
    public Map<Integer, State> getStates() {
        return states;
    }
    
    /**
     * This method allow to store a map of {@link #states}.
     * 
     * @param states The map of {@link #states} to store.
     */
    public void setStates(Map<Integer, State> states) {
        this.states = states;
    }
    
    /**
     * This method will return the {@link #description} of this role.
     * 
     * @return The {@link #description} or <code>null</code>.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * This method allows to store the {@link #description} of this role.
     * 
     * @param description The {@link #description} of this role.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * This method will return the {@link #name} of this role.
     * 
     * @return The {@link #name} of this role.
     */
    public String getName() {
        return name;
    }
    
    /**
     * This method allows to store the {@link #name} of this role.
     * 
     * @param name The {@link #name} of this role.
     */
    public void setName(String name) {
        this.name = name;
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
        sb.append("name [").append(name);
        sb.append("]; states [").append(states);
        sb.append("]");
        return sb.toString();
    }
}
