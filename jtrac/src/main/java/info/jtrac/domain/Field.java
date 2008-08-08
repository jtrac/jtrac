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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Element;

/**
 * <code>Metadata</code> is composited of Field elements
 * that represent each of the custom fields that may be
 * used within an item
 */
public class Field implements Serializable {   
    
    private Name name;
    private String label;
    private boolean optional;
    private Map<String, String> options;    
    
    private static final Map<String, Name> NAMES_MAP;
    
    // set up a static Map to resolve a String to our Field.Name enum value
    static {
        NAMES_MAP = new HashMap<String, Name>();
        for (Name n : Name.values()) {
            NAMES_MAP.put(n.text, n);
        }
    }
    
    /**
     * Resolve a String to a valid enum value for Field.Name
     */
    public static Name convertToName(String text) {
        Name n = NAMES_MAP.get(text);
        if (n == null) {
            throw new RuntimeException("Bad name " + text);
        }
        return n;
    }
    
    /**
     * test if a given string is a valid field name
     */
    public static boolean isValidName(String text) {
        return NAMES_MAP.containsKey(text);
    }

    /**
     * the names that are used for the custom fields in the outside
     * world - e.g. the XML representation of the metadata that is
     * persisted to the database
     */
    public enum Name {        
        SEVERITY   (1, "severity"), 
        PRIORITY   (2, "priority"),
        CUS_INT_01 (3, "cusInt01"), 
        CUS_INT_02 (3, "cusInt02"), 
        CUS_INT_03 (3, "cusInt03"), 
        CUS_INT_04 (3, "cusInt04"), 
        CUS_INT_05 (3, "cusInt05"),
        CUS_INT_06 (3, "cusInt06"), 
        CUS_INT_07 (3, "cusInt07"), 
        CUS_INT_08 (3, "cusInt08"), 
        CUS_INT_09 (3, "cusInt09"), 
        CUS_INT_10 (3, "cusInt10"),        
        CUS_DBL_01 (4, "cusDbl01"), 
        CUS_DBL_02 (4, "cusDbl02"), 
        CUS_DBL_03 (4, "cusDbl03"),
        CUS_STR_01 (5, "cusStr01"), 
        CUS_STR_02 (5, "cusStr02"), 
        CUS_STR_03 (5, "cusStr03"), 
        CUS_STR_04 (5, "cusStr04"), 
        CUS_STR_05 (5, "cusStr05"),
        CUS_TIM_01 (6, "cusTim01"), 
        CUS_TIM_02 (6, "cusTim02"), 
        CUS_TIM_03 (6, "cusTim03");
        
        private final int type;
        private final String text;
        
        Name(int type, String text) {
            this.type = type;
            this.text = text;
        }        
        
        public int getType() {
            return type;
        }        
        
        public String getText() {
            return text;
        }
        
        public boolean isDropDownType() {
            return type < 4;
        }
        
        public String getDescription() {
            switch (type) {
                case 1: return "Severity (Drop Down)";
                case 2: return "Priority (Drop Down)";             
                case 3: return "Drop Down List";
                case 4: return "Decimal Number";
                case 5: return "Free Text Field";
                case 6: return "Date Field";
                default: throw new RuntimeException("Unknown type " + type);
            }
        }
        
        @Override
        public String toString() {
            return text;
        }
    }    
    
    //===================================================================
    
    public Field() {
        // zero arg constructor
    }
    
    public Field(String fieldName) {
        this.setName(fieldName);
    }
    
    public Field(Name n) {
        this.setName(n);
    }
    
    public Field(Element e) {
        setName(e.attributeValue(NAME));
        label = e.attributeValue(LABEL);
        if (e.attribute(OPTIONAL) != null
                && e.attributeValue(OPTIONAL).equals(TRUE)) {
            optional = true;
        }
        for (Object o : e.elements(OPTION)) {        
            addOption((Element) o);
        }        
    }
    
    /* append this object onto an existing XML document */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(FIELD);
        copyTo(e);
    }
    
    /* marshal this object into a fresh new XML Element */
    public Element getAsElement() {
        Element e = XmlUtils.getNewElement(FIELD);
        copyTo(e);
        return e;
    }
    
    /* copy object values into an existing XML Element */
    private void copyTo(Element e) {
        // appending empty strings to create new objects for "clone" support
        e.addAttribute(NAME, name + "");
        e.addAttribute(LABEL, label + "");
        if (optional) { 
            e.addAttribute(OPTIONAL, TRUE);
        }
        if (options == null) {
            return;
        }
        for (Map.Entry<String, String> entry : options.entrySet()) {                
            Element option = e.addElement(OPTION);
            option.addAttribute(VALUE, entry.getKey() + "");
            option.addText((String) entry.getValue() + "");
        }        
    }
    
    public void addOption(String value) {
        if (options == null) {
            addOption("1", value);
            return;
        }
        Set<Integer> set = new TreeSet<Integer>();
        for (String s : options.keySet()) {
            set.add(new Integer(s));
        }
        int last = set.toArray(new Integer[set.size()])[set.size() -1];
        addOption(last + 1 + "", value);             
    }
    
    public void addOption(String key, String value) {
        if (options == null) {
            options = new LinkedHashMap<String, String>();
        }
        options.put(key, value);
    }   
    
    public void addOption(Element e) {
        String value = e.attributeValue(VALUE);
        if (value == null) {
            return;
        }
        String text = e.getTextTrim();
        if (text == null || text.equals("")) {
            return;
        }
        addOption(value, text);
    } 
    
    public String getCustomValue(String key) {
        if (options == null || key == null) {
            return "";
        }
        String value = options.get(key);
        if (value == null) {
            return "";
        }
        return value;
    }
    
    public boolean hasOption(String value) {
        if (options == null) {
            return false;
        }
        return options.containsValue(value);
    }
    
    public Field getClone() {
        return new Field(getAsElement());
    }
    
    public void initOptions() {
        // TODO i18n
        if (name.type == 1) {
            label = "Severity";
            addOption("1", "Fatal");
            addOption("2", "Major");
            addOption("3", "Minor");
            addOption("4", "Trivial");
            addOption("5", "Suggestion");
        } else if (name.type == 2) {
            label = "Priority";
            addOption("1", "Highest");
            addOption("2", "High");
            addOption("3", "Medium");
            addOption("4", "Low");
            addOption("5", "Lowest");            
        }          
    }    
    
    /* custom accessor */
    public void setName(String nameAsString) {        
        setName(convertToName(nameAsString));
    }    
    
    public boolean isDropDownType() {
        return name.isDropDownType();
    }
    
    public boolean isDatePickerType() {
        return name.type == 6;
    }
    
    public boolean isDecimalNumberType() {
        return name.type == 4;
    }    
    
    public Map<Integer, String> getOptionsWithIntegerKeys() {
        Map<Integer, String> map = new HashMap<Integer, String>(options.size());
        for(Map.Entry<String, String> entry : options.entrySet()) {
            map.put(new Integer(entry.getKey()), entry.getValue());
        }
        return map;
    }
    
    //===================================================================  
    
    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }    
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }        
    
    public Name getName() {
        return name;
    }    
    
    public void setName(Name name) {        
        this.name = name;         
    }    
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("name [").append(name);
        sb.append("]; label [").append(label);
        sb.append("]; optional [").append(optional);
        sb.append("]; options [").append(options);
        sb.append("]");
        return sb.toString();
    }    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Field)) {
            return false;
        }
        final Field f = (Field) o;
        return f.getName().equals(name);
    }
    
    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }
    
}
