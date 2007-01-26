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

import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * dashboard page
 */
public class ItemListPage extends BasePage {
      
    public ItemListPage(List<Item> items, ItemSearch itemSearch) {
        
        super("Item Search Results");
        
        long resultCount = itemSearch.getResultCount();
        border.add(new Label("count", resultCount + ""));
        
        int pageSize = itemSearch.getPageSize();
        int pageCount = 0;
        if (pageSize != -1) {
            pageCount = (int) Math.ceil((double) resultCount / pageSize);
        }        
        
        border.add(new Link("prev") {
            public void onClick() {
                
            }            
        });
        
        List<Integer> pageNumbers = new ArrayList<Integer>(pageCount);        
        for(int i = 0; i < pageCount; i++) {
            pageNumbers.add(new Integer(i));
        }
        
        ListView pages = new ListView("pages", pageNumbers) {
            protected void populateItem(ListItem listItem) {
                Integer i = (Integer) listItem.getModelObject();
                Link link = new Link("page") {
                    public void onClick() {
                        
                    }
                };
                link.add(new Label("page", i + 1 + ""));
                listItem.add(link);
            }            
        };
                
        border.add(pages);
        
        border.add(new Link("next") {
            public void onClick() {
                
            }            
        });        
        
        final List<Field> fields = itemSearch.getFields();
        
        ListView labels = new ListView("labels", fields) {
            protected void populateItem(ListItem listItem) {
                Field field = (Field) listItem.getModelObject();
                listItem.add(new Label("label", field.getLabel()));
            }            
        };        
        
        border.add(labels);
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        ListView itemList = new ListView("itemList", items) {
            protected void populateItem(ListItem listItem) {
                if(listItem.getIndex() % 2 != 0) {
                    listItem.add(sam);
                }                
                final Item item = (Item) listItem.getModelObject();                
                Link link = new Link("refId") {
                    public void onClick() {
                        setResponsePage(new ItemViewPage(item));
                    }
                };
                link.add(new Label("refId", new PropertyModel(item, "refId")));                                
                listItem.add(link);                
                listItem.add(new Label("summary", new PropertyModel(item, "summary")));                
                listItem.add(new Label("loggedBy", new PropertyModel(item, "loggedBy.name")));
                listItem.add(new Label("status", new PropertyModel(item, "statusValue")));
                listItem.add(new Label("assignedTo", new PropertyModel(item, "assignedTo.name")));                               
                ListView fieldValues = new ListView("fields", fields) {
                    protected void populateItem(ListItem listItem) {
                        Field field = (Field) listItem.getModelObject();
                        listItem.add(new Label("field", item.getCustomValue(field.getName())));
                    }                    
                };                
                listItem.add(fieldValues);                
                listItem.add(new Label("timeStamp", DateUtils.formatTimeStamp(item.getTimeStamp())));
            }            
        };
        
        border.add(itemList);        
        
    }

    
}
