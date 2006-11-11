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

import info.jtrac.domain.Config;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.AttachmentUtils;
import info.jtrac.util.ExcelUtils;
import info.jtrac.util.SecurityUtils;
import info.jtrac.util.SvnUtils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.acegisecurity.ui.cas.CasProcessingFilterEntryPoint;
import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.execution.FlowExecution;

public class DefaultMultiActionController extends AbstractMultiActionController {
    
    private AuthenticationEntryPoint authenticationEntryPoint;

    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    
    public ModelAndView loginHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {        
        if (authenticationEntryPoint instanceof CasProcessingFilterEntryPoint) {
            logger.info("CAS mode detected, attempting CasProcessingFilterEntryPoint");            
            authenticationEntryPoint.commence(request, response, null);
            return null;
        }
        ModelAndView mav = new ModelAndView("login");
        if (request.getParameter("error") != null) {
            AuthenticationException ae = (AuthenticationException) WebUtils.getSessionAttribute(request, AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY);
            mav.addObject("message", ae.getMessage());            
        }
        String loginName = (String) WebUtils.getSessionAttribute(request, AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY);              
        mav.addObject("loginName", loginName);
        return mav;
    }

    public ModelAndView logoutHandler(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        Cookie terminate = new Cookie(TokenBasedRememberMeServices.ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE_KEY, null);
        terminate.setMaxAge(0);
        response.addCookie(terminate);
        return new ModelAndView("logout");
    }

    public ModelAndView dashboardHandler(HttpServletRequest request, HttpServletResponse response) {
        User user = SecurityUtils.getPrincipal();
        ModelAndView mav = new ModelAndView("dashboard");
        mav.addObject("countsHolder", jtrac.loadCountsForUser(user));
        // performance hack, because of the principal (loaded by Acegi) not in sync with our
        // open session in view.  Basically cache the "SpacesWhereAbleToCreateNewItem" else
        // rendering dashboard would require a lot of extra queries to load Space + Metadata
        // which the userSpaceRole.isAbleToCreateNewItem() method requires
        if (user.getSpacesWhereAbleToCreateNewItem() == null) {
            Map<Long, Boolean> map = new HashMap<Long, Boolean>();
            for(UserSpaceRole usr : jtrac.loadUser(user.getId()).getSpaceRoles()) {
                if (usr.isAbleToCreateNewItem()) {
                    map.put(usr.getSpace().getId(), true);
                }
            }
            user.setSpacesWhereAbleToCreateNewItem(map);
        }
        applyCacheSeconds(response, 0, true);
        return mav;
    }   

    public ModelAndView optionsHandler(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("options");
    }      
    
    public ModelAndView svnFormHandler(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("svn_form");
    }    
    
    public ModelAndView reindexHandler(HttpServletRequest request, HttpServletResponse response) {
        jtrac.rebuildIndexes();
        return new ModelAndView("index_rebuild_success");
    }     
    
    public ModelAndView svnViewHandler(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getParameter("url");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String, Integer> commitsPerCommitter = SvnUtils.getCommitsPerCommitter(SvnUtils.getRepository(url, username, password));
        request.getSession().setAttribute("commitsPerCommitter", commitsPerCommitter);
        return new ModelAndView("svn_view");
    }    

    public ModelAndView attachmentViewHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AttachmentUtils.download(getServletContext(), request, response);
        return null;
    }

    public ModelAndView svnCommitsPerCommitterChartHandler(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Integer> commitsPerCommitter = (Map<String, Integer>) request.getSession().getAttribute("commitsPerCommitter");
        request.getSession().setAttribute("commitsPerCommitter", null);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : commitsPerCommitter.entrySet()) {
            dataset.addValue(entry.getValue(), "Commits", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        ChartUtilities.writeChartAsJPEG(out, chart, 600, 300);
        out.close();
        return null;
    }

    public ModelAndView userListHandler(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("user_list", "users", jtrac.findAllUsers());
    }

    public ModelAndView spaceListHandler(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("space_list", "spaces", jtrac.findAllSpaces());
    }    

    public ModelAndView configListHandler(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("configMap", jtrac.loadAllConfig());
        model.put("configParams", Config.getParams());
        return new ModelAndView("config_list", model);
    }
    
    public ModelAndView itemListExcelHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {        
        FlowExecution flow = getFlowExecution(request, response);        
        ItemSearch itemSearch = (ItemSearch) flow.getActiveSession().getScope().get("itemSearch");
        int pageSize = itemSearch.getPageSize();
        itemSearch.setPageSize(-1); // temporarily switch off paging of results 
        ExcelUtils eu = new ExcelUtils(jtrac.findItems(itemSearch), itemSearch);
        itemSearch.setPageSize(pageSize);
        response.setContentType("application/unknow");
        response.setHeader("Content-Disposition", "inline;filename=jtrac-export.xls");
        ServletOutputStream out = response.getOutputStream();
        eu.exportToExcel().write(out);
        out.flush();        
        return null;        
    }       
    
}
