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
 * TODO better validation, type-safety, masking of mail server password
 */
public class Config implements Serializable {
    
    private String param;  // someone reported that "key" is a reserved word in MySQL
    private String value;

    private static final Set<String> PARAMS;
    
    // set up a static set of valid config key names
    static {
        PARAMS = new LinkedHashSet<String>();
        PARAMS.add("mail.server.host");
        PARAMS.add("mail.server.port");
        PARAMS.add("mail.server.username");
        PARAMS.add("mail.server.password");
        PARAMS.add("mail.server.starttls.enable");
        PARAMS.add("mail.subject.prefix");
        PARAMS.add("mail.from");
        PARAMS.add("mail.smtp.localhost");
        PARAMS.add("mail.session.jndiname");
        PARAMS.add("jtrac.url.base");
        PARAMS.add("jtrac.header.picture");
        PARAMS.add("jtrac.header.text");
        PARAMS.add("jtrac.edit.item");
        PARAMS.add("locale.default");
        PARAMS.add("session.timeout");
        PARAMS.add("attachment.maxsize");
    }
    
    public static Set<String> getParams() {
        return PARAMS;
    }
    
    public Config() {
        // zero arg constructor
    }
    
    public Config(String param, String value) {
        this.param = param;
        this.value = value;
    }
    
    public boolean isMailConfig() {
        return param.startsWith("mail.") || param.startsWith("jtrac.url.");
    }
    
    public boolean isAttachmentConfig() {
        return param.startsWith("attachment.");
    }
    
    public boolean isSessionTimeoutConfig() {
        return param.startsWith("session.");
    }
    
    public boolean isLocaleConfig() {
        return param.startsWith("locale.");
    }
    
    //==========================================================================
    
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
