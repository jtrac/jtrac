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

package info.jtrac.wicket.devmode;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this is a custom session store for development mode that just extends the wicket 
 * http session store but is designed to warn of anything encountered that is 
 * not-serializable, see this wicket mail discussion thread:
 * 
 * http://www.nabble.com/Page-serialization-and-reloading-don%27t-get-on-td15574828.html#a15574828
 * 
 * As a bonus, we log the size of each item serialized so that we get an idea
 * if anything is bloated and needs optimization
 */
public class DebugHttpSessionStore extends HttpSessionStore {

    private static final Logger logger = LoggerFactory.getLogger(DebugHttpSessionStore.class);
    
    private static final String banner = 
        "\n***********************************************\n"
        + "*** WARNING: Debug HttpSession store in use ***\n"
        + "***    This is wrong if production mode.    ***\n"
        + "***********************************************";       
    
    public DebugHttpSessionStore(Application application) {
        super(application);
        logger.warn(banner);
    }

    @Override
    public void setAttribute(Request request, String name, Object value) {        
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(os);
            stream.writeObject(value);
            stream.close();
            logger.debug(os.size() + " bytes: " + name + " [" + value.getClass().getName() + "]");
        } catch (Exception e) {
            logger.error("serialization failed for name: " + name + ", class: " + value.getClass(), e);
            // throw Error so that we sit up and take notice
            throw new Error("Unable to serialize value: ", e);
        }
        super.setAttribute(request, name, value);
    }

}
