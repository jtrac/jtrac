package @@project.name@@;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends WebPage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

    public HomePage() {
        add(new Label("myLabel", "Hello World"));
    }

}
