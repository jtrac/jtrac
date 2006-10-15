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

package info.jtrac.webflow;

import info.jtrac.domain.Attachment;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.util.AttachmentUtils;
import info.jtrac.util.ItemUserEditor;
import info.jtrac.util.UserEditor;
import info.jtrac.util.ValidationUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;
import org.springframework.webflow.context.servlet.ServletExternalContext;

/**
 * Multiaction that backs the "Item Create / Edit" flow
 */
public class ItemFormAction extends AbstractFormAction {
    
    public ItemFormAction() {
        setFormObjectClass(Item.class);
        setFormObjectName("item");
        setFormObjectScope(ScopeType.REQUEST);
    }
    
    @Override
    protected void initBinder(RequestContext request, DataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(User.class, new UserEditor(jtrac));
        binder.registerCustomEditor(ItemUser.class, new ItemUserEditor(jtrac));
    }    
    
    @Override
    public Object loadFormObject(RequestContext context) {
        String itemId = ValidationUtils.getParameter(context, "itemId");
        Item item = null;
        Space space = null;
        if (itemId != null) {
            item = jtrac.loadItem(Long.parseLong(itemId));
            space = item.getSpace();
        } else {
            item = new Item();
            String spaceId = ValidationUtils.getParameter(context, "spaceId");
            if (spaceId == null) {
                space = (Space) context.getFlowScope().get("space");
            } else {
                space = jtrac.loadSpace(Integer.parseInt(spaceId));
            }
        }
        context.getFlowScope().put("space", space);
        List<User> users = jtrac.findUsersForSpace(space.getId());
        context.getFlowScope().put("users", users);
        return item;
    }
    
    public Event itemFormHandler(RequestContext context) throws Exception {
        Item item = (Item) getFormObject(context);
        Errors errors = getFormErrors(context);
        Space space = null;
        boolean isEdit = false;
        if (item.getId() == 0) {
            item.setStatus(State.OPEN);
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        
            item.setLoggedBy(user);
            space = (Space) context.getFlowScope().get("space");
            item.setSpace(space);
            ValidationUtils.rejectIfEmpty(errors, "assignedTo");
        } else { // edit scenario
            space = item.getSpace();
            isEdit = true;
        }       

        // validation
        ValidationUtils.rejectIfEmpty(errors, "summary", "detail");
        for (Field field : space.getMetadata().getFields().values()) {
            Object o = item.getValue(field.getName());
            if (o == null && !field.isOptional()) {
                errors.rejectValue(field.getName() + "", ValidationUtils.ERROR_EMPTY_CODE, ValidationUtils.ERROR_EMPTY_MSG);
            }
        }
        
        // TODO clean this mess up with proper form backing object
        String comment = ValidationUtils.getParameter(context, "comment");
        if (errors.hasErrors() || (isEdit && comment == null)) {
            context.getRequestScope().put("comment", comment);
            if ((isEdit && comment == null)) {
                context.getRequestScope().put("commentError", ValidationUtils.ERROR_EMPTY_MSG);
            }
            return error();
        }
        
        ServletExternalContext servletContext = (ServletExternalContext) context.getLastEvent().getSource();
        MultipartHttpServletRequest request = (MultipartHttpServletRequest) servletContext.getRequest();
        MultipartFile multipartFile = request.getFile("file");
        Attachment attachment = null;
        if (!isEdit && !multipartFile.isEmpty()) {
            String fileName = AttachmentUtils.cleanFileName(multipartFile.getOriginalFilename());
            attachment = new Attachment();
            attachment.setFileName(fileName);
        }
        
        if (isEdit) {            
            History history = new History(item);
            history.setComment(comment);
            jtrac.storeHistoryForItem(item, history, attachment);
        } else {
            jtrac.storeItem(item, attachment);
        }
        
        if (attachment != null) {
            File file = new File(System.getProperty("jtrac.home") + "/attachments/" + attachment.getFilePrefix() + "_" + attachment.getFileName());
            multipartFile.transferTo(file);
        }
        
        if (isEdit) {
            return new Event(this, "edit");
        }

        return success();
    }     
    
}
