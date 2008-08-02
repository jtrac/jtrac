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

package info.jtrac.wicket;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

/**
 * reused in space allocate and user allocate admin screens
 */
public class RoleAllocatePanel extends BasePanel {  
    
    private List<String> choices;
    private List<String> selected = new ArrayList<String>();

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }    
    
    public List<String> getSelected() {
        return selected;
    }        
    
    public RoleAllocatePanel(String id) {
        super(id);                       
        CheckGroup checkGroup = new CheckGroup("checkGroup", new PropertyModel(this, "selected"));
        add(checkGroup);
        checkGroup.add(new ListView("roleKeys", new PropertyModel(this, "choices")) {            
            protected void populateItem(ListItem listItem) {
                String roleKey = (String) listItem.getModelObject();
                listItem.add(new Check("checkBox", listItem.getModel()));
                listItem.add(new Label("roleKey", roleKey));
            }
        });
    }

}
