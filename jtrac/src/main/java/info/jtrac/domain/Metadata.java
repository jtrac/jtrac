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
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import org.dom4j.Document;
import org.dom4j.Element;

/**
 * XML metadata is one of the interesting design decisions of jTrac.
 * Metadata is defined for each space and so Items that belong to a
 * space are customized by the space metadata.
 * 
 * The Metadata defines the following
 * - custom Fields for an Item (within a Space)
 * -- Label
 * -- whether mandatory or not
 * -- the option values (drop down list options)
 *
 * Also the metadata should define the order in which the fields are displayed
 * on the data entry screens and the query result screens etc.
 *
 * Metadata can be inherited.
 */
public class Metadata implements Serializable {    
    
    private int id;
    private Integer type;
    private String name;
    private String description;
    private Metadata parent;

    private Map<Field.Name, Field> fields = new EnumMap<Field.Name, Field>(Field.Name.class);
    private Map<String, Role> roles = new HashMap<String, Role>();
    private Map<Integer, String> states = new TreeMap<Integer, String>();
    private List<Field.Name> fieldOrder = new LinkedList<Field.Name>();
    
    /* accessor, will be used by Hibernate */
    public void setXml(String xmlString) {
        if (xmlString == null) {
            return;
        }
        Document document = XmlUtils.parse(xmlString);
        List<Element> fs = document.selectNodes(FIELD_XPATH);        
        for (Element f : fs) {
            Field field = new Field(f);            
            fields.put(field.getName(), field);
        }
        List<Element> rs = document.selectNodes(ROLE_XPATH);        
        for (Element r : rs) {
            Role role = new Role(r);            
            roles.put(role.getName(), role);
        }
        List<Element> ss = document.selectNodes(STATE_XPATH);
        for (Element s : ss) {
            String key = s.attributeValue(STATUS);
            String value = s.attributeValue(LABEL);
            states.put(Integer.parseInt(key), value);
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
        return fields.get(Field.textToName(name));
    }        
    
    public void add(Field field) {
        fields.put(field.getName(), field);
        for (Role role : roles.values()) {
            for (State state : role.getStates().values()) {
                state.add(field.getName());
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
        for (Map.Entry<Integer, String> entry: states.entrySet()) {
            State state = new State(entry.getKey());
            state.add(fields.keySet());
            role.add(state);
        }
        roles.put(role.getName(), role);
    }
    
    public Set<Field.Name> getUnusedFieldNames() {
        EnumSet<Field.Name> allFieldNames = EnumSet.allOf(Field.Name.class);
        for (Field f : getFieldSet()) {
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
    
    public Set<Field> getFieldSet() {
        Set<Field> fieldSet = new LinkedHashSet<Field>();
        if (parent != null) {
            fieldSet.addAll(parent.getFieldSet());
        }
        // fields will override parent
        fieldSet.addAll(fields.values());
        return fieldSet;
    }
    
    public Collection<Role> getRoleSet() {
        return roles.values();
    }
    
    public String getOptionText(Field.Name fieldName, int key) {
        return getOptionText(fieldName,  key + "");
    }
    
    public String getOptionText(Field.Name fieldName, String key) {
        Field field = fields.get(fieldName);
        if (field != null) {
            return field.getOptionText(key);
        }
        if (parent != null) {
            return parent.getOptionText(fieldName, key);
        }
        return "";        
    }
    
    public String getStatusText(int key) {
        String s = states.get(key);
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public int getRolesCount() {
        return roles.size();
    }
    
    public int getFieldsCount() {
        return fields.size();
    }
    
    public int getStatesCount() {
        return states.size();
    }
    
    //==================================================================
    
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    
    public Map<String, Role> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Role> roles) {
        this.roles = roles;
    }    
    
    public Map<Field.Name, Field> getFields() {
        return fields;
    }

    public void setFields(Map<Field.Name, Field> fields) {
        this.fields = fields;
    }

    public Map<Integer, String> getStates() {
        return states;
    }

    public void setStates(Map<Integer, String> states) {
        this.states = states;
    }    
    
    public List<Field.Name> getFieldOrder() {
        return fieldOrder;
    }

    public void setFieldOrder(List<Field.Name> fieldOrder) {
        this.fieldOrder = fieldOrder;
    }    
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; parent [").append(parent);
        sb.append("]; fields [").append(fields);
        sb.append("]; roles [").append(roles).append("]");
        return sb.toString();
    }
    
}
