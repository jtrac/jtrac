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
    
    private String param;  // someone reported that "key" is a reserved word in MySQL
    private String value;

    private static final Set<String> params;
    
    // set up a static set of valid config key names
    static {
        params = new LinkedHashSet<String>();
        params.add("mail.server.host");
        params.add("mail.server.port");
        params.add("mail.server.username");
        params.add("mail.server.password");
        params.add("mail.server.starttls.enable");
        params.add("mail.subject.prefix");
        params.add("mail.from");
        params.add("jtrac.url.base");
        params.add("locale.default");
    }
    
    public static Set<String> getParams() {
        return params;
    }
    
    public Config() {
        // zero arg constructor
    }
    
    public Config(String param, String value) {
        this.param = param;
        this.value = value;
    }
    
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    /* custom accessor */
    public String getValue() {
        if(value != null && value.trim().length() == 0) {
            return null;
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
