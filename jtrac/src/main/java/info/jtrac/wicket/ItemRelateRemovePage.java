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
import info.jtrac.domain.ItemSearch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextArea;
import wicket.model.BoundCompoundPropertyModel;

/**
 * small form only to confirm and capture comment when removing relationship
 * between items
 */
public class ItemRelateRemovePage extends BasePage {
        
    private long itemId;
    private ItemItem itemItem;
    
    public ItemRelateRemovePage(long itemId, ItemItem itemItem) {                       
       this.itemId = itemId;
       this.itemItem = itemItem;
       add(new ConfirmForm("form"));
    }
    
    private class ConfirmForm extends Form {
                
        private String comment;                
        
        public ConfirmForm(String id) {
            super(id);            
            setModel(new BoundCompoundPropertyModel(this));
            TextArea comment = new TextArea("comment");
            comment.setRequired(true);
            comment.add(new ErrorHighlighter());
            add(comment);
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
