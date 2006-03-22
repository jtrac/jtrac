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

package info.jtrac;

import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceRole;
import info.jtrac.domain.User;

import java.util.List;
import java.util.Random;


import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Jtrac Facade implementation
 * Here is where all the business logic is
 * For data persistence this delegates to JtracDao
 */
public class JtracImpl implements Jtrac {
    
    private JtracDao dao;
    private PasswordEncoder passwordEncoder;
    
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    private final Log logger = LogFactory.getLog(getClass());
    
    // public for testing
    public String generatePassword() {
        byte[] ab = new byte[1];
        Random r = new Random();
        r.nextBytes(ab);
        return passwordEncoder.encodePassword(new String(ab), null).substring(24);
    }
    
    // public for testing
    public String encodeClearTextPassword(String clearText) {
        return passwordEncoder.encodePassword(clearText, null);
    }
    
    // =========  acegi UserDetailsService implementation ==========
    public UserDetails loadUserByUsername(String loginName) {
        User user = null;
        if (loginName.indexOf("@") != -1) {
            user = dao.loadUserByEmail(loginName);
        } else {
            user = dao.loadUser(loginName);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found for '" + loginName + "'");
        }
        logger.debug("acegi: loadUserByUserName success for '" + loginName + "'");
        return user;
    }
    
    public User loadUser(int id) {
        return dao.loadUser(id);
    }
    
    public User loadUser(String loginName) {
        return dao.loadUser(loginName);
    }
    
    public void storeUser(User user) {
        logger.info("Saving User");
        String password = user.getPassword();
        if (password == null) {
            user.setPassword(generatePassword());
        } else {
            user.setPassword(encodeClearTextPassword(password));
        }
        dao.storeUser(user);
    }
    
    public List<User> loadAllUsers() {
        return dao.loadAllUsers();
    }
    
    public Space loadSpace(int id) {
        return dao.loadSpace(id);
    }
    
    public Space loadSpace(String prefixCode) {
        List<Space> spaces = dao.loadSpace(prefixCode);
        if (spaces.size() > 0) {
            return spaces.get(0);
        }
        return null;
    }
    
    public void storeSpace(Space space) {
        dao.storeSpace(space);
    }
    
    public List<Space> loadAllSpaces() {
        return dao.loadAllSpaces();
    }
    
    public List<Space> loadUnallocatedSpacesForUser(int userId) {
        List<Space> spaces = loadAllSpaces();
        User user = loadUser(userId);
        for(SpaceRole spaceRole : user.getSpaceRoles()) {
            spaces.remove(spaceRole.getSpace());
        }
        return spaces;
    }
    
    public void storeMetadata(Metadata metadata) {
        dao.storeMetadata(metadata);
    }
    
    public Metadata loadMetadata(int id) {
        return dao.loadMetadata(id);
    }    
    
}
