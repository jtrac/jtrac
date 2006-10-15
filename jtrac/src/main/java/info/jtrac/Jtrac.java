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

import info.jtrac.domain.Attachment;
import info.jtrac.domain.Config;
import info.jtrac.domain.Counts;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;

import java.util.List;
import java.util.Map;


import org.acegisecurity.userdetails.UserDetailsService;

/**
 * Jtrac main business interface (Service Layer)
 */
public interface Jtrac extends UserDetailsService {
      
    void storeItem(Item item, Attachment attachment);
    void storeHistoryForItem(Item item, History history, Attachment attachment);
    Item loadItem(long id);
    Item loadItem(long sequenceNum, String prefixCode);
    History loadHistory(long id);
    List<Item> findItems(ItemSearch itemSearch);
    void removeItemItem(ItemItem itemItem);
    //========================================================
    int findItemCount(Space space, Field field);
    int removeField(Space space, Field field);
    int findItemCount(Space space, Field field, String optionKey);
    int removeFieldValues(Space space, Field field, String optionKey);
    //========================================================
    void rebuildIndexes();
    //========================================================
    void storeUser(User user);
    List<User> findAllUsers();
    User loadUser(long id);
    User loadUser(String loginName);
    List<User> findUsersForSpace(long spaceId);
    List<UserSpaceRole> findUserRolesForSpace(long spaceId);
    List<User> findUsersForUser(User user);
    List<User> findUnallocatedUsersForSpace(long spaceId);
    //========================================================
    Counts loadCountsForUser(User user);
    //========================================================
    void storeSpace(Space space);
    Space loadSpace(long id);
    Space loadSpace(String prefixCode);
    List<Space> findAllSpaces();
    List<Space> findSpacesWhereGuestAllowed();
    List<Space> findUnallocatedSpacesForUser(long userId);
    //========================================================
    void storeUserSpaceRole(User user, Space space, String roleKey);        
    UserSpaceRole loadUserSpaceRole(long id);
    void removeUserSpaceRole(UserSpaceRole userSpaceRole);
    int renameSpaceRole(String oldRoleKey, String newRoleKey, Space space);
    //========================================================
    void storeMetadata(Metadata metadata);
    Metadata loadMetadata(long id);
    //========================================================
    String generatePassword();
    String encodeClearText(String clearText);
    //========================================================
    Map<String, String> loadAllConfig();
    void storeConfig(Config config);
    String loadConfig(String param);    
    
}
