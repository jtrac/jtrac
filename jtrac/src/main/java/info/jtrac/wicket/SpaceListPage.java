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

import info.jtrac.domain.Space;
import java.util.List;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * space management page
 */
public class SpaceListPage extends BasePage {
    
    private long selectedSpaceId;
    
    public void setSelectedSpaceId(long selectedSpaceId) {
        this.selectedSpaceId = selectedSpaceId;
    }
      
    public SpaceListPage() {
        
        super("Space List");      
        
        add(new HeaderPanel(null));
        
        border.add(new Link("create") {
            public void onClick() {
                SpaceFormPage page = new SpaceFormPage();
                page.setPrevious(SpaceListPage.this);
                setResponsePage(page);
            }            
        });
        
        List<Space> spaces = getJtrac().findAllSpaces();
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        ListView listView = new ListView("spaces", spaces) {
            protected void populateItem(ListItem listItem) {                
                final Space space = (Space) listItem.getModelObject();                
                if (selectedSpaceId == space.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                                 
                listItem.add(new Label("prefixCode", new PropertyModel(space, "prefixCode")));
                listItem.add(new Label("name", new PropertyModel(space, "name")));
                Link edit = new Link("edit") {
                    public void onClick() {
                        Space temp = getJtrac().loadSpace(space.getId());
                        temp.getMetadata().getXmlString();  // hack to override lazy loading
                        SpaceFormPage page = new SpaceFormPage(temp);
                        page.setPrevious(SpaceListPage.this);
                        setResponsePage(page);                        
                    }                    
                };
                listItem.add(edit);
                listItem.add(new Label("description", new PropertyModel(space, "description")));
                listItem.add(new Link("allocate") {
                    public void onClick() {
                        Space temp = getJtrac().loadSpace(space.getId());
                        temp.getMetadata().getXmlString();  // hack to override lazy loading                        
                        setResponsePage(new SpaceAllocatePage(temp, SpaceListPage.this));
                    }                    
                });
            }            
        };
        
        border.add(listView);
        
    }
    
}
