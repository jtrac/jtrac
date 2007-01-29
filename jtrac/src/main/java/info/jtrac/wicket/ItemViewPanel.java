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

import info.jtrac.domain.Attachment;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.basic.MultiLineLabel;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * panel for showing the item read-only view
 */
public class ItemViewPanel extends BasePanel {    
    
    public ItemViewPanel(String id, final Item tempItem) {
        
        super(id);        
        
        final Item item = getJtrac().loadItem(tempItem.getId());
        
        add(new Label("refId", new PropertyModel(item, "refId")));
        add(new Label("status", new PropertyModel(item, "statusValue")));
        add(new Label("loggedBy", new PropertyModel(item, "loggedBy.name")));
        add(new Label("assignedTo", new PropertyModel(item, "assignedTo.name")));
        add(new Label("summary", new PropertyModel(item, "summary")));
        add(new MultiLineLabel("detail", new PropertyModel(item, "detail")));
        
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
                    final History h = (History) listItem.getModelObject();
                    listItem.add(new Label("loggedBy", new PropertyModel(h, "loggedBy.name")));
                    listItem.add(new Label("status", new PropertyModel(h, "statusValue")));
                    listItem.add(new Label("assignedTo", new PropertyModel(h, "assignedTo.name")));
                    
                    WebMarkupContainer comment = new WebMarkupContainer("comment");                    
                    final Attachment attachment = h.getAttachment();
                    if (attachment != null) {
                        CharSequence fileName = getResponse().encodeURL(attachment.getFileName());
                        String href = "app/attachments/" + fileName +"?filePrefix=" + attachment.getFilePrefix();                        
                        comment.add(new ExternalLink("attachment", href, attachment.getFileName()));                      
                    } else {
                        comment.add(new Label("attachment", "").setVisible(false));
                    }
                    comment.add(new MultiLineLabel("comment", new PropertyModel(h, "comment")));
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
