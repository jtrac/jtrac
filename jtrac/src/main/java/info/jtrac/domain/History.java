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
public class History extends AbstractItem {
    
    private String comment;
    private Double actualEffort;
    private Attachment attachment;

    public History() {
        // zero arg constructor
    }
    
    public History(Item item) {
        setSummary(item.getSummary());
        setDetail(item.getDetail());
        setLoggedBy(item.getLoggedBy());
        setAssignedTo(item.getAssignedTo());
        // setTimeStamp(item.getTimeStamp());
        setPlannedEffort(item.getPlannedEffort());
        //==========================
        setStatus(item.getStatus());
        setSeverity(item.getSeverity());
        setPriority(item.getPriority());
        setCusInt01(item.getCusInt01());
        setCusInt02(item.getCusInt02());
        setCusInt03(item.getCusInt03());
        setCusInt04(item.getCusInt04());
        setCusInt05(item.getCusInt05());
        setCusInt06(item.getCusInt06());
        setCusInt07(item.getCusInt07());
        setCusInt08(item.getCusInt08());
        setCusInt09(item.getCusInt09());
        setCusInt10(item.getCusInt10());
        setCusDbl01(item.getCusDbl01());
        setCusDbl02(item.getCusDbl02());
        setCusDbl03(item.getCusDbl03());
        setCusStr01(item.getCusStr01());
        setCusStr02(item.getCusStr02());
        setCusStr03(item.getCusStr03());
        setCusStr04(item.getCusStr04());
        setCusStr05(item.getCusStr05());
        setCusTim01(item.getCusTim01());
        setCusTim02(item.getCusTim02());
        setCusTim03(item.getCusTim03());
    }
    
    @Override
    public Space getSpace() {
        return getParent().getSpace();
    }            
    
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
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("; comment [").append(comment);
        sb.append("]; actualEffort [").append(actualEffort);
        sb.append("]; attachment [").append(attachment);
        sb.append("]");
        return sb.toString();
    }
    
}
