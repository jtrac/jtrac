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

import static info.jtrac.domain.ItemItem.*;

import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.ItemSearch;
import info.jtrac.util.DateUtils;
import info.jtrac.util.ItemUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

/**
 * panel for showing the item read-only view
 */
public class ItemViewPanel extends BasePanel {    
    
    private boolean hideLinks;
    
    public ItemViewPanel(String id, long itemId) {                
        super(id);
        addComponents(getJtrac().loadItem(itemId));
    }    
    
    public ItemViewPanel(String id, final Item item, boolean hideLinks) {                
        super(id);
        this.hideLinks = hideLinks;
        addComponents(item);
    }
    
    private void addComponents(final Item item) {      
        
        add(new Label("refId", new PropertyModel(item, "refId")));
        
        add(new Link("relate") {
            public void onClick() {
                // TODO choose specific space for search
                ItemSearch itemSearch = new ItemSearch(getPrincipal(), ItemViewPanel.this);
                itemSearch.setRelatingItemRefId(item.getRefId());
                setResponsePage(new ItemSearchFormPage(itemSearch));
            }
        }.setVisible(!hideLinks));        
        
        if(item.getRelatedItems() != null) {        
            add(new ListView("relatedItems", new ArrayList(item.getRelatedItems())) {            
                protected void populateItem(ListItem listItem) {
                    final ItemItem itemItem = (ItemItem) listItem.getModelObject();
                    String message = null;
                    if(itemItem.getType() == DUPLICATE_OF) {
                        message = localize("item_view.duplicateOf");
                    } else if (itemItem.getType() == DEPENDS_ON) {
                        message = localize("item_view.dependsOn");
                    } else if (itemItem.getType() == RELATED){
                        message = localize("item_view.relatedTo");                  
                    }
                    final String refId = itemItem.getRelatedItem().getRefId();
                    if(hideLinks) {
                        message = message + " " + refId;
                    }
                    listItem.add(new Label("message", message));
                    Link link = new Link("link") {
                        public void onClick() {
                            setResponsePage(ItemViewPage.class, new PageParameters("0=" + refId));
                        }
                    };
                    link.add(new Label("refId", refId));
                    link.setVisible(!hideLinks);
                    listItem.add(link);
                    listItem.add(new Link("remove") {
                        public void onClick() {
                            setResponsePage(new ItemRelateRemovePage(item.getId(), itemItem));
                        }
                    }.setVisible(!hideLinks));
                }
            });
        } else {
            add(new WebMarkupContainer("relatedItems").setVisible(false));
        }
        
        if(item.getRelatingItems() != null) {
            add(new ListView("relatingItems", new ArrayList(item.getRelatingItems())) {            
                protected void populateItem(ListItem listItem) {
                    final ItemItem itemItem = (ItemItem) listItem.getModelObject();
                    // this looks very similar to related items block above
                    // but the display strings could be different and in future handling of the 
                    // inverse of the bidirectional link could be different as well                    
                    String message = null;
                    if(itemItem.getType() == DUPLICATE_OF) {
                        message = localize("item_view.duplicateOfThis");
                    } else if (itemItem.getType() == DEPENDS_ON) {
                        message = localize("item_view.dependsOnThis");
                    } else if (itemItem.getType() == RELATED){
                        message = localize("item_view.relatedToThis");                  
                    }
                    final String refId = itemItem.getItem().getRefId();
                    if(hideLinks) {
                        message = refId + " " + message;
                    }                    
                    listItem.add(new Label("message", message));
                    Link link = new Link("link") {
                        public void onClick() {
                            setResponsePage(ItemViewPage.class, new PageParameters("0=" + refId));
                        }
                    };
                    link.add(new Label("refId", refId));
                    link.setVisible(!hideLinks);
                    listItem.add(link);
                    listItem.add(new Link("remove") {
                        public void onClick() {
                            setResponsePage(new ItemRelateRemovePage(item.getId(), itemItem));
                        }
                    }.setVisible(!hideLinks));                
                }
            });
        } else {
            add(new WebMarkupContainer("relatingItems").setVisible(false));
        }
        
        add(new Label("status", new PropertyModel(item, "statusValue")));
        add(new Label("loggedBy", new PropertyModel(item, "loggedBy.name")));
        add(new Label("assignedTo", new PropertyModel(item, "assignedTo.name")));
        add(new Label("summary", new PropertyModel(item, "summary")));
        add(new Label("detail", ItemUtils.fixWhiteSpace(item.getDetail())).setEscapeModelStrings(false));        
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        final Map<Field.Name, Field> fields = item.getSpace().getMetadata().getFields();
        add(new ListView("fields", item.getSpace().getMetadata().getFieldOrder()) {
            protected void populateItem(ListItem listItem) {
                if(listItem.getIndex() % 2 == 0) {
                    listItem.add(sam);
                }
                Field.Name fieldName = (Field.Name) listItem.getModelObject();
                Field field = fields.get(fieldName);
                listItem.add(new Label("label", field.getLabel()));
                listItem.add(new Label("value", item.getCustomValue(fieldName)));
            }            
        });
        
        final List<Field> editable = item.getSpace().getMetadata().getEditableFields();
        add(new ListView("labels", editable) {
            protected void populateItem(ListItem listItem) {
                Field field = (Field) listItem.getModelObject();
                listItem.add(new Label("label", field.getLabel()));
            }            
        });
      
        if (item.getHistory() != null) {
            List<History> history = new ArrayList(item.getHistory());
            add(new ListView("history", history) {
                protected void populateItem(ListItem listItem) {
                    if(listItem.getIndex() % 2 != 0) {
                        listItem.add(sam);
                    }                    
                    final History h = (History) listItem.getModelObject();
                    listItem.add(new Label("loggedBy", new PropertyModel(h, "loggedBy.name")));
                    listItem.add(new Label("status", new PropertyModel(h, "statusValue")));
                    listItem.add(new Label("assignedTo", new PropertyModel(h, "assignedTo.name")));
                    
                    WebMarkupContainer comment = new WebMarkupContainer("comment");                    
                    comment.add(new AttachmentLinkPanel("attachment", h.getAttachment()));
                    comment.add(new Label("comment", ItemUtils.fixWhiteSpace(h.getComment())).setEscapeModelStrings(false));
                    listItem.add(comment);
                    
                    listItem.add(new Label("timeStamp", DateUtils.formatTimeStamp(h.getTimeStamp())));
                    listItem.add(new ListView("fields", editable) {
                        protected void populateItem(ListItem listItem) {
                            Field field = (Field) listItem.getModelObject();
                            listItem.add(new Label("field", h.getCustomValue(field.getName())));
                        }                        
                    });
                }                
            });            
        }
        
    }
    
}
