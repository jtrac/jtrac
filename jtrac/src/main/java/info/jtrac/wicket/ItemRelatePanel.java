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
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.BoundCompoundPropertyModel;

/**
 * header that appears only witin relate items use case
 * containing modal window link
 */
public class ItemRelatePanel extends BasePanel {
    
    private String refId;    
    
    public ItemRelatePanel(String id, boolean isItemViewPage) {                
        super(id);
        ItemSearch itemSearch = getCurrentItemSearch();
        refId = itemSearch == null ? null : itemSearch.getRelatingItemRefId();
        if (refId != null) {
            final YuiDialog dialog = new YuiDialog("itemWindow");
            add(dialog);                                                        
            AjaxLink link = new AjaxLink("link") {
                public void onClick(AjaxRequestTarget target) {
                    Item item = getJtrac().loadItemByRefId(refId);                    
                    dialog.show(target, refId, new ItemViewPanel(YuiDialog.CONTENT_ID, item, true));
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
                    setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getRefId()));
                }
            });
        } else {
            setVisible(false);
        }        
    }
    
    /**
     * wicket form
     */
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
            TextArea commentArea = new TextArea("comment");
            commentArea.setRequired(true);
            commentArea.add(new ErrorHighlighter());
            add(commentArea);
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
            setCurrentItemSearch(null);
            setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getRefId()));
        }          
        
        
    }
    
}
