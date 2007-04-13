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

package info.jtrac.config;

import info.jtrac.Jtrac;
import info.jtrac.acegi.JtracLdapAuthenticationProvider;
import java.util.ArrayList;
import java.util.List;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * acegi authentication provider manager factory bean
 * conditionally sets up ldap authentication
 */
public class ProviderManagerFactoryBean implements FactoryBean {
    
    private Jtrac jtrac;    
    private String ldapUrl;
    private String activeDirectoryDomain;
    private String searchBase;
    private AuthenticationProvider authenticationProvider;

    private final Log logger = LogFactory.getLog(getClass());

    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }    
    
    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public void setActiveDirectoryDomain(String activeDirectoryDomain) {
        this.activeDirectoryDomain = activeDirectoryDomain;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }    
    
    public Object getObject() throws Exception {        
        List providers = new ArrayList();
        if(ldapUrl.length() > 0) {
            logger.info("switching on ldap authentication provider");
            JtracLdapAuthenticationProvider ldapProvider = new JtracLdapAuthenticationProvider();
            ldapProvider.setLdapUrl(ldapUrl);
            ldapProvider.setActiveDirectoryDomain(activeDirectoryDomain);
            ldapProvider.setSearchBase(searchBase);
            ldapProvider.setJtrac(jtrac);
            providers.add(ldapProvider);
        } else {
            logger.info("not using ldap authentication");
        }       
        providers.add(authenticationProvider);
        ProviderManager mgr = new ProviderManager();
        mgr.setProviders(providers);
        return mgr;
    }

    public Class getObjectType() {
        return ProviderManager.class;
    }

    public boolean isSingleton() {
        return true;
    }
    
    
}
