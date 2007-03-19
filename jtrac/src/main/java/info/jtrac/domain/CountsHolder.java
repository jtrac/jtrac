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
 * Just wraps a Map of Counts keyed to Space ids
 * but adds logic for adding and iterative totalling
 */
public class CountsHolder implements Serializable {

    Map<Long, Counts> counts = new HashMap<Long, Counts>();
    
    public void add(long spaceId, int type, long count) {
        Counts c = counts.get(spaceId);
        if (c == null) {
            c = new Counts(false);
            counts.put(spaceId, c);
        }
        c.add(type, -1, count);
    }
    
    private int getTotalForType(int type) {
        int total = 0;
        for(Counts c : counts.values()) {
            total += c.getTotalForType(type);
        }
        return total;
    }
    
    public int getTotalLoggedByMe() {
        return getTotalForType(Counts.LOGGED_BY_ME);
    }
        
    public int getTotalAssignedToMe() {
        return getTotalForType(Counts.ASSIGNED_TO_ME);
    }    
    
    public int getTotalTotal() {
        return getTotalForType(Counts.TOTAL);
    }    

    public Map<Long, Counts> getCounts() {
        return counts;
    }
    
}
