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

package info.jtrac.util;

import info.jtrac.Jtrac;
import info.jtrac.domain.ItemUser;
import java.beans.PropertyEditorSupport;

/**
 * Property Editor for binding / unbinding ItemUser objects into JSP forms
 */
public class ItemUserEditor extends PropertyEditorSupport {
    
    private Jtrac jtrac;
    
    public ItemUserEditor(Jtrac jtrac) {
        this.jtrac = jtrac;
    }
    
    @Override
    public void setAsText(String text) {
        if (text == null || text.equals("")) {
            setValue(null);
        } else {
            int id = Integer.parseInt(text);
            setValue(new ItemUser(jtrac.loadUser(id)));
        }
    }

    @Override
    public String getAsText() {
        Object o = getValue();
        if (o == null) {
            return "";
        }
        ItemUser itemUser = (ItemUser) o;
        return itemUser.getUser().getId() + "";
    }    
    
}
