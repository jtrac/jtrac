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
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import info.jtrac.domain.Counts;
import java.util.Collection;

import java.util.List;

/**
 * Jtrac DAO Interface
 * all database access operations
 */
public interface JtracDao {
    
    void storeItem(Item item);
    Item loadItem(long id);
    List<Item> findItems(long sequenceNum, String prefixCode);
    List<Item> findItems(ItemSearch itemSearch);
    //===========================================
    void storeAttachment(Attachment attachment);
    //===========================================
    void storeMetadata(Metadata metadata);
    Metadata loadMetadata(long id);
    //===========================================
    void storeSpace(Space space);
    Space loadSpace(long id);
    List<Space> findSpacesByPrefixCode(String prefixCode);
    List<Space> findAllSpaces();
    //===========================================
    void storeSpaceSequence(SpaceSequence spaceSequence);
    SpaceSequence loadSpaceSequence(long id);
    //===========================================
    void storeUser(User user);
    User loadUser(long id);
    List<User> findAllUsers();
    List<User> findUsersByLoginName(String loginName);
    List<User> findUsersByEmail(String email);
    List<User> findUsersForSpace(long spaceId);
    List<UserRole> findUserRolesForSpace(long spaceId);
    List<User> findUsersForSpaceSet(Collection<Space> spaces);
    //===========================================
    Counts loadCountsForUser(long userId);
    List<Config> findAllConfig();
    void storeConfig(Config config);
    Config loadConfig(String key);
    
}
