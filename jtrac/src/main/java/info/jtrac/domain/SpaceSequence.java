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
 * Class that exists purely to denormalize the Space entity in the database.
 * A Space has a one-to-one relationship with SpaceSequence
 * Since the "nextSeqNum" property is going to be updated on every new 
 * Item insert, this is kept in a separate "sequence" kind of table to 
 * reduce the number of updates that happen for the Space object / table
 */
public class SpaceSequence implements Serializable {
    
    private int id;
    private long nextSeqNum;

    public long getNextSeqNum() {
        return nextSeqNum;
    }

    public void setNextSeqNum(long nextSeqNum) {
        this.nextSeqNum = nextSeqNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
