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

package info.jtrac.domain;

/**
 * Any updates to an Item (even a new insert) causes a snapshot of
 * the item to be stored in the History table.
 * In this way for each Item, a History view is available which
 * shows the diffs, who made changes and when, etc.
 */
public class History extends Item {
    
    private String comment;
    private Double actualEffort;
    private Attachment attachment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Double getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Double actualEffort) {
        this.actualEffort = actualEffort;
    }
    
}
