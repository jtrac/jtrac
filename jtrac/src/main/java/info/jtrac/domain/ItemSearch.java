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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Object that holds filter criteria when searching for Items
 * and also creates a Hibernate Criteria query to pass to the DAO
 * Spring MVC automagically binds most of the screen selections
 * on to the large bunch of instance variables of this class
 */
public class ItemSearch implements Serializable {
    
    private List<Field> fields; // columns in order
    private Space space; // if null, means aggregate across all spaces
    private User user; // this will be set in the case space is null
    
    private int pageSize = 25;
    private int currentPage;
    private long resultCount;
    private String sortFieldName = "id";
    private boolean sortDescending;
    private boolean showHistory;
    private boolean showDetail;
    
    private String summary;
    
    private Date createdDateStart;
    private Date createdDateEnd;
    private Date modifiedDateStart;
    private Date modifiedDateEnd;
    
    private Set<Integer> spaceSet;
    private Set<Integer> statusSet;
    private Set<Integer> severitySet;
    private Set<Integer> prioritySet;
    private Set<Integer> loggedBySet;
    private Set<Integer> assignedToSet;
    
    private Set<Integer> cusInt01Set;
    private Set<Integer> cusInt02Set;
    private Set<Integer> cusInt03Set;
    private Set<Integer> cusInt04Set;
    private Set<Integer> cusInt05Set;
    private Set<Integer> cusInt06Set;
    private Set<Integer> cusInt07Set;
    private Set<Integer> cusInt08Set;
    private Set<Integer> cusInt09Set;
    private Set<Integer> cusInt10Set;
    
    private String cusStr01;
    private String cusStr02;
    private String cusStr03;
    private String cusStr04;
    private String cusStr05;
    
    private Date cusTim01Start;
    private Date cusTim01End;
    private Date cusTim02Start;
    private Date cusTim02End;
    private Date cusTim03Start;
    private Date cusTim03End;
    
    // have to do this as "order by" clause conflicts with "count (*)" clause
    // DAO has to use getCriteriaForCount() separately
    public DetachedCriteria getCriteria() {
        DetachedCriteria criteria = getCriteriaForCount();
        if (sortDescending) {
            criteria.addOrder(Order.desc(sortFieldName));
        } else {
            criteria.addOrder(Order.asc(sortFieldName));
        }
        return criteria;
    }
    
    public DetachedCriteria getCriteriaForCount() {
        
        if (modifiedDateStart != null || modifiedDateEnd != null) {
            showHistory = true;
        }
        
        DetachedCriteria criteria = null;
        
        if (showHistory == true) {
            criteria = DetachedCriteria.forClass(History.class);
            criteria.createCriteria("parent").add(Restrictions.in("space.id", getSpaceIdSet()));
        } else {
            criteria = DetachedCriteria.forClass(Item.class);
            criteria.add(Restrictions.in("space.id", getSpaceIdSet()));
            // Item created date filter takes effect only here if set i.e. when showHistory == false
            if (createdDateStart != null) {
                criteria.add(Restrictions.ge("timeStamp", createdDateStart));
            }
            if (createdDateEnd != null) {
                criteria.add(Restrictions.le("timeStamp", createdDateEnd));
            }
        }
        
        if (statusSet != null) {
            criteria.add(Restrictions.in("status", statusSet));
        }
        if (severitySet != null) {
            criteria.add(Restrictions.in("severity", severitySet));
        }
        if (prioritySet != null) {
            criteria.add(Restrictions.in("priority", prioritySet));
        }
        if (loggedBySet != null) {
            criteria.add(Restrictions.in("loggedBy.id", loggedBySet));
        }
        if (assignedToSet != null) {
            criteria.add(Restrictions.in("assignedTo.id", assignedToSet));
        }
        if (cusInt01Set != null) {
            criteria.add(Restrictions.in("cusInt01", cusInt01Set));
        }
        if (cusInt02Set != null) {
            criteria.add(Restrictions.in("cusInt02", cusInt02Set));
        }
        if (cusInt03Set != null) {
            criteria.add(Restrictions.in("cusInt03", cusInt03Set));
        }
        if (cusInt04Set != null) {
            criteria.add(Restrictions.in("cusInt04", cusInt04Set));
        }
        if (cusInt05Set != null) {
            criteria.add(Restrictions.in("cusInt05", cusInt05Set));
        }
        if (cusInt06Set != null) {
            criteria.add(Restrictions.in("cusInt06", cusInt06Set));
        }
        if (cusInt07Set != null) {
            criteria.add(Restrictions.in("cusInt07", cusInt07Set));
        }
        if (cusInt08Set != null) {
            criteria.add(Restrictions.in("cusInt08", cusInt08Set));
        }
        if (cusInt09Set != null) {
            criteria.add(Restrictions.in("cusInt09", cusInt09Set));
        }
        if (cusInt10Set != null) {
            criteria.add(Restrictions.in("cusInt10", cusInt10Set));
        }
        if (modifiedDateStart != null) {
            criteria.add(Restrictions.ge("timeStamp", modifiedDateStart));
        }
        if (modifiedDateEnd != null) {
            criteria.add(Restrictions.le("timeStamp", modifiedDateEnd));
        }
        return criteria;
    }
    
    // private routine to help with the space "in" clause
    private Collection<Integer> getSpaceIdSet() {
        if (space == null) {
            if (spaceSet != null) {
                return spaceSet;
            }
            Set<Integer> spaceIdSet = new HashSet<Integer>(user.getSpaceRoles().size());
            for (SpaceRole sr : user.getSpaceRoles()) {
                if (sr.getSpace() != null) {
                    spaceIdSet.add(sr.getSpace().getId());
                }
            }
            return spaceIdSet;
        } else {
            return Collections.singleton(space.getId());
        }
    }
    
    //=========================================================================
    
    public ItemSearch() {
        // zero arg constructor
    }
    
    public ItemSearch(User user) {
        this.user = user;
        fields = new ArrayList<Field>();
        fields.add(new Field(Field.Name.SEVERITY));
        fields.add(new Field(Field.Name.PRIORITY));
        this.sortDescending = true;
    }
    
    public ItemSearch(Space space) {
        this.fields = space.getMetadata().getFieldList();
        this.space = space;
        this.sortDescending = true;
    }
    
    private Map setToMap(Set s) {
        if (s == null) {
            return null;
        }
        Map map = new HashMap(s.size());
        for (Object o : s) {
            // ugly toString hack to make JSTL / EL get map value by key easier
            map.put(o.toString(), new Boolean(true));
        }
        return map;
    }
    
    public Map getSearchMap() {
        Map map = new HashMap();
        map.put("spaceSet", setToMap(spaceSet));
        map.put("statusSet", setToMap(statusSet));
        map.put("severitySet", setToMap(severitySet));
        map.put("prioritySet", setToMap(prioritySet));
        map.put("loggedBySet", setToMap(loggedBySet));
        map.put("assignedToSet", setToMap(assignedToSet));
        map.put("cusInt01Set", setToMap(cusInt01Set));
        map.put("cusInt02Set", setToMap(cusInt02Set));
        map.put("cusInt03Set", setToMap(cusInt03Set));
        map.put("cusInt04Set", setToMap(cusInt04Set));
        map.put("cusInt05Set", setToMap(cusInt05Set));
        map.put("cusInt06Set", setToMap(cusInt06Set));
        map.put("cusInt07Set", setToMap(cusInt07Set));
        map.put("cusInt08Set", setToMap(cusInt08Set));
        map.put("cusInt09Set", setToMap(cusInt09Set));
        map.put("cusInt10Set", setToMap(cusInt10Set));
        return map;
    }
    
    public Map<String, String> getSeverityOptions() {
        Field f = new Field(Field.Name.SEVERITY);
        return f.getOptions();
    }
    
    public Map<String, String> getPriorityOptions() {
        Field f = new Field(Field.Name.PRIORITY);
        return f.getOptions();
    }
    
    public Map<Integer, String> getStatusOptions() {
        if (space == null) {
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(State.OPEN, "Open");
            map.put(State.CLOSED, "Closed");
            return map;
        }
        // is mutable so caution
        Map<Integer, String> temp = new HashMap(space.getMetadata().getStates());
        temp.remove(State.NEW);
        return temp;
    }
    
    public Map<Integer, String> getSpaceOptions() {
        if (user == null) {
            return null;
        }
        Map<Integer, String> map = new HashMap<Integer, String>(user.getSpaceRoles().size());
        for(SpaceRole sr : user.getSpaceRoles()) {
            if (sr.getSpace() != null) {
                map.put(sr.getSpace().getId(), sr.getSpace().getPrefixCode());
            }
        }
        return map;
    }
    
    public Map<String, Field> getFieldMap() {
        if (space == null) {
            return null;
        }
        Map<String, Field> fieldMap = new HashMap<String, Field>(space.getMetadata().getFieldCount());
        for (Field f : space.getMetadata().getFields().values()) {
            fieldMap.put(f.getName().toString(), f);
        }
        return fieldMap;
    }
    
    //=====================================================================
    
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    
    public List<Field> getFields() {
        return fields;
    }
    
    public Space getSpace() {
        return space;
    }
    
    public void setSpace(Space space) {
        this.space = space;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public String getSortFieldName() {
        return sortFieldName;
    }
    
    public void setSortFieldName(String sortFieldName) {
        this.sortFieldName = sortFieldName;
    }
    
    public boolean isSortDescending() {
        return sortDescending;
    }
    
    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }
    
    public boolean isShowHistory() {
        return showHistory;
    }
    
    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }
    
    public boolean isShowDetail() {
        return showDetail;
    }
    
    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public Date getCreatedDateStart() {
        return createdDateStart;
    }
    
    public void setCreatedDateStart(Date createdDateStart) {
        this.createdDateStart = createdDateStart;
    }
    
    public Date getCreatedDateEnd() {
        return createdDateEnd;
    }
    
    public void setCreatedDateEnd(Date createdDateEnd) {
        this.createdDateEnd = createdDateEnd;
    }
    
    public Date getModifiedDateStart() {
        return modifiedDateStart;
    }
    
    public void setModifiedDateStart(Date modifiedDateStart) {
        this.modifiedDateStart = modifiedDateStart;
    }
    
    public Date getModifiedDateEnd() {
        return modifiedDateEnd;
    }
    
    public void setModifiedDateEnd(Date modifiedDateEnd) {
        this.modifiedDateEnd = modifiedDateEnd;
    }
    
    public Set<Integer> getSpaceSet() {
        return spaceSet;
    }
    
    public void setSpaceSet(Set<Integer> spaceSet) {
        this.spaceSet = spaceSet;
    }
    
    public Set<Integer> getStatusSet() {
        return statusSet;
    }
    
    public void setStatusSet(Set<Integer> statusSet) {
        this.statusSet = statusSet;
    }
    
    public Set<Integer> getSeveritySet() {
        return severitySet;
    }
    
    public void setSeveritySet(Set<Integer> severitySet) {
        this.severitySet = severitySet;
    }
    
    public Set<Integer> getPrioritySet() {
        return prioritySet;
    }
    
    public void setPrioritySet(Set<Integer> prioritySet) {
        this.prioritySet = prioritySet;
    }
    
    public Set<Integer> getLoggedBySet() {
        return loggedBySet;
    }
    
    public void setLoggedBySet(Set<Integer> loggedBySet) {
        this.loggedBySet = loggedBySet;
    }
    
    public Set<Integer> getAssignedToSet() {
        return assignedToSet;
    }
    
    public void setAssignedToSet(Set<Integer> assignedToSet) {
        this.assignedToSet = assignedToSet;
    }
    
    public Set<Integer> getCusInt01Set() {
        return cusInt01Set;
    }
    
    public void setCusInt01Set(Set<Integer> cusInt01Set) {
        this.cusInt01Set = cusInt01Set;
    }
    
    public Set<Integer> getCusInt02Set() {
        return cusInt02Set;
    }
    
    public void setCusInt02Set(Set<Integer> cusInt02Set) {
        this.cusInt02Set = cusInt02Set;
    }
    
    public Set<Integer> getCusInt03Set() {
        return cusInt03Set;
    }
    
    public void setCusInt03Set(Set<Integer> cusInt03Set) {
        this.cusInt03Set = cusInt03Set;
    }
    
    public Set<Integer> getCusInt04Set() {
        return cusInt04Set;
    }
    
    public void setCusInt04Set(Set<Integer> cusInt04Set) {
        this.cusInt04Set = cusInt04Set;
    }
    
    public Set<Integer> getCusInt05Set() {
        return cusInt05Set;
    }
    
    public void setCusInt05Set(Set<Integer> cusInt05Set) {
        this.cusInt05Set = cusInt05Set;
    }
    
    public Set<Integer> getCusInt06Set() {
        return cusInt06Set;
    }
    
    public void setCusInt06Set(Set<Integer> cusInt06Set) {
        this.cusInt06Set = cusInt06Set;
    }
    
    public Set<Integer> getCusInt07Set() {
        return cusInt07Set;
    }
    
    public void setCusInt07Set(Set<Integer> cusInt07Set) {
        this.cusInt07Set = cusInt07Set;
    }
    
    public Set<Integer> getCusInt08Set() {
        return cusInt08Set;
    }
    
    public void setCusInt08Set(Set<Integer> cusInt08Set) {
        this.cusInt08Set = cusInt08Set;
    }
    
    public Set<Integer> getCusInt09Set() {
        return cusInt09Set;
    }
    
    public void setCusInt09Set(Set<Integer> cusInt09Set) {
        this.cusInt09Set = cusInt09Set;
    }
    
    public Set<Integer> getCusInt10Set() {
        return cusInt10Set;
    }
    
    public void setCusInt10Set(Set<Integer> cusInt10Set) {
        this.cusInt10Set = cusInt10Set;
    }
    
    public String getCusStr01() {
        return cusStr01;
    }
    
    public void setCusStr01(String cusStr01) {
        this.cusStr01 = cusStr01;
    }
    
    public String getCusStr02() {
        return cusStr02;
    }
    
    public void setCusStr02(String cusStr02) {
        this.cusStr02 = cusStr02;
    }
    
    public String getCusStr03() {
        return cusStr03;
    }
    
    public void setCusStr03(String cusStr03) {
        this.cusStr03 = cusStr03;
    }
    
    public String getCusStr04() {
        return cusStr04;
    }
    
    public void setCusStr04(String cusStr04) {
        this.cusStr04 = cusStr04;
    }
    
    public String getCusStr05() {
        return cusStr05;
    }
    
    public void setCusStr05(String cusStr05) {
        this.cusStr05 = cusStr05;
    }
    
    public Date getCusTim01Start() {
        return cusTim01Start;
    }
    
    public void setCusTim01Start(Date cusTim01Start) {
        this.cusTim01Start = cusTim01Start;
    }
    
    public Date getCusTim01End() {
        return cusTim01End;
    }
    
    public void setCusTim01End(Date cusTim01End) {
        this.cusTim01End = cusTim01End;
    }
    
    public Date getCusTim02Start() {
        return cusTim02Start;
    }
    
    public void setCusTim02Start(Date cusTim02Start) {
        this.cusTim02Start = cusTim02Start;
    }
    
    public Date getCusTim02End() {
        return cusTim02End;
    }
    
    public void setCusTim02End(Date cusTim02End) {
        this.cusTim02End = cusTim02End;
    }
    
    public Date getCusTim03Start() {
        return cusTim03Start;
    }
    
    public void setCusTim03Start(Date cusTim03Start) {
        this.cusTim03Start = cusTim03Start;
    }
    
    public Date getCusTim03End() {
        return cusTim03End;
    }
    
    public void setCusTim03End(Date cusTim03End) {
        this.cusTim03End = cusTim03End;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public long getResultCount() {
        return resultCount;
    }
    
    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
    }
            
}
