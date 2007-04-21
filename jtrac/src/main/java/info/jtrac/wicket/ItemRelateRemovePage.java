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
import info.jtrac.domain.ItemItem;
import info.jtrac.wicket.yui.YuiDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.BoundCompoundPropertyModel;

/**
 * small form only to confirm and capture comment when removing relationship
 * between items
 */
public class ItemRelateRemovePage extends BasePage {
        
    private long itemId;
    private ItemItem itemItem;
    
    public ItemRelateRemovePage(long itemId, final ItemItem itemItem) {
        this.itemId = itemId;
        this.itemItem = itemItem;
        add(new ConfirmForm("form"));
        final String relatingRefId = itemItem.getItem().getRefId();
        final String relatedRefId = itemItem.getRelatedItem().getRefId();
        final YuiDialog relatingDialog = new YuiDialog("relatingDialog");
        final YuiDialog relatedDialog = new YuiDialog("relatedDialog");
        add(relatingDialog);
        add(relatedDialog);
        AjaxLink relating = new AjaxLink("relating") {
            public void onClick(AjaxRequestTarget target) {
                Item relating = getJtrac().loadItem(itemItem.getItem().getId());
                relatingDialog.show(target, relatingRefId, new ItemViewPanel(YuiDialog.CONTENT_ID, relating, true));                
            }
        };
        relating.add(new Label("refId", relatingRefId));
        add(relating);
        
        // TODO refactor, duplicate code in ItemViewPanel
        String message = null;
        if(itemItem.getType() == DUPLICATE_OF) {
            message = localize("item_view.duplicateOf");
        } else if (itemItem.getType() == DEPENDS_ON) {
            message = localize("item_view.dependsOn");
        } else if (itemItem.getType() == RELATED){
            message = localize("item_view.relatedTo");                  
        }
        add(new Label("message", message));
        
        AjaxLink related = new AjaxLink("related") {
            public void onClick(AjaxRequestTarget target) {
                Item related = getJtrac().loadItem(itemItem.getRelatedItem().getId());
                relatedDialog.show(target, relatedRefId, new ItemViewPanel(YuiDialog.CONTENT_ID, related, true));
            }
        };
        related.add(new Label("refId", itemItem.getRelatedItem().getRefId()));
        add(related);        
        
    }
    
    /**
     * wicket form
     */    
    private class ConfirmForm extends Form {
                
        private String comment;                
        
        public ConfirmForm(String id) {
            super(id);            
            setModel(new BoundCompoundPropertyModel(this));
            TextArea commentArea = new TextArea("comment");
            commentArea.setRequired(true);
            commentArea.add(new ErrorHighlighter());
            add(commentArea);
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
             
        @Override
        protected void onSubmit() {
            getJtrac().removeItemItem(itemItem);
            Item item = getJtrac().loadItem(itemId);                                    
            item.setEditReason(comment);
            getJtrac().updateItem(item, getPrincipal());
            setResponsePage(new ItemViewPage(item, null));
        }          
        
        
    }
    
}
