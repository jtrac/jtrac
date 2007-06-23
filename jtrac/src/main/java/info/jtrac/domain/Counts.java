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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object that holds statistics for items within a single space
 * a map of these would serve as the model for the dashboard view
 * contains logic for totalling etc.
 */
public class Counts implements Serializable {

    public static final int ASSIGNED_TO_ME = 1;
    public static final int LOGGED_BY_ME = 2;
    public static final int TOTAL = 3;
    
    private Map<Integer, Map<Integer, Long>> typeCounts = new HashMap<Integer, Map<Integer, Long>>();     
    
    private boolean detailed;  
    
    public boolean isDetailed() {
        return detailed;
    }
    
    public Counts(boolean detailed) {        
        this.detailed = detailed;
        for(int i = 1; i < 4; i++) {
            typeCounts.put(i, new HashMap<Integer, Long>());
        }
    }
    
    public void addLoggedByMe(int state, long count) {
        add(LOGGED_BY_ME, state, count);
    }
    
    public void addAssignedToMe(int state, long count) {
        add(ASSIGNED_TO_ME, state, count);
    }    
    
    public void addTotal(int state, long count) {
        add(TOTAL, state, count);
    }    
    
    protected void add(int type, int state, long count) {
        Map<Integer, Long> stateCounts = typeCounts.get(type);
        Long i = stateCounts.get(state);
        if (i == null) {            
            stateCounts.put(state, count);
        } else {
            stateCounts.put(state, i + count);
        }
    }  
    
    protected int getTotalForType(int type) {
        Map<Integer, Long> stateCounts = typeCounts.get(type);
        if (stateCounts == null) {
            return 0;
        }
        int total = 0;
        for(Map.Entry<Integer, Long> entry : stateCounts.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }
    
    public int getLoggedByMe() {
        return getTotalForType(LOGGED_BY_ME);
    }     
    
    public int getAssignedToMe() {
        return getTotalForType(ASSIGNED_TO_ME);
    }    
    
    public int getTotal() {
        return getTotalForType(TOTAL);
    }
 
    public Map<Integer, Long> getLoggedByMeMap() {
        return typeCounts.get(LOGGED_BY_ME);
    }
    
    public Map<Integer, Long> getAssignedToMeMap() {
        return typeCounts.get(ASSIGNED_TO_ME);
    } 
    
    public Map<Integer, Long> getTotalMap() {
        return typeCounts.get(TOTAL);
    }
    
    // return string for easier rendering on dashboard screen    
    public String getLoggedByMeForState(int stateKey) {
        Long i = typeCounts.get(LOGGED_BY_ME).get(stateKey);
        return i == null ? "" : i.toString();
    }
    
    public String getAssignedToMeForState(int stateKey) {
        Long i = typeCounts.get(ASSIGNED_TO_ME).get(stateKey);
        return i == null ? "" : i.toString();
    } 
    
    public String getTotalForState(int stateKey) {
        Long i = typeCounts.get(TOTAL).get(stateKey);
        return i == null ? "" : i.toString();
    }    
    
}
