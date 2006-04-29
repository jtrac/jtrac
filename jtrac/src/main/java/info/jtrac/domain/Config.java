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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple name value pair to hold configuration parameters
 * in the database for JTrac, e.g. SMTP e-mail server, etc.
 */
public class Config implements Serializable {
    
    private String key;
    private String value;

    private static final Set<String> keys;
    
    // set up a static set of valid config key names
    static {
        keys = new LinkedHashSet<String>();
        keys.add("smtp.server.host");
        keys.add("smtp.server.port");
    }
    
    public static Set<String> getKeys() {
        return keys;
    }
    
    public Config() {
        // zero arg constructor
    }
    
    public Config(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
