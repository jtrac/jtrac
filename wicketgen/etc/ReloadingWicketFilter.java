package @@project.name@@;

import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class @@project.name.titleCase@@ReloadingWicketFilter extends ReloadingWicketFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String banner =
        "\n***********************************************\n"
        + "*** WARNING: Reloading Wicket Filter in use ***\n"
        + "***    This is wrong if production mode.    ***\n"
        + "***********************************************";

    static {
        ReloadingClassLoader.includePattern("@@project.name@@.*");
    }

    public @@project.name.titleCase@@ReloadingWicketFilter() {
		super();
        logger.warn(banner);
    }

}
