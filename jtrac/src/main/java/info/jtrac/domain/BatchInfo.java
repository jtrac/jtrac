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
 * class that was created just to bridge the UI and the service-tier
 * for batch operations and allows the UI to query this object for
 * status of a long running batch so that a progress bar could be updated for e.g.
 * but it also holds useful generic info like size of batch etc.
 */
public class BatchInfo implements Serializable {
    
    private static final int BATCH_SIZE = 500;
    
    private int batchSize = BATCH_SIZE;  
    private int totalSize;
    private int currentPosition;

    public boolean isComplete() {
        return currentPosition >= totalSize;
    }
    
    public void incrementPosition() {
        currentPosition++;
    }
    
    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }        

}
