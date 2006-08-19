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

package info.jtrac.lucene;

import info.jtrac.JtracDao;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import org.apache.lucene.document.Document;
import org.springmodules.lucene.search.core.HitExtractor;

/**
 * Uses Spring Modules Lucene support
 * converts a search "hit" into our domain object
 */
public class ItemHitExtractor implements HitExtractor {
    
    private JtracDao dao;
    
    public ItemHitExtractor(JtracDao dao) {
        this.dao = dao;
    }
    
    public Item mapHit(int i, Document document, float f) {
        String id = document.get("id");
        if (id == null) {
            return null;
        }        
        String type = document.get("type");
        Item item = null;
        if (type.equals("item")) {
            item = dao.loadItem(new Long(id));
        } else if (type.equals("history")) {
            History h = dao.loadHistory(new Long(id));
            if (h != null) {
                item = h.getParent();
            }
        }
        return item;
    }
 
}
