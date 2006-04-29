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
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

/**
 * Object that holds filter criteria when searching for Items
 */
public class ItemSearch implements Serializable {
    
    private List<Field> fields; // column names
    private Space space; // if null, means aggregate across all spaces
    
    private int rowsPerPage;
    private Field.Name sortFieldName;    
    private boolean sortDescending;
    private boolean showHistory;
    private boolean showDescription;
    
    private String refId;
    private String summary;
    private String loggedDateStart;
    private String loggedDateEnd;
    private String historyDateStart;
    private String historyDateEnd;

    private Set<Integer> spaceSet;
    private Set<Integer> stateSet;
    private Set<Integer> severitySet;
    private Set<Integer> prioritySet;
    private Set<User> loggedBySet;
    private Set<User> assignedToSet;

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
    
    public ItemSearch() {
        // TODO
    }
    
    public ItemSearch(Space space) {
        fields = space.getMetadata().getFieldList();
        this.space = space;
    }    
    
    public List<Field> getFields() {
        return fields;
    }    
    
    public DetachedCriteria getCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Item.class, "item");        
        criteria.addOrder(Order.desc("id"));        
        return criteria;
    }
    
    //=====================================================================
        
    
}
