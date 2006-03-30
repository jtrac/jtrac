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
 * for a Space may contain a bunch of Role defintions as well.
 * Roles do the following
 * - define the State Transitions possible (i.e. from status --> to status)
 * - for each State (from status) define the access permissions that this Role has per Field
 */
public class Role implements Serializable {
    
    private String name;
    private String description;
    private Map<Integer, State> states = new HashMap<Integer, State>();
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role(Element e) {
        name = e.attributeValue(NAME);
        for (Object o : e.elements(STATE)) {
            State state = new State((Element) o);
            states.put(state.getStatus(), state);
        }
    }
    
    /* append this object onto an existing XML document */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(ROLE);
        copyTo(e);
    }
    
    /* marshal this object into a fresh new XML Element */
    public Element getAsElement() {
        Element e = XmlUtils.getNewElement(ROLE);
        copyTo(e);
        return e;
    }
    
    /* copy object values into an existing XML Element */
    private void copyTo(Element e) {
        // appending empty strings to create new objects for "clone" support
        e.addAttribute(NAME, name + "");
        for (State state : states.values()) {
            state.addAsChildOf(e);
        }
    }
    
    //=======================================================================
    
    public void add(State state) {
        states.put(state.getStatus(), state);
    }
    
    //=======================================================================
    
    public Map<Integer, State> getStates() {
        return states;
    }
    
    public void setStates(Map<Integer, State> states) {
        this.states = states;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("name [").append(name);
        sb.append("]; states [").append(states);
        sb.append("]");
        return sb.toString();
    }
    
}
