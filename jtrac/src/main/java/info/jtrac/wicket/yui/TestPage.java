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

package info.jtrac.wicket.yui;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

/**
 * test page
 */
public class TestPage extends WebPage {
    
    public TestPage() {
        TestPanel testPanel = new TestPanel(YuiPanel.CONTENT_ID);
        YuiPanel panel = new YuiPanel("dialog", "Test Dialog", testPanel, null);
        add(panel);
        WebMarkupContainer link = new WebMarkupContainer("link");
        link.add(new SimpleAttributeModifier("onClick", panel.getShowScript() + testPanel.getFocusScript()));
        add(link);
    }
    
}
