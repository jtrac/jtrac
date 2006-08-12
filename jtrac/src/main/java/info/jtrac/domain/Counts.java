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
 * Object that holds statistics for items per space, per user
 */
public class Counts implements Serializable {
    
    private Map<Long, Counts> counts = new HashMap<Long, Counts>();
    
    private int loggedBy;
    private int assignedTo;
    private int open;
    private int closed;
    private int total;
    
    private Counts getCounts(long spaceId) {
        Counts c = counts.get(spaceId);
        if (c == null) {
            c = new Counts();
            counts.put(spaceId, c);
        }
        return c;
    }
    
    public void addLoggedBy(long spaceId, int count) {
        Counts c = getCounts(spaceId);
        c.setLoggedBy(count);
        loggedBy += count;
    }
    
    public void addAssignedTo(long spaceId, int count) {
        Counts c = getCounts(spaceId);
        c.setAssignedTo(count);
        assignedTo += count;       
    }
    
    public void addOpen(long spaceId, int count) {
        Counts c = getCounts(spaceId);
        c.setOpen(count);
        c.setTotal(c.getTotal() + count);
        open += count;
        total += count;        
    }
    
    public void addClosed(long spaceId, int count) {
        Counts c = getCounts(spaceId);
        c.setClosed(count);
        c.setTotal(c.getTotal() + count);
        closed += count;
        total += count;        
    }
    
    //==========================================================================
    
    public int getLoggedBy() {
        return loggedBy;
    }
    
    public void setLoggedBy(int loggedBy) {
        this.loggedBy = loggedBy;
    }
    
    public int getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public int getOpen() {
        return open;
    }
    
    public void setOpen(int open) {
        this.open = open;
    }
    
    public int getClosed() {
        return closed;
    }
    
    public void setClosed(int closed) {
        this.closed = closed;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public Map<Long, Counts> getCounts() {
        return counts;
    }
    
    public void setCounts(Map<Long, Counts> counts) {
        this.counts = counts;
    }
    
}
