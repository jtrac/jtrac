package info.jtrac.wicket;

import info.jtrac.Jtrac;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wicket.protocol.http.WebApplication;

public class JtracApplication extends WebApplication {        
    
    private Jtrac jtrac;

    public Jtrac getJtrac() {
        return jtrac;
    }
    
    @Override
    public void init() {
        ServletContext sc = getWicketServlet().getServletContext();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sc);
        jtrac = (Jtrac) ac.getBean("jtrac");
    }    
    
    public Class getHomePage() {
        return DashboardPage.class;
    }    
    
}
