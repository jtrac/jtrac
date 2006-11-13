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

package info.jtrac.util;

import info.jtrac.domain.Space;
import info.jtrac.domain.UserSpaceRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * routines to filter User, UserSpaceRoles collections etc
 */
public class UserUtils {
    
    /**
     * This is a rather 'deep' concept, first of course you need to restrict the next possible
     * states that an item can be switched to based on the current state and the workflow defined.
     * But what about who all it can be assigned to?  This will be the set of users who fall into roles
     * that have permissions to transition FROM the state being switched to. Ouch.
     * This is why the item_view / history update screen has to be Ajaxed so that the drop
     * down list of users has to dynamically change based on the TO state
     */    
     public static List<UserSpaceRole> filterUsersAbleToTransitionFrom(List<UserSpaceRole> userSpaceRoles, Space space, int state) {
        Set<String> set = space.getMetadata().getRolesAbleToTransitionFrom(state);
        List<UserSpaceRole> list = new ArrayList<UserSpaceRole>(userSpaceRoles.size());
        for(UserSpaceRole usr : userSpaceRoles) {
            if(set.contains(usr.getRoleKey())) {
                list.add(usr);
            }
        } 
        return list;         
     }    
}
