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

package info.jtrac.acegi;

import org.acegisecurity.providers.cas.ticketvalidator.CasProxyTicketValidator;

/**
 * class that exists purely to add a couple of setters to the Acegi CasProxyTicketValidator
 * so that the loginUrl ' logoutUrl can be also included in the applicationContext-acegi-cas.xml
 * since we use Wicket, we don't need the CasProcessingFilterEntryPoint
 * kind of a hack, would have been much better to use the JtracConfigurer + properties file
 * but people who want to use CAS are assumed to be good at hacking XML :)
 * plus Acegi seems to be undergoing a major overhaul at the moment as well
 * and haven't yet looked at CAS 3 yet
 */
public class JtracCasProxyTicketValidator extends CasProxyTicketValidator {
    
    private String loginUrl;
    private String logoutUrl;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }        

}
