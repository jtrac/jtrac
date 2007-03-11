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
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Object that holds filter criteria when searching for Items
 * and also creates a Hibernate Criteria query to pass to the DAO
 * Wicket automagically binds most of the screen selections
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
        
    private long selectedItemId;
    private String summary;    
    private Collection<Long> itemIds;
    
    private Date createdDateStart;
    private Date createdDateEnd;
    private Date modifiedDateStart;
    private Date modifiedDateEnd;
    
    private List<Long> spaceList;
    private List<Integer> statusList;
    private List<Integer> severityList;
    private List<Integer> priorityList;
    private List<User> loggedByList;
    private List<User> assignedToList;    
    
    private List<Integer> cusInt01List;
    private List<Integer> cusInt02List;
    private List<Integer> cusInt03List;
    private List<Integer> cusInt04List;
    private List<Integer> cusInt05List;
    private List<Integer> cusInt06List;
    private List<Integer> cusInt07List;
    private List<Integer> cusInt08List;
    private List<Integer> cusInt09List;
    private List<Integer> cusInt10List;
    
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
        if (sortFieldName == null) { // can happen only for multi-space search
            sortFieldName = "id"; // effectively is a sort on created date
        }
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
            // apply restrictions to parent, this is an inner join =============
            DetachedCriteria parent = criteria.createCriteria("parent");
            parent.add(Restrictions.in("space.id", getSpaceIdList()));            
            if (createdDateStart != null) {
                parent.add(Restrictions.ge("timeStamp", createdDateStart));
            }
            if (createdDateEnd != null) {
                parent.add(Restrictions.le("timeStamp", createdDateEnd));
            }              
            //==================================================================            
            if (modifiedDateStart != null) {
                criteria.add(Restrictions.ge("timeStamp", modifiedDateStart));
            }
            if (modifiedDateEnd != null) {
                criteria.add(Restrictions.le("timeStamp", modifiedDateEnd));
            }            
        } else {
            criteria = DetachedCriteria.forClass(Item.class);
            criteria.add(Restrictions.in("space.id", getSpaceIdList()));              
            if (itemIds != null) {
                criteria.add(Restrictions.in("id", itemIds));
            }            
            // see the difference above in the if clause
            if (createdDateStart != null) {
                criteria.add(Restrictions.ge("timeStamp", createdDateStart));
            }

            if (createdDateEnd != null) {
                criteria.add(Restrictions.le("timeStamp", createdDateEnd));
            }               
            
        }         
        //======================================================================
        if (statusList != null && statusList.size() > 0) {
            criteria.add(Restrictions.in("status", statusList));
        }
        if (severityList != null && severityList.size() > 0) {
            criteria.add(Restrictions.in("severity", severityList));
        }
        if (priorityList != null && priorityList.size() > 0) {
            criteria.add(Restrictions.in("priority", priorityList));
        }
        if (loggedByList != null && loggedByList.size() > 0) {
            criteria.add(Restrictions.in("loggedBy", loggedByList));
        }
        if (assignedToList != null && assignedToList.size() > 0) {
            criteria.add(Restrictions.in("assignedTo", assignedToList));
        }        
        //======================================================================
        if (cusInt01List != null && cusInt01List.size() > 0) {
            criteria.add(Restrictions.in("cusInt01", cusInt01List));
        }
        if (cusInt02List != null && cusInt02List.size() > 0) {
            criteria.add(Restrictions.in("cusInt02", cusInt02List));
        }
        if (cusInt03List != null && cusInt03List.size() > 0) {
            criteria.add(Restrictions.in("cusInt03", cusInt03List));
        }
        if (cusInt04List != null && cusInt04List.size() > 0) {
            criteria.add(Restrictions.in("cusInt04", cusInt04List));
        }
        if (cusInt05List != null && cusInt05List.size() > 0) {
            criteria.add(Restrictions.in("cusInt05", cusInt05List));
        }
        if (cusInt06List != null && cusInt06List.size() > 0) {
            criteria.add(Restrictions.in("cusInt06", cusInt06List));
        }
        if (cusInt07List != null && cusInt07List.size() > 0) {
            criteria.add(Restrictions.in("cusInt07", cusInt07List));
        }
        if (cusInt08List != null && cusInt08List.size() > 0) {
            criteria.add(Restrictions.in("cusInt08", cusInt08List));
        }
        if (cusInt09List != null && cusInt09List.size() > 0) {
            criteria.add(Restrictions.in("cusInt09", cusInt09List));
        }
        if (cusInt10List != null && cusInt10List.size() > 0) {
            criteria.add(Restrictions.in("cusInt10", cusInt10List));
        }
        //======================================================================
        if (cusStr01 != null) {
            criteria.add(Restrictions.like("cusStr01", "%" + cusStr01 + "%"));
        }                 
        if (cusStr02 != null) {
            criteria.add(Restrictions.like("cusStr02", "%" + cusStr02 + "%"));
        }          
        if (cusStr03 != null) {
            criteria.add(Restrictions.like("cusStr03", "%" + cusStr03 + "%"));
        }
        if (cusStr04 != null) {
            criteria.add(Restrictions.like("cusStr04", "%" + cusStr04 + "%"));
        }
        if (cusStr05 != null) {
            criteria.add(Restrictions.like("cusStr05", "%" + cusStr05 + "%"));
        }
        //======================================================================
        if (cusTim01Start != null) {
            criteria.add(Restrictions.ge("cusTim01", cusTim01Start));
        }
        if (cusTim01End != null) {
            criteria.add(Restrictions.le("cusTim01", cusTim01End));
        }
        if (cusTim02Start != null) {
            criteria.add(Restrictions.ge("cusTim02", cusTim02Start));
        }
        if (cusTim02End != null) {
            criteria.add(Restrictions.le("cusTim02", cusTim02End));
        }
        if (cusTim03Start != null) {
            criteria.add(Restrictions.ge("cusTim03", cusTim03Start));
        }
        if (cusTim03End != null) {
            criteria.add(Restrictions.le("cusTim03", cusTim03End));
        }         
        //======================================================================
        return criteria;
    }
    
    // private routine to help with the space "in" clause
    private Collection<Long> getSpaceIdList() {
        if (space == null) {
            if (spaceList != null && spaceList.size() > 0) {
                return spaceList;
            }
            List<Long> spaceIdList = new ArrayList<Long>(user.getUserSpaceRoles().size());
            for (UserSpaceRole usr : user.getUserSpaceRoles()) {
                if (usr.getSpace() != null) {
                    spaceIdList.add(usr.getSpace().getId());
                }
            }
            if (spaceIdList.size() == 0) {
                // no spaces allocated
                // hack so that search screen does not bomb
                return Collections.singleton(new Long(0)); 
            }            
            return spaceIdList;
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
        Field severity = new Field(Field.Name.SEVERITY);
        severity.initOptions();
        fields.add(severity);
        Field priority = new Field(Field.Name.PRIORITY);
        priority.initOptions();
        fields.add(priority);
        this.sortDescending = true;
    }
    
    public ItemSearch(Space space) {
        this.fields = space.getMetadata().getFieldList();
        this.space = space;
        this.sortDescending = true;
    }     
    
    public Map<String, String> getSeverityOptions() {
        Field f = new Field(Field.Name.SEVERITY);
        f.initOptions();
        return f.getOptions();
    }
    
    public Map<String, String> getPriorityOptions() {
        Field f = new Field(Field.Name.PRIORITY);
        f.initOptions();
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
        Map<Integer, String> temp = new HashMap<Integer, String>(space.getMetadata().getStates());
        temp.remove(State.NEW);
        return temp;
    }
    
    public Map<Integer, String> getSpaceOptions() {
        if (user == null) {
            return null;
        }
        Map<Integer, String> map = new HashMap<Integer, String>(user.getUserSpaceRoles().size());
        for(UserSpaceRole usr : user.getUserSpaceRoles()) {
            if (usr.getSpace() != null) {
                map.put((int) usr.getSpace().getId(), usr.getSpace().getName());
            }
        }
        return map;
    }    
    
    public List<Field> getDropDownFields() {
        List<Field> temp = new ArrayList<Field>();
        for(Field f : fields) {
            if (f.getName().getType() < 4) {
                temp.add(f);
            }
        }
        return temp;
    }
    
    public List<Field> getDateFields() {
        List<Field> temp = new ArrayList<Field>();
        for(Field f : fields) {
            if (f.getName().getType() == 6) {
                temp.add(f);
            }
        }
        return temp;
    }    
    
    public List<Field> getTextFields() {
        List<Field> temp = new ArrayList<Field>();
        for(Field f : fields) {
            if (f.getName().getType() == 5) {
                temp.add(f);
            }
        }
        return temp;
    }    
    
    public Map<String, Field> getFieldMap() {
        Map<String, Field> fieldMap = new HashMap<String, Field>(fields.size());
        for (Field f : fields) {
            fieldMap.put(f.getName().toString(), f);
        }
        return fieldMap;
    }
    
    public void toggleSortDirection() {
        sortDescending = !sortDescending;
    }      
    
    public void setLoggedBy(User user) {
        loggedByList = new ArrayList<User>();
        loggedByList.add(user);
    }
    
    public void setAssignedTo(User user) {
        assignedToList = new ArrayList<User>();
        assignedToList.add(user);        
    }
    
    public void setStatus(int i) {
        statusList = new ArrayList<Integer>();
        statusList.add(i);
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
    
    public Collection<Long> getItemIds() {
        return itemIds;
    }
    
    public void setItemIds(Collection<Long> itemIds) {
        this.itemIds = itemIds;
    }    
    
    public List<Long> getSpaceList() {
        return spaceList;
    }
    
    public void setSpaceList(List<Long> spaceList) {
        this.spaceList = spaceList;
    }
    
    public List<Integer> getStatusList() {
        return statusList;
    }
    
    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }
    
    public List<Integer> getSeverityList() {
        return severityList;
    }
    
    public void setSeverityList(List<Integer> severityList) {
        this.severityList = severityList;
    }
    
    public List<Integer> getPriorityList() {
        return priorityList;
    }
    
    public void setPriorityList(List<Integer> priorityList) {
        this.priorityList = priorityList;
    }
    
    public List<User> getLoggedByList() {
        return loggedByList;
    }
    
    public void setLoggedByList(List<User> loggedByList) {
        this.loggedByList = loggedByList;
    }
    
    public List<User> getAssignedToList() {
        return assignedToList;
    }
    
    public void setAssignedToList(List<User> assignedToList) {
        this.assignedToList = assignedToList;
    }
    
    public List<Integer> getCusInt01List() {
        return cusInt01List;
    }
    
    public void setCusInt01List(List<Integer> cusInt01List) {
        this.cusInt01List = cusInt01List;
    }
    
    public List<Integer> getCusInt02List() {
        return cusInt02List;
    }
    
    public void setCusInt02List(List<Integer> cusInt02List) {
        this.cusInt02List = cusInt02List;
    }
    
    public List<Integer> getCusInt03List() {
        return cusInt03List;
    }
    
    public void setCusInt03List(List<Integer> cusInt03List) {
        this.cusInt03List = cusInt03List;
    }
    
    public List<Integer> getCusInt04List() {
        return cusInt04List;
    }
    
    public void setCusInt04List(List<Integer> cusInt04List) {
        this.cusInt04List = cusInt04List;
    }
    
    public List<Integer> getCusInt05List() {
        return cusInt05List;
    }
    
    public void setCusInt05List(List<Integer> cusInt05List) {
        this.cusInt05List = cusInt05List;
    }
    
    public List<Integer> getCusInt06List() {
        return cusInt06List;
    }
    
    public void setCusInt06List(List<Integer> cusInt06List) {
        this.cusInt06List = cusInt06List;
    }
    
    public List<Integer> getCusInt07List() {
        return cusInt07List;
    }
    
    public void setCusInt07List(List<Integer> cusInt07List) {
        this.cusInt07List = cusInt07List;
    }
    
    public List<Integer> getCusInt08List() {
        return cusInt08List;
    }
    
    public void setCusInt08List(List<Integer> cusInt08List) {
        this.cusInt08List = cusInt08List;
    }
    
    public List<Integer> getCusInt09List() {
        return cusInt09List;
    }
    
    public void setCusInt09List(List<Integer> cusInt09List) {
        this.cusInt09List = cusInt09List;
    }
    
    public List<Integer> getCusInt10List() {
        return cusInt10List;
    }
    
    public void setCusInt10List(List<Integer> cusInt10List) {
        this.cusInt10List = cusInt10List;
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

    public long getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(long selectedItemId) {
        this.selectedItemId = selectedItemId;
    }           
    
}
