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

import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.exception.InvalidRefIdException;
import wicket.Component;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.PropertyModel;

/**
 * view by ref id form page
 */
public class ItemRefIdFormPanel extends BasePanel {
    
    private ItemSearch itemSearch;
    
    public ItemRefIdFormPanel(String id, ItemSearch itemSearch) {
        super(id);
        this.itemSearch = itemSearch;
        add(new ItemRefIdForm());        
    }
    
    private class ItemRefIdForm extends Form {
        
        private String refId;
        private TextField refIdField;

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }        
        
        public ItemRefIdForm() {
            super("form");
            add(new FeedbackPanel("feedback"));
            refIdField = new TextField("refId", new PropertyModel(this, "refId")) {
                @Override
                public void onAttach() {
                    super.onAttach();
                    getWebPage().getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                        public Object getObject() {
                            return "document.getElementById('" + getMarkupId() + "').focus()";
                        }
                    }, this);
                }                
            };
            refIdField.setOutputMarkupId(true);
            refIdField.add(new ErrorHighlighter());
            add(refIdField);           
        }
        
        @Override
        public void onSubmit() {                                                       
            if(refId == null) {
                refIdField.error(localize("item_search_form.error.refId.invalid"));                
                return;
            }
            Item item = null;
            try {
                item = getJtrac().loadItemByRefId(refId);
            } catch (InvalidRefIdException e) {                        
                refIdField.error(localize("item_search_form.error.refId.invalid"));                
                return;          
            }        
            if (item == null) {                        
                refIdField.error(localize("item_search_form.error.refId.notFound"));                
                return;       
            } 
            setResponsePage(new ItemViewPage(item, itemSearch));                     
        }
        
    }
    
}
