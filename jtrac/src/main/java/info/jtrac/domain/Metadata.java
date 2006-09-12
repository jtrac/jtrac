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
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import org.dom4j.Document;
import org.dom4j.Element;

/**
 * XML metadata is one of the interesting design decisions of JTrac.
 * Metadata is defined for each space and so Items that belong to a
 * space are customized by the space metadata.
 * 
 * 1) custom Fields for an Item (within a Space)
 * - Label
 * - whether mandatory or not
 * - the option values (drop down list options)
 * - the option "key" values are stored in the database (WITHOUT any relationships)
 * - the values corresponding to "key"s are resolved from the Metadata
 *   and not through a database join.
 *
 * 2) the Roles available within a space
 * - for each (from) State the (to) State transitions allowed for this role
 * - and within each (from) State the fields that this Role can view / edit
 *
 * 3) the State labels corresponding to each state 
 * - internally States are integers, but for display we need a label
 * - labels can be customized
 * - special State values: 0 = New, 1 = Open, 99 = Closed
 *
 * 4) the order in which the fields are displayed
 * on the data entry screens and the query result screens etc.
 *
 * Metadata can be inherited, and this allows for "reuse" TODO
 */
public class Metadata implements Serializable {    
    
    private long id;
    private int version;
    private Integer type;
    private String name;
    private String description;
    private Metadata parent;

    private Map<Field.Name, Field> fields;
    private Map<String, Role> roles;
    private Map<Integer, String> states;
    private List<Field.Name> fieldOrder;    
    
    public Metadata() {
        init();
    }
    
    private void init() {
        fields = new EnumMap<Field.Name, Field>(Field.Name.class);
        roles = new HashMap<String, Role>();
        states = new TreeMap<Integer, String>();
        fieldOrder = new LinkedList<Field.Name>();         
    }
    
    /* accessor, will be used by Hibernate */
    public void setXml(String xmlString) {
        init();
        if (xmlString == null) {
            return;
        }
        Document document = XmlUtils.parse(xmlString);        
        for (Element e : (List<Element>) document.selectNodes(FIELD_XPATH)) {
            Field field = new Field(e);            
            fields.put(field.getName(), field);
        }       
        for (Element e : (List<Element>) document.selectNodes(ROLE_XPATH)) {
            Role role = new Role(e);            
            roles.put(role.getName(), role);
        }
        for (Element e : (List<Element>) document.selectNodes(STATE_XPATH)) {
            String key = e.attributeValue(STATUS);
            String value = e.attributeValue(LABEL);
            states.put(Integer.parseInt(key), value);
        }        
        for (Element e : (List<Element>) document.selectNodes(FIELD_ORDER_XPATH)) {
            String fieldName = e.attributeValue(NAME);
            fieldOrder.add(Field.convertToName(fieldName));
        }         
    }        
    
    /* accessor, will be used by Hibernate */
    public String getXml() {
        Document d = XmlUtils.getNewDocument(METADATA);
        Element root = d.getRootElement();
        Element fs = root.addElement(FIELDS);
        for (Field field : fields.values()) {
            field.addAsChildOf(fs);
        }        
        Element rs = root.addElement(ROLES);
        for (Role role : roles.values()) {
            role.addAsChildOf(rs);
        }        
        Element ss = root.addElement(STATES);
        for (Map.Entry<Integer, String> entry : states.entrySet()) {
            Element e = ss.addElement(STATE);
            e.addAttribute(STATUS, entry.getKey() + "");
            e.addAttribute(LABEL, entry.getValue());
        }
        Element fo = fs.addElement(FIELD_ORDER);
        for (Field.Name f : fieldOrder) {
            Element e = fo.addElement(FIELD);
            e.addAttribute(NAME, f.toString());
        }          
        return d.asXML();
    }
    
    public String getPrettyXml() {
        return XmlUtils.getAsPrettyXml(getXml());
    }
    
    //====================================================================
    
    public void initRoles() {
        states.put(State.NEW, "New");
        states.put(State.OPEN, "Open");
        states.put(State.CLOSED, "Closed");
        addRole("DEFAULT");
    }
    
    public Field getField(String name) {
        return fields.get(Field.convertToName(name));
    }        
    
    public void add(Field field) {
        fields.put(field.getName(), field); // will overwrite if exists
        if (!fieldOrder.contains(field.getName())) { // but for List, need to check
            fieldOrder.add(field.getName());
        }
        for (Role role : roles.values()) {
            for (State state : role.getStates().values()) {
                state.add(field.getName());
            }
        }
    }
    
    public void removeField(String fieldName) {
        Field.Name name = Field.convertToName(fieldName);
        fields.remove(name);
        fieldOrder.remove(name);
        for (Role role : roles.values()) {
            for (State state : role.getStates().values()) {
                state.remove(name);
            }
        }        
    }
    
    public void addState(String name) {
        // first get the max of existing state keys
        int maxStatus = 0;
        for (int status : states.keySet()) {
            if (status > maxStatus && status != State.CLOSED) {
                maxStatus = status;
            }
        }
        int newStatus = maxStatus + 1;
        states.put(newStatus, name);
        // by default each role will have permissions for this state, for all fields
        for (Role role : roles.values()) {
            State state = new State(newStatus);
            state.add(fields.keySet());
            role.add(state);
        }
    }
    
    public void addRole(String name) {
        Role role = new Role(name);
        for (Map.Entry<Integer, String> entry : states.entrySet()) {
            State state = new State(entry.getKey());
            state.add(fields.keySet());
            role.add(state);
        }
        roles.put(role.getName(), role);
    }
    
    public Set<Field.Name> getUnusedFieldNames() {
        EnumSet<Field.Name> allFieldNames = EnumSet.allOf(Field.Name.class);
        for (Field f : getFields().values()) {
            allFieldNames.remove(f.getName());
        }
        return allFieldNames;
    }
    
    public Map<String, String> getAvailableFieldTypes() {
        Map<String, String> fieldTypes = new LinkedHashMap<String, String>();
        for (Field.Name fieldName : getUnusedFieldNames()) {
            fieldTypes.put(fieldName.getType() + "", fieldName.getDescription());
        }
        return fieldTypes;        
    }
    
    public Field getNextAvailableField(int type) {
        for (Field.Name fieldName : getUnusedFieldNames()) {
            if (fieldName.getType() == type) {
                return new Field(fieldName + "");
            }
        }
        throw new RuntimeException("No field available of type " + type);
    }
    
    // customized accessor
    public Map<Field.Name, Field> getFields() {
        Map<Field.Name, Field> map = fields;
        if (parent != null) {
            map.putAll(parent.getFields());
        }
        return map;
    }    
    
    // to make JSTL easier
    public Collection<Role> getRoleList() {
        return roles.values();
    }
    
    public List<Field> getFieldList() {
        Map<Field.Name, Field> map = getFields();
        List<Field> list = new ArrayList<Field>(fields.size());
        for (Field.Name fieldName : getFieldOrder()) {
            list.add(fields.get(fieldName));
        }
        return list;
    } 
    
    public String getCustomValue(Field.Name fieldName, Integer key) {
        return getCustomValue(fieldName,  key + "");
    }
    
    public String getCustomValue(Field.Name fieldName, String key) {
        Field field = fields.get(fieldName);
        if (field != null) {
            return field.getCustomValue(key);
        }
        if (parent != null) {
            return parent.getCustomValue(fieldName, key);
        }
        return "";        
    }
    
    public String getStatusValue(Integer key) {
        if (key == null) {
            return "";
        }
        String s = states.get(key);
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public int getRoleCount() {
        return roles.size();
    }
    
    public int getFieldCount() {
        return getFields().size();
    }
    
    public int getStateCount() {
        return states.size();
    }
    
    /**
     * logic for resolving the next possible transitions for a given role and state     
     * - lookup Role by roleKey
     * - for this Role, lookup state by key (integer)
     * - for the State, iterate over transitions, get the label for each and add to map     
     */        
    public Map<Integer, String> getPermittedTransitions(List<String> roleKeys, int status) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        for(String roleKey : roleKeys) {
            Role role = roles.get(roleKey);
            if (role != null) {
                State state = role.getStates().get(status);
                if (state != null) {
                    for(int transition : state.getTransitions()) {
                        map.put(transition, this.states.get(transition));
                    }
                }
            }
        }
        return map;
    }
    
    private State getRoleState(String roleKey, int stateKey) {
        Role role = roles.get(roleKey);
        return role.getStates().get(stateKey);
    }
    
    public void toggleTransition(int stateKey, String roleKey, int transitionKey) {
        State state = getRoleState(roleKey, stateKey);
        if (state.getTransitions().contains(transitionKey)) {
            state.getTransitions().remove(transitionKey);
        } else {
            state.getTransitions().add(transitionKey);
        }
    }
    
    public void switchMask(int stateKey, String roleKey, String fieldName) {
        State state = getRoleState(roleKey, stateKey);
        Field.Name name = Field.convertToName(fieldName);        
        Integer mask = state.getFields().get(name);
        switch(mask) {
            // case State.MASK_HIDE: state.getFields().put(name, State.MASK_VIEW); return; HIDE SUPPORT IN FUTURE
            case State.MASK_VIEW: state.getFields().put(name, State.MASK_EDIT); return;
            case State.MASK_EDIT: state.getFields().put(name, State.MASK_VIEW); return;
        }
    }
    
    public List<Field> getEditableFields(Collection<String> roleKeys, Collection<Integer> ss) {
        Set<Field> fs = new HashSet<Field>();     
        for(String roleKey : roleKeys) {
            if (roleKey.equals("ROLE_ADMIN") || roleKey.equals("ROLE_GUEST")) {
                continue;
            }
            for(Integer status : ss) {
                if (status == State.NEW) {
                    continue; // we are looking only for editable after the NEW
                }
                State state = getRoleState(roleKey, status);
                for(Map.Entry<Field.Name, Integer> entry : state.getFields().entrySet()) {
                    if (entry.getValue() == State.MASK_EDIT) {
                        fs.add(fields.get(entry.getKey()));
                    }
                }
            }
        }
        List<Field> result = getFieldList();
        result.retainAll(fs); // just to retain the order of the fields
        return result;
    }
    
    public List<Field> getEditableFields() {
        return getEditableFields(roles.keySet(), states.keySet());
    }
    
    //==================================================================
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Metadata getParent() {
        return parent;
    }

    public void setParent(Metadata parent) {
        this.parent = parent;
    }
    
    //=======================================
    // no setters required
    
    public Map<String, Role> getRoles() {
        return roles;
    }  

    public Map<Integer, String> getStates() {
        return states;
    }
    
    public List<Field.Name> getFieldOrder() {
        return fieldOrder;
    }   
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; parent [").append(parent);
        sb.append("]; fields [").append(fields);
        sb.append("]; roles [").append(roles);
        sb.append("]; states [").append(states);
        sb.append("]; fieldOrder [").append(fieldOrder);
        sb.append("]");
        return sb.toString();
    }
    
}
