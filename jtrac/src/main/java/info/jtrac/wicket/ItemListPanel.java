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
import info.jtrac.domain.ColumnHeading;
import info.jtrac.domain.History;
import info.jtrac.domain.ItemSearch;
import info.jtrac.util.DateUtils;
import info.jtrac.util.ExcelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;

/**
 * item list panel
 */
public class ItemListPanel extends BasePanel {
    
    private ItemSearch itemSearch;
    
    private void doSort(String sortFieldName) {
        itemSearch.setCurrentPage(0);
        if (itemSearch.getSortFieldName().equals(sortFieldName)) {
            itemSearch.toggleSortDirection();
        } else {
            itemSearch.setSortFieldName(sortFieldName);
            itemSearch.setSortDescending(false);
        }
    }
    
    public ItemListPanel(final String id) {
        super(id);
        this.itemSearch = getCurrentItemSearch();
        LoadableDetachableModel itemListModel = new LoadableDetachableModel() {
            protected Object load() {
                logger.debug("loading item list from database");
                return getJtrac().findItems(itemSearch);
            }
        };
        
        // hack - ensure that wicket model "attach" happens NOW before pagination logic sp that
        // itemSearch is properly initialized in the LoadableDetachableModel#load() above
        itemListModel.getObject();
        
        //======================== PAGINATION ==================================
        
        int pageCount = 1;
        final int pageSize = itemSearch.getPageSize();
        long resultCount = itemSearch.getResultCount();
        if (pageSize != -1) {
            pageCount = (int) Math.ceil((double) resultCount / pageSize);
        }
        final int currentPage = itemSearch.getCurrentPage();
        
        Link link = new Link("count") {
            public void onClick() {
                // return to item search form
                itemSearch.setCurrentPage(0);
                setResponsePage(new ItemSearchFormPage(itemSearch));
            }
        };
        link.add(new Label("count", resultCount + ""));
        String resultCountMessage = resultCount == 1 ? "item_list.recordFound" : "item_list.recordsFound";
        link.add(new Label("recordsFound", localize(resultCountMessage)));        
        add(link);        
        
        WebMarkupContainer pagination = new WebMarkupContainer("pagination");
        
        if(pageCount > 1) {
            Link prevOn = new Link("prevOn") {
                public void onClick() {
                    itemSearch.setCurrentPage(currentPage - 1);
                    setCurrentItemSearch(itemSearch);
                    // TODO avoid next line, refresh pagination only
                    setResponsePage(ItemListPage.class);                    
                }
            };
            prevOn.add(new Label("prevOn", "<<"));
            Label prevOff = new Label("prevOff", "<<");
            if(currentPage == 0) {
                prevOn.setVisible(false);
            } else {
                prevOff.setVisible(false);
            }
            pagination.add(prevOn);
            pagination.add(prevOff);
            
            List<Integer> pageNumbers = new ArrayList<Integer>(pageCount);
            for(int i = 0; i < pageCount; i++) {
                pageNumbers.add(new Integer(i));
            }
            
            ListView pages = new ListView("pages", pageNumbers) {
                protected void populateItem(ListItem listItem) {
                    final Integer i = (Integer) listItem.getModelObject();
                    String pageNumber = i + 1 + "";
                    Link pageOn = new Link("pageOn") {
                        public void onClick() {
                            itemSearch.setCurrentPage(i);
                            setCurrentItemSearch(itemSearch);
                            // TODO avoid next line, refresh pagination only
                            setResponsePage(ItemListPage.class);
                        }
                    };
                    pageOn.add(new Label("pageOn", pageNumber));
                    Label pageOff = new Label("pageOff", pageNumber);
                    if(i == currentPage) {
                        pageOn.setVisible(false);
                    } else {
                        pageOff.setVisible(false);
                    }
                    listItem.add(pageOn);
                    listItem.add(pageOff);
                }
            };
            pagination.add(pages);
            
            Link nextOn = new Link("nextOn") {
                public void onClick() {
                    itemSearch.setCurrentPage(currentPage + 1);
                    setCurrentItemSearch(itemSearch);
                    // TODO avoid next line, refresh pagination only
                    setResponsePage(ItemListPage.class);                    
                }
            };
            nextOn.add(new Label("nextOn", ">>"));
            Label nextOff = new Label("nextOff", ">>");
            if(currentPage == pageCount - 1) {
                nextOn.setVisible(false);
            } else {
                nextOff.setVisible(false);
            }
            pagination.add(nextOn);
            pagination.add(nextOff);
        } else { // if pageCount == 1
            pagination.setVisible(false);
        }
        
        add(pagination);
        
        //========================== EXCEL EXPORT ==============================
        
        add(new Link("export") {
            public void onClick() {
                // temporarily switch off paging of results
                itemSearch.setPageSize(-1);
                final ExcelUtils eu = new ExcelUtils(getJtrac().findItems(itemSearch), itemSearch);
                // restore page size
                itemSearch.setPageSize(pageSize);
                getRequestCycle().setRequestTarget(new IRequestTarget() {
                    public void detach(RequestCycle requestCycle) {
                    }
                    public void respond(RequestCycle requestCycle) {
                        WebResponse r = (WebResponse) requestCycle.getResponse();
                        r.setAttachmentHeader("jtrac-export.xls");
                        try {                            
                            eu.exportToExcel().write(r.getOutputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        
        //====================== HEADER ========================================        
        
        final List<ColumnHeading> columnHeadings = itemSearch.getColumnHeadingsToRender();
        
        ListView headings = new ListView("headings", columnHeadings) {
            protected void populateItem(ListItem listItem) {
                final ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                Link headingLink = new Link("heading") {
                    public void onClick() {
                        doSort(ch.getName());
                    }
                };
                listItem.add(headingLink); 
                String label = ch.isField() ? ch.getLabel() : localize("item_list." + ch.getName());
                headingLink.add(new Label("heading", label));
                if (ch.getName().equals(itemSearch.getSortFieldName())) {
                    String order = itemSearch.isSortDescending() ? "order-down" : "order-up";
                    listItem.add(new SimpleAttributeModifier("class", order));
                }
            }
        };
        
        add(headings);
        
        //======================== ITEMS =======================================
        
        final long selectedItemId = itemSearch.getSelectedItemId();
        
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
                
                final boolean showHistory = itemSearch.isShowHistory();
                
                ListView fieldValues = new ListView("columns", columnHeadings) {
                    protected void populateItem(ListItem listItem) {
                        ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                        IModel value = null;
                        if(ch.isField()) {
                            value = new Model(item.getCustomValue(ch.getField().getName()));
                        } else {
                            // TODO optimize if-then for performance
                            String name = ch.getName();
                            if(name.equals(ColumnHeading.ID)) {
                                String refId = item.getRefId();
                                Fragment refIdFrag = new Fragment("column", "refId");
                                listItem.add(refIdFrag);
                                Link refIdLink = new BookmarkablePageLink("refId", ItemViewPage.class, new PageParameters("0=" + refId));                                
                                refIdFrag.add(refIdLink);
                                refIdLink.add(new Label("refId", refId));
                                if (showHistory) {                                                                                                            
                                    int index = ((History) item).getIndex();
                                    if (index > 0) {
                                        refIdFrag.add(new Label("index", " (" + index + ")"));
                                    } else {
                                        refIdFrag.add(new WebMarkupContainer("index").setVisible(false));
                                    }
                                } else {                                                                           
                                    refIdFrag.add(new WebMarkupContainer("index").setVisible(false));
                                }                                                                
                                return;
                            } else if(name.equals(ColumnHeading.SUMMARY)) {
                                value = new PropertyModel(item, "summary");
                            } else if(name.equals(ColumnHeading.DETAIL)) {                                
                                if(showHistory) {
                                    Fragment detailFrag = new Fragment("column", "detail");
                                    final History history = (History) item;
                                    detailFrag.add(new AttachmentLinkPanel("attachment", history.getAttachment()));
                                    if (history.getIndex() > 0) {
                                        detailFrag.add(new Label("detail", new PropertyModel(history, "comment")));
                                    } else {
                                        detailFrag.add(new Label("detail", new PropertyModel(history, "detail")));
                                    }
                                    listItem.add(detailFrag);
                                    return;
                                } else {                                    
                                    value = new PropertyModel(item, "detail");                                    
                                }                               
                            } else if(name.equals(ColumnHeading.LOGGED_BY)) {
                                value = new PropertyModel(item, "loggedBy.name");
                            } else if(name.equals(ColumnHeading.STATUS)) {
                                value = new PropertyModel(item, "statusValue");
                            } else if(name.equals(ColumnHeading.ASSIGNED_TO)) {
                                value = new PropertyModel(item, "assignedTo.name");
                            } else if(name.equals(ColumnHeading.TIME_STAMP)) {
                                value = new Model(DateUtils.formatTimeStamp(item.getTimeStamp()));
                            } else if(name.equals(ColumnHeading.SPACE)) {
                                if(showHistory) {
                                    value = new PropertyModel(item, "parent.space.name");
                                } else {
                                    value = new PropertyModel(item, "space.name");
                                }
                            } else {
                                throw new RuntimeException("Unexpected name: '" + name + "'");
                            }
                        }
                        listItem.add(new Label("column", value));
                    }
                };
                
                listItem.add(fieldValues);
                
            }
        };
        
        add(itemList);
        
    }
    
}
