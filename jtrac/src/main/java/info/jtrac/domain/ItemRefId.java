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

import info.jtrac.exception.InvalidRefIdException;
import java.io.Serializable;

/**
 * Class that exists purely to parse a String into a valid item ref id of the form ABC-123
 */
public class ItemRefId implements Serializable {
    
    private long sequenceNum;
    private String prefixCode;
    
    public ItemRefId(String refId) throws InvalidRefIdException {
        int pos = refId.indexOf('-');
        if (pos == -1) {
            throw new InvalidRefIdException("invalid ref id");
        }
        try {
            sequenceNum = Long.parseLong(refId.substring(pos + 1));
        } catch (NumberFormatException e) {
            throw new InvalidRefIdException("invalid ref id");
        }
        prefixCode = refId.substring(0, pos).toUpperCase();
    }

    public String getPrefixCode() {
        return prefixCode;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }    
    
}
