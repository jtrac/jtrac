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
import java.util.List;

public class FilterCriteria implements Serializable {
    
    public enum Expression {        
        IN("in"), 
        NOT_IN("notIn"),
        CONTAINS("like"),
        EQ("equal"),
        NOT_EQ("notEqual"),
        GE("greaterEqual"),
        LE("lessEqual");
                
        private String key;
        
        Expression(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }             
    }
    
    private ColumnHeading columnHeading;
    private Expression expression;
    private List values;
    private Object value;    
    
    public FilterCriteria(ColumnHeading columnHeading) {
        this.columnHeading = columnHeading;
    }
    
    public ColumnHeading getColumnHeading() {
        return columnHeading;
    }
    
    public void setColumnHeading(ColumnHeading columnHeading) {
        this.columnHeading = columnHeading;
    }

    public FilterCriteria.Expression getExpression() {
        return expression;
    }

    public void setExpression(FilterCriteria.Expression expression) {
        this.expression = expression;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }
    
}
