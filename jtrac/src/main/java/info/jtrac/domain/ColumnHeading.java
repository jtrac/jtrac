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

/**
 * used to render columns in the search results table
 * and also in the search filter screen
 */
public class ColumnHeading implements Serializable {
    
    private Field field;
    private String name;
    
    public ColumnHeading(String name) {
        this.name = name;
    }
    
    public ColumnHeading(Field field) {
        this.field = field;
        this.name = field.getName().getText();
    }
    
    public Field getField() {
        return field;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isField() {
        return field != null;
    }
    
}
