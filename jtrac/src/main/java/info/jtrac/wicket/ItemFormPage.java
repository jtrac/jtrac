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
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.UserUtils;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Bytes;

/**
 * Create / Edit item form page
 */
public class ItemFormPage extends BasePage {    
            
    public ItemFormPage() {        
        Item item = new Item();
        item.setSpace(getCurrentSpace());
        item.setStatus(State.NEW);        
        add(new ItemForm("form", item));
    }
    
    public ItemFormPage(long itemId) {        
        Item item = getJtrac().loadItem(itemId);      
        add(new ItemForm("form", item));
    }    
    
    /**
     * wicket form
     */    
    private class ItemForm extends Form {
        
        private JtracFeedbackMessageFilter filter;
        private FileUploadField fileUploadField = new FileUploadField("file");
        private boolean editMode;
        private int version;        
        
        public ItemForm(String id, final Item item) {
            super(id);
            setMultiPart(true);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);   
            version = item.getVersion();
            if(item.getId() > 0) {
                editMode = true;                
            }
            BoundCompoundPropertyModel model = null;
            if(editMode) {
                // this ensures that the model object is re-loaded as part of the
                // form submission workflow before form binding and avoids
                // hibernate lazy loading issues during the whole update transaction
                LoadableDetachableModel itemModel = new LoadableDetachableModel() {
                    protected Object load() {                                        
                        logger.debug("attaching existing item " + item.getId());
                        return getJtrac().loadItem(item.getId());
                    }
                };
                model = new BoundCompoundPropertyModel(itemModel);
            } else {
                model = new BoundCompoundPropertyModel(item);
            }
            setModel(model);                        
            // summary =========================================================
            final TextField summaryField = new TextField("summary");
            summaryField.setRequired(true);
            summaryField.add(new ErrorHighlighter());
            summaryField.setOutputMarkupId(true);
            add(summaryField);
            add(new HeaderContributor(new IHeaderContributor() {
                public void renderHead(IHeaderResponse response) {
                    response.renderOnLoadJavascript("document.getElementById('" + summaryField.getMarkupId() + "').focus()");
                }
            }));
            // delete button ===================================================
            Button delete = new Button("delete") {
                @Override
                public void onSubmit() {
                    String heading = localize("item_delete.confirm");
                    String warning = localize("item_delete.line2");
                    String line1 = localize("item_delete.line1");                    
                    ConfirmPage confirm = new ConfirmPage(ItemFormPage.this, heading, warning, new String[] {line1}) {
                        public void onConfirm() {
                            // avoid lazy init problem
                            getJtrac().removeItem(getJtrac().loadItem(item.getId()));
                            ItemSearch itemSearch = JtracSession.get().getItemSearch();
                            if(itemSearch != null) {
                                setResponsePage(new ItemListPage(itemSearch));
                            } else {
                                setResponsePage(DashboardPage.class);
                            }
                        }                        
                    };
                    setResponsePage(confirm);                    
                }
            };
            delete.setDefaultFormProcessing(false);
            add(delete);
            if(!editMode) {
                delete.setVisible(false);
            }
            // detail ==========================================================
            add(new TextArea("detail").setRequired(true).add(new ErrorHighlighter()));
            // custom fields ===================================================
            if(editMode) {
                add(new CustomFieldsFormPanel("fields", model, item.getSpace()).setRenderBodyOnly(true));
            } else {
                add(new CustomFieldsFormPanel("fields", model, item, getPrincipal()).setRenderBodyOnly(true));
            }
            // hide some components if editing item
            WebMarkupContainer hideAssignedTo = new WebMarkupContainer("hideAssignedTo");
            WebMarkupContainer hideNotifyList = new WebMarkupContainer("hideNotifyList");
            WebMarkupContainer hideEditReason = new WebMarkupContainer("hideEditReason");
            add(hideAssignedTo);
            add(hideNotifyList);
            add(hideEditReason);
            if(editMode) {
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
                WebMarkupContainer border = new WebMarkupContainer("border");
                border.add(choice);
                border.add(new ErrorHighlighter(choice));
                hideAssignedTo.add(border);
                // notify list =================================================
                List<ItemUser> choices = UserUtils.convertToItemUserList(userSpaceRoles);
                ListMultipleChoice itemUsers = new JtracCheckBoxMultipleChoice("itemUsers", choices, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return ((ItemUser) o).getUser().getName();
                    }
                    public String getIdValue(Object o, int i) {
                        return ((ItemUser) o).getUser().getId() + "";
                    }               
                }, true);
                hideNotifyList.add(itemUsers);
                // attachment ==================================================                                                
                hideNotifyList.add(fileUploadField);
                setMaxSize(Bytes.megabytes(getJtrac().getAttachmentMaxSizeInMb()));
            }
            // send notifications ==========================================
            add(new CheckBox("sendNotifications"));
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getRefId()));
                }                
            }.setVisible(editMode && JtracSession.get().getItemSearch() != null));            
        }
        
        @Override
        protected void validate() {
            filter.reset();     
            Item item = (Item) getModelObject();                      
            if(editMode && item.getVersion() != version) {                                
                // user must have used back button after edit
                error(localize("item_form.error.version"));                                                    
            } 
            super.validate();    
        }        
        
        @Override
        protected void onSubmit() {
            final FileUpload fileUpload = fileUploadField.getFileUpload();
            Item item = (Item) getModelObject();                                                
            User user = getPrincipal();            
            if(editMode) {
                getJtrac().updateItem(item, user);                
            } else {
                item.setLoggedBy(user);
                item.setStatus(State.OPEN);                
                getJtrac().storeItem(item, fileUpload);                
            }                   
            // on creating an item, clear any search filter (especially the related item) from session
            JtracSession.get().setItemSearch(null);
            setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getRefId()));
        }
        
    }
    
}
