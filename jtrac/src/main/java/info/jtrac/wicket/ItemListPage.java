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
import wicket.markup.html.panel.Fragment;
import wicket.model.PropertyModel;

/**
 * dashboard page
 */
public class ItemListPage extends BasePage {      
    
    public ItemListPage(final ItemSearch itemSearch) {
        
        super("Item Search Results");

        final List<Item> items = getJtrac().findItems(itemSearch);
        
        //======================== PAGINATION ===================================
        
        long resultCount = itemSearch.getResultCount();
        
        Link link = new Link("count") {
            public void onClick() {                
                setResponsePage(new ItemSearchFormPage(itemSearch));
            }
        };
        link.add(new Label("count", resultCount + ""));        
        border.add(link);
        
        int pageSize = itemSearch.getPageSize();
        int pageCount = 0;
        if (pageSize != -1) {
            pageCount = (int) Math.ceil((double) resultCount / pageSize);
        }                
        
        if(pageCount > 1) {
            
            Fragment pagination = new Fragment("pagination", "pagination");
            final int currentPage = itemSearch.getCurrentPage();
            
            List<Integer> pageNumbers = new ArrayList<Integer>(pageCount);        
            for(int i = 0; i < pageCount; i++) {
                pageNumbers.add(new Integer(i));
            }            
                        
            if (currentPage == 0) {
                pagination.add(new Label("prev", "<<"));
            } else {
                Fragment prev = new Fragment("prev", "prev");
                prev.add(new Link("prev") {
                    public void onClick() {
                        itemSearch.setCurrentPage(currentPage - 1);
                        setResponsePage(new ItemListPage(itemSearch));
                    }            
                });
                pagination.add(prev);
            }

            ListView pages = new ListView("pages", pageNumbers) {
                protected void populateItem(ListItem listItem) {
                    final Integer i = (Integer) listItem.getModelObject();
                    String pageNumber = i + 1 + "";
                    if (currentPage == i) {
                        listItem.add(new Label("page", pageNumber));
                    } else {
                        Fragment page = new Fragment("page", "page");
                        Link link = new Link("page") {
                            public void onClick() {
                                itemSearch.setCurrentPage(i);
                                setResponsePage(new ItemListPage(itemSearch));
                            }
                        };
                        link.add(new Label("page", pageNumber));
                        page.add(link);
                        listItem.add(page);
                    }
                }            
            };                
            pagination.add(pages);            
                        
            final int lastPage = pageCount - 1;
            if (currentPage == lastPage) {
                pagination.add(new Label("next", ">>"));
            } else {
                Fragment next = new Fragment("next", "next");

                next.add(new Link("next") {
                    public void onClick() {
                        itemSearch.setCurrentPage(currentPage + 1);
                        setResponsePage(new ItemListPage(itemSearch));
                    }            
                });
                pagination.add(next);
            }
            
            border.add(pagination);
            
        } else {
            border.add(new Label("pagination", ""));
        }
        
        //======================== ITEMS =======================================
        
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
