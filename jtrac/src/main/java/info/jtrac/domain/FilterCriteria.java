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
import java.util.ArrayList;
import java.util.List;

public class FilterCriteria implements Serializable {        
    
    private ColumnHeading columnHeading;
    
    public FilterCriteria(ColumnHeading columnHeading) {
        this.columnHeading = columnHeading;
    }
        
    public static List<ColumnHeading> getColumnHeadings(Space s) {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>();
        list.add(new ColumnHeading("id"));
        list.add(new ColumnHeading("summary"));        
        list.add(new ColumnHeading("detail"));        
        list.add(new ColumnHeading("loggedBy"));
        list.add(new ColumnHeading("status"));
        list.add(new ColumnHeading("assignedTo"));
        for(Field f : s.getMetadata().getFieldList()) {
            list.add(new ColumnHeading(f));
        }
        list.add(new ColumnHeading("timeStamp"));
        return list;        
    }            
    

    public ColumnHeading getColumnHeading() {
        return columnHeading;
    }

    public void setColumnHeading(ColumnHeading columnHeading) {
        this.columnHeading = columnHeading;
    }
    
}
