package @@project.name@@;

import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class @@applicationClassName@@ extends WebApplication {

	private final Logger logger = LoggerFactory.getLogger(getClass());

    public Class getHomePage() {
        return HomePage.class;
    }

}
