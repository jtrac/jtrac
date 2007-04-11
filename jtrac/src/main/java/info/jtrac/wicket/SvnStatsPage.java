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

package info.jtrac.wicket;

import info.jtrac.util.SvnUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.BoundCompoundPropertyModel;

/**
 * subversion statistics chart
 */
public class SvnStatsPage extends BasePage {
      
    private WebMarkupContainer hide = new WebMarkupContainer("hide");
    private Form form;
    
    public SvnStatsPage() {                          
        setVersioned(false);
        form = new SvnForm("form");
        add(form);
        add(hide);
        hide.setVisible(false);
    }
    
    private class SvnForm extends Form {
        
        public SvnForm(String id) {
            super(id);
            SvnFormModel modelObject = new SvnFormModel();
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
            setModel(model);
            add(new TextField("url").setRequired(true));
            add(new TextField("loginName"));
            add(new PasswordTextField("password").setRequired(false));
        }
        
        @Override
        protected void onSubmit() {
            final SvnFormModel model = (SvnFormModel) getModelObject();
            final Map<String, Integer> commitsPerCommitter = 
                    SvnUtils.getCommitsPerCommitter(SvnUtils.getRepository(model.getUrl(), model.getLoginName(), model.getPassword()));
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : commitsPerCommitter.entrySet()) {
                dataset.addValue(entry.getValue(), "Commits", entry.getKey());
            }             
            List<String> users = new ArrayList(commitsPerCommitter.keySet());
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            hide.add(new ListView("users", users) {
                protected void populateItem(ListItem listItem) {
                    if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }                      
                    String user = (String) listItem.getModelObject();
                    listItem.add(new Label("user", user));
                    listItem.add(new Label("commits", commitsPerCommitter.get(user) + ""));
                    
                }
            });
            JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);
            BufferedDynamicImageResource resource = new BufferedDynamicImageResource();
            resource.setImage(chart.createBufferedImage(600, 300));
            hide.add(new Image("chart", resource));
            hide.setVisible(true);
            form.setVisible(false);
        }        
        
    }
    
    
    
    private class SvnFormModel implements Serializable {
        
        private String url;
        private String loginName;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        
        
    }
    
}
