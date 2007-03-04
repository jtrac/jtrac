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
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.AttachmentUtils;
import info.jtrac.util.UserUtils;
import java.io.File;
import java.util.List;
import wicket.Component;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;
import wicket.model.LoadableDetachableModel;

/**
 * Create / Edit item form page
 */
public class ItemFormPage extends BasePage {                           
    
    ItemViewPage previous;
            
    public ItemFormPage(Space space) {       
        Item item = new Item();
        item.setSpace(space);
        add(new ItemForm("form", item));
    }
    
    public ItemFormPage(Item item, ItemViewPage previous) {
        this.previous = previous;        
        add(new ItemForm("form", item));        
    }   
    
    private class ItemForm extends Form {
        
        private JtracFeedbackMessageFilter filter;
        private FileUploadField fileUploadField = new FileUploadField("file");
        private boolean editMode;
        private int version;
        
        public ItemForm(String id, final Item temp) {
            super(id);
            setMultiPart(true);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);
            // this ensures that the model object is re-loaded as part of the
            // form submission workflow before form binding and avoids
            // hibernate lazy loading issues during the whole update transaction
            LoadableDetachableModel itemModel = new LoadableDetachableModel() {
                protected Object load() {
                    if(temp.getId() > 0) {
                        return getJtrac().loadItem(temp.getId());
                    } else {
                        return temp;
                    }
                }
            };            
            BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(itemModel);
            setModel(model);
            Item item = (Item) itemModel.getObject(null);
            if(item.getId() > 0) {
                editMode = true;
                version = item.getVersion();
            }
            // summary =========================================================
            final TextField summaryField = new TextField("summary");
            summaryField.setRequired(true);
            summaryField.add(new ErrorHighlighter());
            summaryField.setOutputMarkupId(true);
            add(summaryField);
            ItemFormPage.this.getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                public Object getObject(Component c) {
                    return "document.getElementById('" + summaryField.getMarkupId() + "').focus()";
                }
            }, summaryField);
            // detail ==========================================================
            add(new TextArea("detail").setRequired(true).add(new ErrorHighlighter()));
            // custom fields ===================================================
            List<Field> fields = item.getSpace().getMetadata().getFieldList();            
            add(new CustomFieldsFormPanel("fields", model, fields));
            // hide some components if editing item
            WebMarkupContainer hideAssignedTo = new WebMarkupContainer("hideAssignedTo");
            WebMarkupContainer hideNotifyList = new WebMarkupContainer("hideNotifyList");
            WebMarkupContainer hideEditReason = new WebMarkupContainer("hideEditReason");
            add(hideAssignedTo);
            add(hideNotifyList);
            add(hideEditReason);
            if(item.getId() > 0) {
                hideAssignedTo.setVisible(false);
                hideNotifyList.setVisible(false);
                hideEditReason.add(new TextArea("editReason").setRequired(true).add(new ErrorHighlighter()));
            } else {
                hideEditReason.setVisible(false);
                // assigned to =================================================
                Space space = item.getSpace();
                List<UserSpaceRole> userSpaceRoles = getJtrac().findUserRolesForSpace(space.getId());
                List<User> assignable = UserUtils.filterUsersAbleToTransitionFrom(userSpaceRoles, space, State.OPEN);
                DropDownChoice choice = new DropDownChoice("assignedTo", assignable, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return ((User) o).getName();
                    }
                    public String getIdValue(Object o, int i) {
                        return ((User) o).getId() + "";
                    }                
                });
                choice.setNullValid(true);
                choice.setRequired(true);
                choice.add(new ErrorHighlighter());            
                hideAssignedTo.add(choice);
                // notify list =================================================
                List<ItemUser> choices = UserUtils.convertToItemUserList(userSpaceRoles);
                ListMultipleChoice itemUsers = new JtracCheckBoxMultipleChoice("itemUsers", choices, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return ((ItemUser) o).getUser().getName();
                    }
                    public String getIdValue(Object o, int i) {
                        return ((ItemUser) o).getUser().getId() + "";
                    }               
                });
                hideNotifyList.add(itemUsers);
                // attachment ==================================================                
                // TODO file size limit
                hideNotifyList.add(fileUploadField);                
            }
            // send notifications ==========================================
            add(new CheckBox("sendNotifications"));
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(previous);
                }                
            }.setVisible(previous != null));            
        }
        
        @Override
        protected void validate() {
            filter.reset();            
            super.validate();
        }        
        
        @Override
        protected void onSubmit() {
            final FileUpload fileUpload = fileUploadField.getFileUpload();
            Attachment attachment = null;
            if (fileUpload != null) {
                String fileName = AttachmentUtils.cleanFileName(fileUpload.getClientFileName());
                attachment = new Attachment();
                attachment.setFileName(fileName);
            }
            Item item = (Item) getModelObject();                        
            
            if(!editMode && item.getId() > 0) {
                // user must have used back button after creating new
                error(localize("item_form.error.version"));
                return;
            }           
            
            if(editMode) {
                if(item.getVersion() != version) {
                    // user must have used back button after edit
                    error(localize("item_form.error.version"));
                    return;                    
                }
            }
            
            User user = getPrincipal();            
            if(item.getId() > 0) { // edit mode
                getJtrac().updateItem(item, user);                
            } else {
                item.setLoggedBy(user);
                item.setStatus(State.OPEN);
                getJtrac().storeItem(item, attachment);                 
            }
            if (attachment != null) {
                File file = AttachmentUtils.getFile(attachment);
                try {
                    fileUpload.writeTo(file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            // allow user to navigate back to search results if applicable
            ItemListPage itemListPage = null;
            if(previous != null) {
                itemListPage = previous.getPrevious();
            }            
            setResponsePage(new ItemViewPage(item, itemListPage));
        }
        
    }
    
}
