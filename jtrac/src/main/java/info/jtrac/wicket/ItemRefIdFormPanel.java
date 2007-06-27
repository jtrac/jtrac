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
import info.jtrac.wicket.yui.YuiPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 * view by ref id form panel
 * the submit is done over ajax and validation errors also
 * are shown using ajax
 */
public class ItemRefIdFormPanel extends BasePanel {
        
    private TextField refIdField;   
    private YuiPanel yuiPanel;

    // TODO nice frameworky way to do this
    public void setYuiPanel(YuiPanel yuiPanel) {
        this.yuiPanel = yuiPanel;
    }
    
    public String getFocusScript() {
        return "document.getElementById('" + refIdField.getMarkupId() + "').focus();";
    }    
    
    public ItemRefIdFormPanel(String id) {
        super(id);        
        add(new ItemRefIdForm());        
    }
    
    /**
     * wicket form
     */    
    private class ItemRefIdForm extends Form {
        
        private String refId;

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }        
        
        public ItemRefIdForm() {
            super("form");            
            final FeedbackPanel feedback;
            add(feedback = new FeedbackPanel("feedback"));
            feedback.setOutputMarkupId(true);
            refIdField = new TextField("refId", new PropertyModel(this, "refId"));
            refIdField.setOutputMarkupId(true);
            refIdField.add(new ErrorHighlighter());
            add(refIdField);
            add(new AjaxSubmitButton("submit", this) {
                @Override
                protected void onError(AjaxRequestTarget target, Form form) {
                    target.addComponent(feedback);
                    // hack for IE, flip visibility will re-size panel to accomodate area size change
                    target.appendJavascript(yuiPanel.getHideScript() + yuiPanel.getShowScript() + getFocusScript());
                }
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    target.addComponent(feedback);
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
                    ItemSearch itemSearch = getCurrentItemSearch();
                    if(itemSearch == null || itemSearch.getRelatingItemRefId() == null) {
                        setCurrentItemSearch(null); // disable back link for item view
                    }
                    setResponsePage(ItemViewPage.class, new PageParameters("0=" + refId));  
                }
            });              
        }        
        
    }
    
}
