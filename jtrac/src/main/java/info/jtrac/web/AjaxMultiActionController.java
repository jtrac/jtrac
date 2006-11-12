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

package info.jtrac.web;

import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring MultiActionController that handles Ajax requests
 * we return HTML through a JSP as far as possible, to keep things simple
 */
public class AjaxMultiActionController extends AbstractMultiActionController {
    
    public ModelAndView ajaxDashboardHandler(HttpServletRequest request, HttpServletResponse response) {
        String spaceId = request.getParameter("spaceId");
        Space space = jtrac.loadSpace(Long.parseLong(spaceId));
        User user = SecurityUtils.getPrincipal();        
        ModelAndView mav = new ModelAndView("ajax_dashboard");
        Map states =  new TreeMap(space.getMetadata().getStates());
        states.remove(State.NEW);
        mav.addObject("states", states);
        mav.addObject("stateCount", states.size());
        mav.addObject("space", space);
        mav.addObject("counts", jtrac.loadCountsForUserSpace(user, space));
        applyCacheSeconds(response, 0, true);
        return mav;
    } 
    
    public ModelAndView ajaxItemViewUsersHandler(HttpServletRequest request, HttpServletResponse response) {
        String spaceId = request.getParameter("spaceId");
        String fromState = request.getParameter("fromState");
        String toState = request.getParameter("toState");
        logger.debug("ajaxItemViewUsersHandler: spaceId = " + spaceId + ", fromState = " + fromState + ", toState = " + toState);
        Space space = jtrac.loadSpace(Long.parseLong(spaceId));
        Map<String, Boolean> map = space.getMetadata().getRolesAbleToTransition(Integer.parseInt(fromState), Integer.parseInt(toState));
        List<UserSpaceRole> userSpaceRoles = jtrac.findUserRolesForSpace(Long.parseLong(spaceId));
        List<UserSpaceRole> list = new ArrayList<UserSpaceRole>(userSpaceRoles.size());
        for(UserSpaceRole usr : userSpaceRoles) {
            if(map.containsKey(usr.getRoleKey())) {
                list.add(usr);
            }
        }
        return new ModelAndView("ajax_item_view_users", "userSpaceRoles", list);        
    }
    
}
