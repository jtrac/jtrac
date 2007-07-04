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

/**
 * can possibly be merged into ColumnHeading, but at the moment
 * hold filter criteria entered by user for search
 * value = for single values
 * value2 = second value for "between" kind of queries
 * values = list of values for multi-select filter criteria
 */
public class FilterCriteria implements Serializable {
    
    public enum Expression {        
        IN("in"), 
        NOT_IN("notIn"),
        CONTAINS("like"),
        EQ("equal"),
        NOT_EQ("notEqual"),
        GT("greaterThan"),
        LT("lessThan"),        
        BETWEEN("between");
                
        private String key;
        
        Expression(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }             
    }
        
    private Expression expression;
    private List values;
    private Object value;
    private Object value2;

    private Expression previousExpression;
    
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        previousExpression = this.expression;
        this.expression = expression;
        if(expression == null) {
            values = null;
            value = null;
            value2 = null;
        }
    }
    
    public boolean requiresUiFragmentUpdate() {
        if(expression != null && previousExpression != null) {
            if(expression == Expression.BETWEEN || previousExpression == Expression.BETWEEN) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }    
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue2() {
        return value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("expression [").append(expression);
        sb.append("]; value [").append(value);
        sb.append("]; value2 [").append(value2);
        sb.append("]; values [").append(values);
        sb.append("]");
        return sb.toString();        
    }
    
}
