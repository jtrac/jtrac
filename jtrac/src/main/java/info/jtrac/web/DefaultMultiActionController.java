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

import info.jtrac.util.SvnUtils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

public class DefaultMultiActionController extends AbstractMultiActionController {
    
    public ModelAndView loginHandler(HttpServletRequest request,
            HttpServletResponse response) {
        String message = null;
        if (request.getParameter("error") != null) {
            AuthenticationException ae = (AuthenticationException) WebUtils.getSessionAttribute(request, AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY);
            message = ae.getMessage();
        }
        String loginName = (String) WebUtils.getSessionAttribute(request, AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY);
        Map<String, String> model = new HashMap<String, String>();
        model.put("message", message);
        model.put("loginName", loginName);
        return new ModelAndView("login", model);
    }
    
    public ModelAndView logoutHandler(HttpServletRequest request,
            HttpServletResponse response) {
        request.getSession().invalidate();
        Cookie terminate = new Cookie(TokenBasedRememberMeServices.ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE_KEY, null);
        terminate.setMaxAge(0);
        response.addCookie(terminate);      
        return new ModelAndView("logout");
    }
    
    public ModelAndView dashboardHandler(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("dashboard");
    }
    
    public ModelAndView optionsHandler(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("options");
    }    
    
    public ModelAndView svnFormHandler(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("svn_form");
    }    
    
    public ModelAndView svnViewHandler(HttpServletRequest request,
            HttpServletResponse response) {
        String url = request.getParameter("url");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String, Integer> commitsPerCommitter = SvnUtils.getCommitsPerCommitter(SvnUtils.getRepository(url, username, password));
        request.getSession().setAttribute("commitsPerCommitter", commitsPerCommitter);
        return new ModelAndView("svn_view");
    }
    
    public ModelAndView svnCommitsPerCommitterChartHandler(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Integer> commitsPerCommitter = (Map<String, Integer>) request.getSession().getAttribute("commitsPerCommitter");
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();        
        for (Map.Entry<String, Integer> entry : commitsPerCommitter.entrySet()) {
            dataset.addValue(entry.getValue(), "Commits", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);        
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        ChartUtilities.writeChartAsJPEG(out, chart, 600, 300);
        out.close(); 
        return new ModelAndView("this_is_never_used");
    }
    
    public ModelAndView userListHandler(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("user_list", "users", jtrac.loadAllUsers());
    }
    
    public ModelAndView spaceListHandler(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("space_list", "spaces", jtrac.loadAllSpaces());
    }    
    
}
