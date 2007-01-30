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
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.ExternalLink;

/**
 * link for downloading an attachment
 */
public class AttachmentLinkPanel extends BasePanel {    
    
    public AttachmentLinkPanel(String id, Attachment attachment) {        
        
        super(id);
        
        if(attachment == null) {
            add(new Label("attachment", ""));
            setVisible(false);            
            return;
        }        
        
        CharSequence fileName = getResponse().encodeURL(attachment.getFileName());
        String href = "app/attachments/" + fileName +"?filePrefix=" + attachment.getFilePrefix();                        
        add(new ExternalLink("attachment", href, attachment.getFileName()));  
        
    }
    
}
