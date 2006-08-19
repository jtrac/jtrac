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

import org.apache.lucene.document.Document;
import org.springmodules.lucene.search.core.HitExtractor;

/**
 * Uses Spring Modules Lucene support
 * converts a search "hit" into an Item id, which we
 * can later use to load the actual object
 */
public class ItemIdHitExtractor implements HitExtractor {
    
    public Long mapHit(int i, Document document, float f) {
        String id = document.get("id");
        if (id == null) {
            return null;
        }        
        String type = document.get("type");
        if (type.equals("item")) {
            return new Long(id);
        } else if (type.equals("history")) {
            String itemId = document.get("itemId");
            return new Long(itemId);
        }
        return null;
    }
 
}
