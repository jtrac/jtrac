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

import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.UserUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import wicket.extensions.ajax.markup.html.WicketAjaxIndicatorAppender;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.form.validation.AbstractValidator;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;
import wicket.model.Model;

/**
 * Form to update history for item
 */
public class ItemViewFormPanel extends BasePanel {
    
    private JtracFeedbackMessageFilter filter;
    private ItemSearch itemSearch;
    
    public ItemViewFormPanel(String id, Item item, ItemSearch itemSearch) {
        super(id);
        this.itemSearch = itemSearch;
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        filter = new JtracFeedbackMessageFilter();
        feedback.setFilter(filter);
        add(feedback);        
        add(new ItemViewForm("form", item));
    }
    
    private class ItemViewForm extends Form {
        
        private FileUploadField fileUploadField;
        private long itemId;
        private DropDownChoice assignedToChoice;
        private DropDownChoice statusChoice;
        
        public ItemViewForm(String id, final Item item) {
            super(id);
            setMultiPart(true);
            this.itemId = item.getId();
            History history = new History();
            history.setItemUsers(item.getItemUsers());
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(history);
            setModel(model);
            add(new TextArea("comment").setRequired(true).add(new ErrorHighlighter()));
            // custom fields ===================================================
            User user = ((JtracSession) getSession()).getUser();
            List<Field> fields = item.getEditableFieldList(user);                   
            add(new CustomFieldsFormPanel("fields", model, fields));
            // =================================================================
            final Space space = item.getSpace();
            final List<UserSpaceRole> userSpaceRoles = getJtrac().findUserRolesForSpace(space.getId());
            // status ==========================================================
            final Map<Integer, String> statesMap = item.getPermittedTransitions(user);
            List<Integer> states = new ArrayList(statesMap.keySet());                                                   
            statusChoice = new IndicatingDropDownChoice("status", states, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return statesMap.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }                
            });            
            statusChoice.setNullValid(true);
            statusChoice.add(new ErrorHighlighter());            
            statusChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                protected void onUpdate(AjaxRequestTarget target) {
                    Integer selectedStatus = (Integer) getFormComponent().getConvertedInput();
                    if (selectedStatus == null) {
                        assignedToChoice.setEnabled(false);
                    } else {
                        List<User> assignable = UserUtils.filterUsersAbleToTransitionFrom(userSpaceRoles, space, selectedStatus);
                        assignedToChoice.setChoices(assignable);                    
                        assignedToChoice.setEnabled(true);
                    }
                    target.addComponent(assignedToChoice);
                }
            });            
            add(statusChoice);
            // assigned to =====================================================            
            List<User> empty = new ArrayList<User>(0);
            assignedToChoice = new DropDownChoice("assignedTo", empty, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((User) o).getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((User) o).getId() + "";
                }                
            });
            assignedToChoice.setNullValid(true);            
            assignedToChoice.add(new ErrorHighlighter());
            assignedToChoice.setOutputMarkupId(true);
            assignedToChoice.setEnabled(false);
            assignedToChoice.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    // validation: assignedTo cannot be null if status is not null
                    // unless the status is CLOSED
                    if(c.getConvertedInput() == null) {
                        Integer i = (Integer) statusChoice.getConvertedInput();
                        if (i != null && i != State.CLOSED) {
                            error(c);
                        }
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "item_view_form.assignedTo.error";
                }
            });
            assignedToChoice.setLabel(new Model(space.getMetadata().getStatusValue(State.CLOSED)));
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
            History history = (History) getModelObject();                                  
            User user = ((JtracSession) getSession()).getUser();
            history.setLoggedBy(user);            
            getJtrac().storeHistoryForItem(itemId, history, fileUpload);            
            setResponsePage(new ItemViewPage(history.getParent(), itemSearch));
        }
        
    }
    
    private final class IndicatingDropDownChoice extends DropDownChoice implements wicket.ajax.IAjaxIndicatorAware {
        
        private final WicketAjaxIndicatorAppender indicatorAppender = new WicketAjaxIndicatorAppender();

        public IndicatingDropDownChoice(String id, List list, IChoiceRenderer cr){
            super(id, list, cr);
            add(indicatorAppender);
        }
        
        public java.lang.String getAjaxIndicatorMarkupId(){
            return indicatorAppender.getMarkupId();
        }

    }    
    
}
