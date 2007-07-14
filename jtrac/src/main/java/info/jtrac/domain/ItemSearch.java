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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.Component;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Object that holds filter criteria when searching for Items
 * and also creates a Hibernate Criteria query to pass to the DAO
 */
public class ItemSearch implements Serializable {
        
    private Space space; // if null, means aggregate across all spaces
    private User user; // this will be set in the case space is null
    
    private int pageSize = 25;
    private int currentPage;
    private long resultCount;
    private String sortFieldName = "id";
    private boolean sortDescending = true;
    private boolean showHistory;
    private boolean showDetail;
        
    private long selectedItemId;
    private String relatingItemRefId;    
    private Collection<Long> itemIds;
    
    private List<ColumnHeading> columnHeadings;
    private Map<String, FilterCriteria> filterCriteriaMap = new LinkedHashMap<String, FilterCriteria>();
    
    public ItemSearch(User user, Component c) {
        this.user = user;
        this.columnHeadings = ColumnHeading.getColumnHeadings(user, c);
    }
    
    public ItemSearch(Space space, Component c) {        
        this.space = space;        
        this.columnHeadings = ColumnHeading.getColumnHeadings(space, c);
    }      
    
    private DetachedCriteria parent; // temp working variable hack
    
    // have to do this two step process as "order by" clause conflicts with "count (*)" clause
    // so the DAO has to use getCriteriaForCount() separately
    public DetachedCriteria getCriteria() {
        DetachedCriteria criteria = getCriteriaForCount();
        if (sortFieldName == null) { // can happen only for multi-space search
            sortFieldName = "id"; // effectively is a sort on created date
        }
        if(sortFieldName.equals("id") || sortFieldName.equals("space")) {
            if(showHistory) {
                // if showHistory: sort by item.id and then history.id
                if(sortDescending) {
                    if(space == null) {
                        DetachedCriteria parentSpace = parent.createCriteria("space");
                        parentSpace.addOrder(Order.desc("name"));                        
                    }                    
                    criteria.addOrder(Order.desc("parent.id"));
                    criteria.addOrder(Order.desc("id"));
                } else {
                    if(space == null) {
                        DetachedCriteria parentSpace = parent.createCriteria("space");
                        parentSpace.addOrder(Order.asc("name"));                        
                    }                   
                    criteria.addOrder(Order.asc("parent.id"));
                    criteria.addOrder(Order.asc("id"));                
                }
            } else {
                if (sortDescending) {
                    if(space == null) {
                        DetachedCriteria parentSpace = criteria.createCriteria("space");
                        parentSpace.addOrder(Order.desc("name"));
                    }
                    criteria.addOrder(Order.desc("id"));
                } else {
                    if(space == null) {
                        DetachedCriteria parentSpace = criteria.createCriteria("space");
                        parentSpace.addOrder(Order.asc("name"));
                    }                    
                    criteria.addOrder(Order.asc("id"));
                }                 
            }
        } else {        
            if (sortDescending) {
                criteria.addOrder(Order.desc(sortFieldName));
            } else {
                criteria.addOrder(Order.asc(sortFieldName));
            } 
        }
        return criteria;
    }
    
    public DetachedCriteria getCriteriaForCount() {               
        DetachedCriteria criteria = null;        
        if (showHistory) {
            criteria = DetachedCriteria.forClass(History.class);           
            // apply restrictions to parent, this is an inner join =============
            parent = criteria.createCriteria("parent");
            if(space == null) {
                parent.add(Restrictions.in("space", getSelectedSpaces()));
            } else {
                parent.add(Restrictions.eq("space", space));
            } 
            if (itemIds != null) {
                parent.add(Restrictions.in("id", itemIds));
            }             
        } else {
            criteria = DetachedCriteria.forClass(Item.class);
            if(space == null) {
                criteria.add(Restrictions.in("space", getSelectedSpaces()));
            } else {
                criteria.add(Restrictions.eq("space", space));
            } 
            if (itemIds != null) {
                criteria.add(Restrictions.in("id", itemIds));
            }             
        }        
        for(ColumnHeading ch : columnHeadings) {
            ch.addRestrictions(criteria, this);
        }
        return criteria;
    }
    
    public List<Field> getFields() {
        if(space == null) {
            List<Field> list = new ArrayList<Field>(2);
            Field severity = new Field(Field.Name.SEVERITY);
            severity.initOptions();
            list.add(severity);
            Field priority = new Field(Field.Name.PRIORITY);
            priority.initOptions();
            list.add(priority);
            return list;
        } else {
            return space.getMetadata().getFieldList();
        }        
    }    
    
    private ColumnHeading getColumnHeading(String name) {
        for(ColumnHeading ch : columnHeadings) {
            if(ch.getName().equals(name)) {
                return ch;                
            }
        }
        return null;                
    }
    
    public String getRefId() {
        ColumnHeading ch = getColumnHeading(ColumnHeading.ID);
        return (String) ch.getFilterCriteria().getValue();
    }
    
    public String getSearchText() {
        ColumnHeading ch = getColumnHeading(ColumnHeading.DETAIL);
        String s = (String) ch.getFilterCriteria().getValue();
        if(s != null && s.trim().equals("")) {
            ch.getFilterCriteria().setValue(null);
            return null;
        }
        return s;
    }
    
    public Collection<Space> getSelectedSpaces() {
        ColumnHeading ch = getColumnHeading(ColumnHeading.SPACE);
        List values = ch.getFilterCriteria().getValues();
        if(values == null || values.size() == 0) {
            return user.getSpaces();
        }
        return values;
    }
           
    public void toggleSortDirection() {
        sortDescending = !sortDescending;
    }      
    
    private List getSingletonList(Object o) {
        List list = new ArrayList(1);
        list.add(o);
        return list;
    }
    
    public void setLoggedBy(User loggedBy) {
        ColumnHeading ch = getColumnHeading(ColumnHeading.LOGGED_BY);
        ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IN);
        ch.getFilterCriteria().setValues(getSingletonList(loggedBy));
    }
    
    public void setAssignedTo(User assignedTo) {
        ColumnHeading ch = getColumnHeading(ColumnHeading.ASSIGNED_TO);
        ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IN);
        ch.getFilterCriteria().setValues(getSingletonList(assignedTo));
    }
    
    public void setStatus(int i) {
        ColumnHeading ch = getColumnHeading(ColumnHeading.STATUS);
        ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IN);
        ch.getFilterCriteria().setValues(getSingletonList(i));
    }
    
    public List<ColumnHeading> getColumnHeadingsToRender() {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>(columnHeadings.size());
        for(ColumnHeading ch : columnHeadings) {
            if(ch.isVisible()) {
                list.add(ch);
            }
        }
        return list;
    }
    
    //==========================================================================

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public long getResultCount() {
        return resultCount;
    }

    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
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

    public long getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(long selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String getRelatingItemRefId() {
        return relatingItemRefId;
    }

    public void setRelatingItemRefId(String relatingItemRefId) {
        this.relatingItemRefId = relatingItemRefId;
    }

    public Collection<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Collection<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public List<ColumnHeading> getColumnHeadings() {
        return columnHeadings;
    }

    public void setColumnHeadings(List<ColumnHeading> columnHeadings) {
        this.columnHeadings = columnHeadings;
    }

    public Map<String, FilterCriteria> getFilterCriteriaMap() {
        return filterCriteriaMap;
    }

    public void setFilterCriteriaMap(Map<String, FilterCriteria> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
    }
    
}
