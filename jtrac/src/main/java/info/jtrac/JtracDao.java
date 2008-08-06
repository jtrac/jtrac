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
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.UserSpaceRole;
import java.util.Collection;

import java.util.List;

/**
 * Jtrac DAO Interface
 * all database access operations
 */
public interface JtracDao {
    
    void storeItem(Item item);
    Item loadItem(long id);
    History loadHistory(long id);
    void storeHistory(History history);
    List<Item> findItems(long sequenceNum, String prefixCode);
    List<Item> findItems(ItemSearch itemSearch);
    int loadCountOfAllItems();
    List<Item> findAllItems(int firstResult, int batchSize);
    void removeItem(Item item);
    void removeItemItem(ItemItem itemItem);
    List<ItemUser> findItemUsersByUser(User user);
    void removeItemUser(ItemUser itemUser);
    //===========================================
    int loadCountOfRecordsHavingFieldNotNull(Space space, Field field);
    int bulkUpdateFieldToNull(Space space, Field field);
    int loadCountOfRecordsHavingFieldWithValue(Space space, Field field, int optionKey);
    int bulkUpdateFieldToNullForValue(Space space, Field field, int optionKey);
    int loadCountOfRecordsHavingStatus(Space space, int status);
    int bulkUpdateStatusToOpen(Space space, int status);
    int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey, String newRoleKey);
    int bulkUpdateDeleteSpaceRole(Space space, String roleKey);
    int bulkUpdateDeleteItemsForSpace(Space space);    
    //========================================================    
    void storeAttachment(Attachment attachment);
    //===========================================
    void storeMetadata(Metadata metadata);
    Metadata loadMetadata(long id);
    //===========================================
    void storeSpace(Space space);
    Space loadSpace(long id);
    List<Space> findSpacesByPrefixCode(String prefixCode);
    List<Space> findAllSpaces();    
    List<Space> findSpacesNotAllocatedToUser(long userId);
    List<Space> findSpacesWhereIdIn(List<Long> ids);
    List<Space> findSpacesWhereGuestAllowed();
    void removeSpace(Space space);
    //=========================================== 
    long loadNextSequenceNum(long spaceSequenceId); 
    void storeSpaceSequence(SpaceSequence spaceSequence);
    //===========================================
    void storeUser(User user);
    User loadUser(long id);
    void removeUser(User user);       
    List<User> findAllUsers();
    List<User> findUsersWhereIdIn(List<Long> ids);
    List<User> findUsersMatching(String searchText, String searchOn);
    List<User> findUsersByLoginName(String loginName);
    List<User> findUsersByEmail(String email);
    List<User> findUsersForSpace(long spaceId);
    List<User> findUsersNotAllocatedToSpace(long spaceId);
    List<UserSpaceRole> findUserRolesForSpace(long spaceId);
    List<UserSpaceRole> findSpaceRolesForUser(long userId);
    List<User> findUsersWithRoleForSpace(long spaceId, String roleKey);
    List<User> findUsersForSpaceSet(Collection<Space> spaces);
    List<User> findSuperUsers();
    int loadCountOfHistoryInvolvingUser(User user);
    //===========================================
    UserSpaceRole loadUserSpaceRole(long id);
    void removeUserSpaceRole(UserSpaceRole userSpaceRole);
    //===========================================
    CountsHolder loadCountsForUser(User user);
    Counts loadCountsForUserSpace(User user, Space space);
    //===========================================
    List<Config> findAllConfig();
    void storeConfig(Config config);
    Config loadConfig(String key);
    
}
