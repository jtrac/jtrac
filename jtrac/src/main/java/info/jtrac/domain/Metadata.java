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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
 * space are customized by the space metadata.  This class can marshall
 * and unmarshall itself to XML and this XML is stored in the database
 * in a single column.  Because of this approach, Metadata can be made more
 * and more complicated in the future without impact to the database schema.
 *
 * Things that the Metadata configures for a Space:
 * 
 * 1) custom Fields for an Item (within a Space)
 * - Label
 * - whether mandatory or not [ DEPRECATED ]
 * - the option values (drop down list options)
 * - the option "key" values are stored in the database (WITHOUT any relationships)
 * - the values corresponding to "key"s are resolved in memory from the Metadata
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
 * There is one downside to this approach and that is there is a limit
 * to the nunmbers of custom fields available.  The existing limits are
 * - Drop Down: 10
 * - Free Text: 5
 * - Numeric: 3
 * - Date/Time: 3 
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
    public void setXmlString(String xmlString) {
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
    public String getXmlString() {
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
        return XmlUtils.getAsPrettyXml(getXmlString());
    }
    
    //====================================================================
    
    public void initRoles() {
        // set up default simple workflow
        states.put(State.NEW, "New");
        states.put(State.OPEN, "Open");
        states.put(State.CLOSED, "Closed");
        addRole("DEFAULT");
        toggleTransition("DEFAULT", State.NEW, State.OPEN);
        toggleTransition("DEFAULT", State.OPEN, State.OPEN);
        toggleTransition("DEFAULT", State.OPEN, State.CLOSED);
        toggleTransition("DEFAULT", State.CLOSED, State.OPEN);
    }
    
    public Field getField(String fieldName) {
        return fields.get(Field.convertToName(fieldName));
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
        Field.Name tempName = Field.convertToName(fieldName);
        fields.remove(tempName);
        fieldOrder.remove(tempName);
        for (Role role : roles.values()) {
            for (State state : role.getStates().values()) {
                state.remove(tempName);
            }
        }        
    }
    
    public void addState(String stateName) {
        // first get the max of existing state keys
        int maxStatus = 0;
        for (int status : states.keySet()) {
            if (status > maxStatus && status != State.CLOSED) {
                maxStatus = status;
            }
        }
        int newStatus = maxStatus + 1;
        states.put(newStatus, stateName);
        // by default each role will have permissions for this state, for all fields
        for (Role role : roles.values()) {
            State state = new State(newStatus);
            state.add(fields.keySet());
            role.add(state);
        }
    }        
    
    public void removeState(int stateId) {
        states.remove(stateId);
        for (Role role : roles.values()) {
            role.removeState(stateId);
        }        
        
    }
    
    public void addRole(String roleName) {
        Role role = new Role(roleName);
        for (Map.Entry<Integer, String> entry : states.entrySet()) {
            State state = new State(entry.getKey());
            state.add(fields.keySet());
            role.add(state);
        }
        roles.put(role.getName(), role);
    }
    
    public void renameRole(String oldRole, String newRole) {
        // important! this has to be combined with a database update
        Role role = roles.get(oldRole);
        if (role == null) {
            return; // TODO improve JtracTest and assert not null here
        }
        role.setName(newRole);
        roles.remove(oldRole);
        roles.put(newRole, role);
    }
    
    public void removeRole(String roleName) {
        // important! this has to be combined with a database update
        roles.remove(roleName);
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
            String fieldType = fieldTypes.get(fieldName.getType() + "");
            if (fieldType == null) {
                fieldTypes.put(fieldName.getType() + "", "1");
            } else {
                int count = Integer.parseInt(fieldType);
                count++;
                fieldTypes.put(fieldName.getType() + "", count + "");
            }
        }
        return fieldTypes;        
    }
    
    public Field getNextAvailableField(int fieldType) {
        for (Field.Name fieldName : getUnusedFieldNames()) {
            if (fieldName.getType() == fieldType) {
                return new Field(fieldName + "");
            }
        }
        throw new RuntimeException("No field available of type " + fieldType);
    }
    
    // customized accessor
    public Map<Field.Name, Field> getFields() {
        Map<Field.Name, Field> map = fields;
        if (parent != null) {
            map.putAll(parent.getFields());
        }
        return map;
    }        
    
    public List<Field> getFieldList() {        
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
     * The map returned is used to render the drop down list on screen, [ key = value ]
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
    
    // returning map ideal for JSTL
    public Map<String, Boolean> getRolesAbleToTransition(int fromStatus, int toStatus) {
        Map<String, Boolean> map = new HashMap<String, Boolean>(roles.size());
        for(Role role : roles.values()) {
            State s = role.getStates().get(fromStatus);
            if(s.getTransitions().contains(toStatus)) {
                map.put(role.getName(), true);
            }
        }
        return map;
    }
    
    public Set<String> getRolesAbleToTransitionFrom(int state) {
        Set<String> set = new HashSet<String>(roles.size());
        for(Role role : roles.values()) {
            State s = role.getStates().get(state);
            if(s.getTransitions().size() > 0) {
                set.add(role.getName());
            }
        }
        return set;        
    }
    
    private State getRoleState(String roleKey, int stateKey) {
        Role role = roles.get(roleKey);
        return role.getStates().get(stateKey);
    }
    
    public void toggleTransition(String roleKey, int fromState,  int toState) {
        State state = getRoleState(roleKey, fromState);
        if (state.getTransitions().contains(toState)) {
            state.getTransitions().remove(toState);
        } else {
            state.getTransitions().add(toState);
        }
    }
    
    public void switchMask(int stateKey, String roleKey, String fieldName) {
        State state = getRoleState(roleKey, stateKey);
        Field.Name tempName = Field.convertToName(fieldName);        
        Integer mask = state.getFields().get(tempName);
        switch(mask) {
            // case State.MASK_HIDDEN: state.getFields().put(name, State.MASK_READONLY); return; HIDDEN support in future
            case State.MASK_READONLY: state.getFields().put(tempName, State.MASK_OPTIONAL); return;
            case State.MASK_OPTIONAL: state.getFields().put(tempName, State.MASK_MANDATORY); return;            
            case State.MASK_MANDATORY: state.getFields().put(tempName, State.MASK_READONLY); return;
            default: // should never happen
        }
    }
    
    public List<Field> getEditableFields(String roleKey, int status) {
        return getEditableFields(Collections.singletonList(roleKey), status);
    }
    
    public List<Field> getEditableFields(Collection<String> roleKeys, int status) {
        Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(getFieldCount());     
        for(String roleKey : roleKeys) {
            if (roleKey.startsWith("ROLE_")) {
                continue;
            }
            if(status > -1) {
                State state = getRoleState(roleKey, status);
                fs.putAll(getEditableFields(state));  
            } else { // we are trying to find all editable fields
                Role role = roles.get(roleKey);                
                for(State state : role.getStates().values()) {
                    if(state.getStatus() == State.NEW) {
                        continue;
                    }
                    fs.putAll(getEditableFields(state));
                }                
            }
        }
        // just to fix the order of the fields
        List<Field> result = new ArrayList<Field>(getFieldCount());
        for(Field.Name fieldName : fieldOrder) {
            Field f = fs.get(fieldName);
            // and not all fields may be editable
            if(f != null) {
                result.add(f);
            }
        }
        return result;
    }
    
    public List<Field> getEditableFields() {
       return getEditableFields(roles.keySet(), -1);        
    }        
    
    private Map<Field.Name, Field> getEditableFields(State state) {
        Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(getFieldCount());
        for(Map.Entry<Field.Name, Integer> entry : state.getFields().entrySet()) {
            if (entry.getValue() == State.MASK_OPTIONAL || entry.getValue() == State.MASK_MANDATORY) {
                Field f = fields.get(entry.getKey());
                // set if optional or not, this changes depending on the user / role and status
                f.setOptional(entry.getValue() == State.MASK_OPTIONAL);
                fs.put(f.getName(), f);
            }
        }
        return fs;
    }
    
    public Collection<Role> getRoleList() {
        return roles.values();
    }
    
    public Collection<String> getRoleKeys() {
        return roles.keySet();
    }
    
    // introducing Admin permissions per space, slight hack
    // so Role stands for "workflow" role from now on
    public List<String> getAdminRoleKeys() {
        return Arrays.asList(new String[] { Role.ROLE_ADMIN });
    }
    
    public List<String> getAllRoleKeys() {
        List<String> list = new ArrayList<String>(getRoleKeys());
        list.addAll(getAdminRoleKeys());
        return list;
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
    
    public Map<String, Role> getRolesMap() {
        return roles;
    }  

    public Map<Integer, String> getStatesMap() {
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
