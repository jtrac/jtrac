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
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Object that holds filter criteria when searching for Items
 */
public class ItemSearch implements Serializable {
    
    private Map<String, String> columns;
    
    public ItemSearch() {
        
    }
    
    public ItemSearch(Space space) {
        columns = new LinkedHashMap<String, String>();     
        columns.put("summary", "Summary");
        columns.put("loggedByText", "Logged By");
        columns.put("statusText", "Status");
        columns.put("assignedToText", "Assigned To");
        Map<Field.Name, Field> fields = space.getMetadata().getFields();
        for(Field.Name fieldName : space.getMetadata().getFieldOrder()) {
            Field field = fields.get(fieldName);
            columns.put(fieldName + "Text", field.getLabel());
        }
        columns.put("timeStamp", "Time Stamp");
    }
    
    public DetachedCriteria getCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Item.class, "item");        
        criteria.addOrder(Order.desc("id"));
        return criteria;
    }

    public Map<String, String> getColumns() {
        return columns;
    }
    
}
