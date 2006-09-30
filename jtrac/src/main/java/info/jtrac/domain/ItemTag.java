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

import java.io.Serializable;

/**
 * Class that exists purely to hold a single Tag associated with an item
 * along with a integer "type" indicating the nature of the relationship
 * between Item --> Tag (one directional relationship)
 *
 * This is used for allowing an Item to have many Tags, web 2.0 style
 */
public class ItemTag implements Serializable {
    
    private long id;
    private Tag tag;
    private int type;
    
    public ItemTag() {
        // zero arg constructor
    }
    
    public ItemTag(Tag tag) {
        this.tag = tag;
    }
    
    public ItemTag(Tag tag, int type) {
        this.tag = tag;
        this.type = type;
    }

    //=================================================
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }    
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; tag [").append(tag);
        sb.append("]; type [").append(type);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemTag)) {
            return false;
        }
        final ItemTag it = (ItemTag) o;
        return (tag.equals(it.getTag()) && type == it.getType());
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + tag.hashCode();
        hash = hash * 31 + type;
        return hash;
    }
    
}
