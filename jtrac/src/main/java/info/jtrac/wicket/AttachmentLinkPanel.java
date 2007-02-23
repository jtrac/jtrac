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
import info.jtrac.util.AttachmentUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import wicket.IRequestTarget;
import wicket.RequestCycle;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.protocol.http.WebResponse;
import wicket.util.io.Streams;

/**
 * link for downloading an attachment
 */
public class AttachmentLinkPanel extends BasePanel {
    
    public AttachmentLinkPanel(String id, final Attachment attachment) {
        
        super(id);
        
        if(attachment == null) {
            add(new Label("attachment", ""));
            setVisible(false);
            return;
        }
        
        final String fileName = getResponse().encodeURL(attachment.getFileName()).toString();
        
        Link link = new Link("attachment") {
            // adapted from wicket.markup.html.link.DownloadLink
            public void onClick() {
                getRequestCycle().setRequestTarget(new IRequestTarget() {
                    
                    public void detach(RequestCycle requestCycle) {
                    }
                    
                    public void respond(RequestCycle requestCycle) {
                        WebResponse r = (WebResponse) requestCycle.getResponse();
                        r.setAttachmentHeader(fileName);
                        try {
                            File file = AttachmentUtils.getFile(attachment);
                            InputStream is = new FileInputStream(file);
                            try {
                                Streams.copy(is, r.getOutputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } finally {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        
        link.add(new Label("fileName", fileName));
        add(link);
        
    }
    
}
