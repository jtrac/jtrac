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

import info.jtrac.domain.AbstractItem;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import wicket.Component;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Fragment;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.LoadableDetachableModel;
import wicket.model.PropertyModel;

/**
 * dashboard page
 */
public class ItemListPage extends BasePage {      
    
    private long selectedItemId;    
    
    public void setSelectedItemId(long selectedItemId) {
        this.selectedItemId = selectedItemId;
    }    
    
    private ItemListPage getItemListPage(ItemSearch itemSearch, String sortFieldName) {
        itemSearch.setCurrentPage(0);        
        if (itemSearch.getSortFieldName().equals(sortFieldName)) {  
            itemSearch.toggleSortDirection();
        } else {            
            itemSearch.setSortFieldName(sortFieldName);
            itemSearch.setSortDescending(false);
        }      
        ItemListPage page = new ItemListPage(itemSearch);
        page.setSelectedItemId(selectedItemId);
        return page;
    }
    
    public ItemListPage(final ItemSearch itemSearch) {
        
        super("Item Search Results");
        
        add(new HeaderPanel(null));
        
        LoadableDetachableModel itemListModel = new LoadableDetachableModel() {
            protected Object load() {
                logger.debug("loading item list from database");
                return getJtrac().findItems(itemSearch);
            }
        };        
        
        //======================== PAGINATION ==================================
        // on the pagination components below, isVisible() methods have been 
        // carefully overridden to dynamically hide / show components 
        // but ensure lazy loading
        
        Link link = new Link("count") {
            public void onClick() {                
                setResponsePage(new ItemSearchFormPage(itemSearch));
            }
        };        
        link.add(new Label("count", new PropertyModel(itemSearch, "resultCount")));        
        border.add(link);                                             
        
        WebMarkupContainer pagination = new WebMarkupContainer("pagination") {
            @Override
            public boolean isVisible() {
                return itemSearch.getPageCount() > 1;
            }            
        };
                
        final int currentPage = itemSearch.getCurrentPage();                        

        AbstractReadOnlyModel pageNumbersModel = new AbstractReadOnlyModel() {            
            public Object getObject(Component c) {
                int pageCount = itemSearch.getPageCount();                
                List<Integer> pageNumbers = new ArrayList<Integer>(pageCount);        
                for(int i = 0; i < pageCount; i++) {
                    pageNumbers.add(new Integer(i));
                }
                return pageNumbers;
            }
        };

        Link prevOn = new Link("prevOn") {
            public void onClick() {
                itemSearch.setCurrentPage(currentPage - 1);
                setResponsePage(new ItemListPage(itemSearch));
            } 
            @Override
            public boolean isVisible() {
                return !itemSearch.isFirstPage();
            }             
        };
        prevOn.add(new Label("prevOn", "<<"));
        Label prevOff = new Label("prevOff", "<<") {
            @Override
            public boolean isVisible() {
                return itemSearch.isFirstPage();
            }             
        };          
        pagination.add(prevOn);
        pagination.add(prevOff);

        ListView pages = new ListView("pages", pageNumbersModel) {
            protected void populateItem(ListItem listItem) {
                final Integer i = (Integer) listItem.getModelObject();
                String pageNumber = i + 1 + "";
                Link pageOn = new Link("pageOn") {
                    public void onClick() {
                        itemSearch.setCurrentPage(i);
                        setResponsePage(new ItemListPage(itemSearch));
                    } 
                    @Override
                    public boolean isVisible() {
                        return i != itemSearch.getCurrentPage();
                    }                    
                };
                pageOn.add(new Label("pageOn", pageNumber));
                Label pageOff = new Label("pageOff", pageNumber) {
                    @Override
                    public boolean isVisible() {
                        return i == itemSearch.getCurrentPage();
                    }                     
                };
                listItem.add(pageOn);
                listItem.add(pageOff);
            }            
        };                
        pagination.add(pages);            
        
        Link nextOn = new Link("nextOn") {
            public void onClick() {
                itemSearch.setCurrentPage(currentPage + 1);
                setResponsePage(new ItemListPage(itemSearch));
            }
            @Override
            public boolean isVisible() {
                return !itemSearch.isLastPage();
            }
        };
        nextOn.add(new Label("nextOn", ">>"));
        Label nextOff = new Label("nextOff", ">>") {
            @Override
            public boolean isVisible() {
                return itemSearch.isLastPage();
            }            
        };
        pagination.add(nextOn);
        pagination.add(nextOff);            
        
        border.add(pagination);
        
        //====================== HEADER ========================================
                
        if(itemSearch.isShowDetail()) {
            border.add(new Label("detail", getLocalizer().getString("item_view.detail", null)));
        } else {
            border.add(new Label("detail", "").setVisible(false));
        }
        
        final SimpleAttributeModifier orderClass;
        
        if (itemSearch.isSortDescending()) {
             orderClass = new SimpleAttributeModifier("class", "order-down");
        } else {
             orderClass = new SimpleAttributeModifier("class", "order-up");
        }
        
        String[] headings = new String[] { "id", "summary", "loggedBy", "status", "assignedTo", "timeStamp" };
        
        for(final String s : headings) {            
            Link headingLink = new Link(s) {
                public void onClick() {                                    
                    setResponsePage(getItemListPage(itemSearch, s));
                }                
            };
            headingLink.add(new Label(s, getLocalizer().getString("item_view." + s, null)));
            if (s.equals(itemSearch.getSortFieldName())) {
                headingLink.add(orderClass);
            }
            border.add(headingLink);
        }        
        
        final List<Field> fields = itemSearch.getFields();
        
        ListView labels = new ListView("labels", fields) {
            protected void populateItem(ListItem listItem) {
                final Field field = (Field) listItem.getModelObject();                
                Link headingLink = new Link("label") {
                    public void onClick() {                        
                        setResponsePage(getItemListPage(itemSearch, field.getName().getText()));                       
                    }                
                };
                listItem.add(headingLink);
                headingLink.add(new Label("label", field.getLabel()));
                if (field.getName().getText().equals(itemSearch.getSortFieldName())) {
                    headingLink.getParent().add(orderClass);
                }                
            }            
        };        
        
        border.add(labels);                     
        
        //======================== ITEMS =======================================
        

        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        ListView itemList = new ListView("itemList", itemListModel) {
            protected void populateItem(ListItem listItem) { 
                // cast to AbstactItem - show history may be == true
                final AbstractItem item = (AbstractItem) listItem.getModelObject(); 
                
                if (selectedItemId == item.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                                 
                
                // detail <--> refId link <--> showHistory logic ===============
                
                WebMarkupContainer detail = new WebMarkupContainer("detail");                
                detail.setVisible(false);
                
                Link refIdLink = null;
                
                if (itemSearch.isShowHistory()) { 
                    final History history = (History) item;
                    refIdLink = new Link("refId") {
                        public void onClick() {
                            // this is a history record
                            setResponsePage(new ItemViewPage(history.getParent(), ItemListPage.this));
                        }
                    };
                    String refId = history.getRefId();
                    int index = history.getIndex();                    
                    if (index > 0) {
                        refIdLink.add(new Label("index", " (" + index + ")"));
                    } else {
                        refIdLink.add(new Label("index", "").setVisible(false));
                    }
                    refIdLink.add(new Label("refId", history.getRefId()));
                    if(itemSearch.isShowDetail()) {
                        detail.setVisible(true);
                        detail.add(new AttachmentLinkPanel("attachment", history.getAttachment()));
                        if (index > 0) {
                            detail.add(new Label("detail", new PropertyModel(history, "comment")));
                        } else {
                            detail.add(new Label("detail", new PropertyModel(history, "detail")));
                        }
                    }                   
                } else {                
                    refIdLink = new Link("refId") {
                        public void onClick() {
                            // this is an item record
                            setResponsePage(new ItemViewPage((Item) item, ItemListPage.this));
                        }
                    };
                    refIdLink.add(new Label("refId", new PropertyModel(item, "refId")));
                    if(itemSearch.isShowDetail()) {
                        detail.setVisible(true);
                        detail.add(new Label("attachment", "").setVisible(false));
                        detail.add(new Label("detail", new PropertyModel(item, "detail")));
                    } 
                    refIdLink.add(new Label("index", "").setVisible(false));
                }                                
                                               
                listItem.add(refIdLink);    
                
                listItem.add(detail);
                
                // end detail <--> refId link <--> showHistory logic ===========
                
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
