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

import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.wicket.yui.YuiDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.link.Link;
import wicket.model.BoundCompoundPropertyModel;

/**
 * header that appears only witin relate items use case
 * containing modal window link
 */
public class ItemRelatePanel extends BasePanel {
    
    private String refId;    
    
    public ItemRelatePanel(String id, ItemSearch itemSearch, boolean isItemViewPage) {        
        super(id);
        refId = itemSearch == null ? null : itemSearch.getRelatingItemRefId();
        if (refId != null) {
            final YuiDialog dialog = new YuiDialog("itemWindow", refId);
            add(dialog);                                                        
            AjaxLink link = new AjaxLink("link") {
                public void onClick(AjaxRequestTarget target) {
                    Item item = getJtrac().loadItemByRefId(refId);                    
                    dialog.show(target, new ItemViewPanel(YuiDialog.CONTENT_ID, item, true));
                }
            };
            link.add(new Label("refId", refId));             
            if(isItemViewPage) {
                add(new WebMarkupContainer("link").setVisible(false));
                add(new WebMarkupContainer("message").setVisible(false));
                add(new RelateForm("form").add(link));
            } else {
                add(new Label("message", localize("item_list.searchingForRelated")));
                add(link);
                add(new WebMarkupContainer("form").setVisible(false));
            }           
            add(new Link("cancel") {
                public void onClick() {
                    Item item = getJtrac().loadItemByRefId(refId);
                    setResponsePage(new ItemViewPage(item, null));
                }
            });
        } else {
            setVisible(false);
        }        
    }
    
    private class RelateForm extends Form {
        
        private int type;
        private String comment;                
        
        public RelateForm(String id) {
            super(id);            
            setModel(new BoundCompoundPropertyModel(this));
            final Map<Integer, String> options = new HashMap<Integer, String>(3);
            options.put(DUPLICATE_OF, localize("item_view_form.duplicateOf"));
            options.put(DEPENDS_ON, localize("item_view_form.dependsOn"));
            options.put(RELATED, localize("item_view_form.relatedTo"));
            DropDownChoice choice = new DropDownChoice("type", new ArrayList(options.keySet()), new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return options.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }
            });
            add(choice);
            TextArea comment = new TextArea("comment");
            comment.setRequired(true);
            comment.add(new ErrorHighlighter());
            add(comment);
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
             
        @Override
        protected void onSubmit() {            
            Item item = getJtrac().loadItemByRefId(refId);
            long itemId = ((ItemViewPage) getPage()).getItemId();
            Item relatedItem = getJtrac().loadItem(itemId);
            item.addRelated(relatedItem, type);
            item.setEditReason(comment);
            getJtrac().updateItem(item, getPrincipal());
            setResponsePage(new ItemViewPage(item, null));
        }          
        
        
    }
    
}
