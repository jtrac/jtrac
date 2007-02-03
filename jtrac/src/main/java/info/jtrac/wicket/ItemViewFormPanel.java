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
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.AttachmentUtils;
import info.jtrac.util.SecurityUtils;
import info.jtrac.util.UserUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wicket.feedback.FeedbackMessage;
import wicket.feedback.IFeedbackMessageFilter;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * Form to update history for item
 */
public class ItemViewFormPanel extends BasePanel {
    
    private MyFilter filter;
    private ItemListPage previous;
    
    private class MyFilter implements IFeedbackMessageFilter {
        
        private Set<String> previous = new HashSet<String>();
        
        public void reset() {
            previous.clear();
        }
        
        public boolean accept(FeedbackMessage fm) {
            if(!previous.contains(fm.getMessage())) {
                previous.add(fm.getMessage());
                return true;
            }
            return false;
        }
    }    
    
    public ItemViewFormPanel(String id, Item item, ItemListPage previous) {
        super(id);
        this.previous = previous;
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        filter = new MyFilter();
        feedback.setFilter(filter);
        add(feedback);        
        add(new ItemViewForm("form", item));
    }
    
    private class ItemViewForm extends Form {
        
        private FileUploadField fileUploadField;
        private long itemId;
        
        public ItemViewForm(String id, Item item) {
            super(id);
            setMultiPart(true);
            this.itemId = item.getId();
            History history = new History();
            history.setItemUsers(item.getItemUsers());
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(history);
            setModel(model);
            add(new TextArea("comment").setRequired(true).add(new ErrorHighlighter()));
            // custom fields ===================================================
            User user = SecurityUtils.getPrincipal();
            List<Field> fields = item.getEditableFieldList(user);                   
            add(new CustomFieldsFormPanel("fields", model, fields));
            // status ==========================================================
            final Map<Integer, String> statesMap = item.getPermittedTransitions(user);
            List<Integer> states = new ArrayList(statesMap.keySet());            
            DropDownChoice statusChoice = new DropDownChoice("status", states, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return statesMap.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }                
            });            
            statusChoice.setNullValid(true);
            statusChoice.add(new ErrorHighlighter());
            add(statusChoice);
            // assigned to =====================================================
            Space space = item.getSpace();
            List<UserSpaceRole> userSpaceRoles = getJtrac().findUserRolesForSpace(space.getId());
            List<User> assignable = UserUtils.filterUsersAbleToTransitionFrom(userSpaceRoles, space, item.getStatus());
            DropDownChoice assignedToChoice = new DropDownChoice("assignedTo", assignable, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((User) o).getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((User) o).getId() + "";
                }                
            });
            assignedToChoice.setNullValid(true);            
            assignedToChoice.add(new ErrorHighlighter());            
            add(assignedToChoice);
            // notify list =====================================================
            List<ItemUser> choices = UserUtils.convertToItemUserList(userSpaceRoles);
            ListMultipleChoice itemUsers = new JtracCheckBoxMultipleChoice("itemUsers", choices, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((ItemUser) o).getUser().getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((ItemUser) o).getUser().getId() + "";
                }               
            });
            add(itemUsers);
            // attachment ======================================================
            fileUploadField = new FileUploadField("file");
            // TODO file size limit
            add(fileUploadField);
            // send notifications===============================================
            add(new CheckBox("sendNotifications"));
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
            History history = (History) getModelObject();
            User user = SecurityUtils.getPrincipal();
            history.setLoggedBy(user);
            Item item = getJtrac().loadItem(itemId);
            getJtrac().storeHistoryForItem(item, history, attachment);
            
            if (attachment != null) {
                File file = new File(System.getProperty("jtrac.home") + "/attachments/" + attachment.getFilePrefix() + "_" + attachment.getFileName());
                try {
                    fileUpload.writeTo(file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }            
            setResponsePage(new ItemViewPage(item, ItemViewFormPanel.this.previous));
        }
        
    }
    
}
