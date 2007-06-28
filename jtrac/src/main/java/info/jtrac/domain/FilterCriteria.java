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
    
    public FilterCriteria(ColumnHeading columnHeading) {
        this.columnHeading = columnHeading;
    }
    
    public List<Expression> getExpressionList() {
        List<Expression> list = new ArrayList<Expression>();
        if(columnHeading.isField()) {
            switch(columnHeading.getField().getName().getType()) {
                case 1:
                case 2:
                case 3:
                    list.add(Expression.IN);
                    list.add(Expression.NOT_IN);
                    break; // drop down list
                case 4: // decimal number
                    list.add(Expression.EQ);
                    list.add(Expression.NOT_EQ);
                case 6: // date
                    list.add(Expression.GE);
                    list.add(Expression.LE);
                    break;
                case 5: // free text
                    list.add(Expression.CONTAINS);
                    break;
                default: 
                    throw new RuntimeException("Unknown Column Heading " + columnHeading.getName());
            }
        } else {
            if(columnHeading.getName().equals(ColumnHeading.ASSIGNED_TO)
                || columnHeading.getName().equals(ColumnHeading.LOGGED_BY)
                || columnHeading.getName().equals(ColumnHeading.STATUS)) {
                list.add(Expression.IN);
                list.add(Expression.NOT_IN);                
            } else if(columnHeading.getName().equals(ColumnHeading.TIME_STAMP)) {
                list.add(Expression.GE);
                list.add(Expression.LE);                
            } else {
                throw new RuntimeException("Unknown Column Heading " + columnHeading.getName());
            }                        
        }
        setExpression(list.get(0));
        return list;
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
    
}
