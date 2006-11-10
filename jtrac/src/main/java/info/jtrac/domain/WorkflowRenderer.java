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

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Class that is used to render a workflow to the GUI
 * currently designed to work with an HTML table in a JSP
 */
public class WorkflowRenderer implements Serializable {           
        
    public class StateElement {
        
        private Element e;
        
        public StateElement(Element e) {
            this.e = e;
        }
        
        public Collection<StateElement> getChildElements() {
            List<StateElement> childElements = new LinkedList<StateElement>();
            for(Element child : (List<Element>) e.elements()) {
                childElements.add(new StateElement(e));
            }
            return childElements;
        }
        
        public int getChildElementCount() {
            return e.elements().size();
        }
        
        public String getStateName() {
            return e.attributeValue("name");
        }
        
    }
    
    private Role role;
    private Map<Integer, String> stateNames;
    Document document;
    
    public WorkflowRenderer(Role role, Map<Integer, String> stateNames) {
        this.role = role;
        this.stateNames = stateNames;
        init();
    }
    
    private void init() {
        State s = role.getStates().get(State.NEW);
        document = XmlUtils.getNewDocument("state");
        Element e = document.getRootElement();
        e.addAttribute("name", stateNames.get(State.NEW));
        e.addAttribute("key", State.NEW + "");
        addTransitions(e, s);
    }
    
    /* has the state already been added to the tree? */
    private boolean stateExists(int key) {
        return document.selectNodes("//state[@key='" + key + "']").size() > 0;
    }
   
    private void addTransitions(Element parent, State s) {
        for(int i : s.getTransitions()) {
            boolean exists = stateExists(i);
            Element child = parent.addElement("state");
            child.addAttribute("name", stateNames.get(i));
            child.addAttribute("key", i + "");            
            if(!exists) { // check to avoid infinite loop
                addTransitions(child, role.getStates().get(i));
            }            
        }        
    }

    public Document getDocument() {
        return document;
    }
    
    public String getAsString() {
        return XmlUtils.getAsPrettyXml(document);
    }
    
    public StateElement getRootStateElement() {
        return new StateElement(document.getRootElement());
    }
    
    public String getAsHtml(Element e) {
        StringBuffer sb = new StringBuffer();
        sb.append("<table border='1'><tr><td colspan='" + e.elements().size() + "'>");
        sb.append(e.attributeValue("name"));
        sb.append("</td></tr><tr valign='top'>");
        for(Element child : (List<Element>) e.elements()) {
            sb.append("<td>");
            sb.append(getAsHtml(child));
            sb.append("</td>");
        }
        sb.append("</tr></table>");
        return sb.toString();
    }
    
    public String getAsHtml() {
        return getAsHtml(document.getRootElement());
    }
}
