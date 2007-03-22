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

import info.jtrac.Jtrac;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * custom simple LDAP integration approach, where only authentication
 * is expected from LDAP, Space allocations have to be within JTrac only
 *
 * we are not using Acegi LDAP support because
 * a) it does not appear to support binding AS the user signing in
 * b) easier to configure and customize
 */
public class JtracLdapAuthenticationProvider implements AuthenticationProvider {

    private Jtrac jtrac;
    private String ldapUrl;
    private String domain;
    private String searchBase;
    private String searchKey = "sAMAccountName";
    private String displayNameKey = "cn";
    private String mailKey = "mail";
    
    private final Log logger = LogFactory.getLog(getClass());

    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }    

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setDisplayNameKey(String displayNameKey) {
        this.displayNameKey = displayNameKey;
    }

    public void setMailKey(String mailKey) {
        this.mailKey = mailKey;
    }    
    
    public boolean supports(Class clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
    }    
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        String displayName = null;
        String mail = null;
        logger.debug("attempting authentication via LDAP");       
        if(bind(authentication.getName(), authentication.getCredentials().toString(), displayName, mail)) {
            logger.debug("user details retrieved from LDAP, now checking local database");
            UserDetails userDetails = null;
            try {
                 userDetails = jtrac.loadUserByUsername(authentication.getName());
            } catch(AuthenticationException ae) { // catch just to log, then throw
                logger.debug("ldap user not allocated to any Spaces within JTrac");
                throw ae;
            }
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } else {
            logger.debug("returning null from ldap authentication provider");
            return null;
        }        
    }

    /**
     * displayName and mail are returned for convenience
     */
    public boolean bind(String loginName, String password, String displayName, String mail) {        
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");        
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        // please refer http://forum.java.sun.com/thread.jspa?threadID=726601&tstart=0
        // for the Active Directory LDAP Fast Bind Control approach used here
        Control control = new Control() {
            public byte[] getEncodedValue() {
                return null;
            }
            public String getID() {
                return "1.2.840.113556.1.4.1781";
            }
            public boolean isCritical() {
                return true;
            }            
        };
        Control[] controls = new Control[] { control };
        try {
            LdapContext ctx = new InitialLdapContext(env, controls);
            logger.debug("Active Directory LDAP context initialized");
            String prefix = domain == null ? "" : domain + "\\";
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, prefix + loginName);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            // javax.naming.AuthenticationException
            ctx.reconnect(controls);
            logger.debug(loginName + ": LDAP bind successful");        
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            sc.setReturningAttributes(new String[] { mailKey, displayNameKey });
            NamingEnumeration results = ctx.search(searchBase, searchKey + "=" + loginName, sc);
            while(results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
                logger.debug("attributes: " + attrs);
                Attribute aMail = attrs.get(mailKey);
                mail = (String) aMail.get();
                logger.debug("mail: " + mail);
                Attribute aDisp = attrs.get(displayNameKey);
                displayName = (String) aDisp.get();
                logger.debug("displayName: " + displayName);
                break; // there should be only one anyway
            }
            return true;
        } catch(Exception e) {
            logger.error("LDAP authentication failed: " + e);
            return false;
        }
    }    
    
}
