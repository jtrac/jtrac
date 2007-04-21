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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

/**
 * This object represents a generic item which can be an issue, defect, task etc.
 * some logic for field accessors and conversion of keys to display values 
 * is contained in the AbstractItem class
 */
public class Item extends AbstractItem {

    private Integer type;
    private Space space;
    private long sequenceNum;
    
    private Set<History> history;
    private Set<Item> children;
    private Set<Attachment> attachments;
    
    // should be ideally in form backing object but for convenience
    private String editReason;

    @Override
    public String getRefId() {
        return getSpace().getPrefixCode() + "-" + sequenceNum;
    }    
    
    public Map<Integer, String> getPermittedTransitions(User user) {
        return user.getPermittedTransitions(space, getStatus());        
    }
    
    public List<Field> getEditableFieldList(User user) {
        return user.getEditableFieldList(space, getStatus());
    }
    
    public void add(History h) {
        if (this.history == null) {
            this.history = new LinkedHashSet<History>();
        }
        h.setParent(this);
        this.history.add(h);
    }
    
    public void add(Attachment attachment) {
        if (attachments == null) {
            attachments = new LinkedHashSet<Attachment>();
        }
        attachments.add(attachment);
    }
    
    public void addRelated(Item relatedItem, int relationType) {
        if (getRelatedItems() == null) {
            setRelatedItems(new LinkedHashSet<ItemItem>());
        }
        ItemItem itemItem = new ItemItem(this, relatedItem, relationType);        
        getRelatedItems().add(itemItem);
    }    
    
    /**
     * Lucene DocumentCreator implementation
     */
    public Document createDocument() {
        Document d = new Document();        
        d.add(new org.apache.lucene.document.Field("id", getId() + "", Store.YES, Index.NO));            
        d.add(new org.apache.lucene.document.Field("type", "item", Store.YES, Index.NO));        
        StringBuffer sb = new StringBuffer();
        if (getSummary() != null) {
            sb.append(getSummary());
        }        
        if (getDetail() != null) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(getDetail());
        }
        d.add(new org.apache.lucene.document.Field("text", sb.toString(), Store.NO, Index.TOKENIZED));
        return d;
    }    
    
    public History getLatestHistory() {
        if (history == null) {
            return null;
        }
        History out = null;
        for(History h : history) {
            out = h;
        }
        return out;
    }       
    
    //===========================================================
    
    @Override
    public Space getSpace() {
        return space;
    }    
    
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setSpace(Space space) {
        this.space = space;
    }    
    
    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }     
    
    public Set<History> getHistory() {
        return history;
    }

    public void setHistory(Set<History> history) {
        this.history = history;
    }

    public Set<Item> getChildren() {
        return children;
    }

    public void setChildren(Set<Item> children) {
        this.children = children;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }      

    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }   
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("; type [").append(type);
        sb.append("]; space [").append(space);
        sb.append("]; sequenceNum [").append(sequenceNum);
        sb.append("]");
        return sb.toString();
    }
    
}
