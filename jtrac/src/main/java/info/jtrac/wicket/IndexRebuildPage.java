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

import info.jtrac.Jtrac;
import info.jtrac.domain.AbstractItem;
import java.util.List;
import wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.IModel;
import wicket.util.time.Duration;

/**
 * rebuild indexes admin option
 */
public class IndexRebuildPage extends BasePage {      
    
    public IndexRebuildPage(boolean success) {                    
        if(success) {
            add(new Label("heading", localize("index_rebuild_success.message")));
            add(new WebMarkupContainer("form").setVisible(false));
        } else {
            add(new Label("heading", localize("index_rebuild.heading")));
            add(new RebuildIndexesForm("form"));
        }
    }
    
    private class RebuildIndexesForm extends Form {
        
        private int current;
        private int total;
        private boolean finished;        
        
        public RebuildIndexesForm(String id) {
            
            super(id);
            
            final Label progress = new Label("progress");
            progress.setOutputMarkupId(true);               
            
            add(new Button("start") {
                @Override
                public void onSubmit() {
                    // hide the button
                    this.setVisible(false);
                    // long running process, use thread
                    new Thread() {
                        // don't serialize this!
                        transient Jtrac jtrac = getJtrac();
                        public void run() {
                            jtrac.clearIndexes();
                            List<AbstractItem> items = jtrac.findAllItems();
                            total = items.size();
                            for(current = 0; current < total; current++) {
                                jtrac.index(items.get(current));
                            }
                            finished = true;
                        }                    
                    }.start();
                    
                    // poll and update the progress every 5 seconds
                    progress.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
                    IModel model = new AbstractReadOnlyModel() {
                        public Object getObject() {
                            if(finished) {
                                // reshow the page, with success message
                                setResponsePage(new IndexRebuildPage(true));
                            }
                            int percent = total == 0 ? 0 : 100 * current / total;
                            return percent + "% [" + current + " / " + total + "]";
                        };
                    };
                    progress.setModel(model);             
                }
            });

            add(progress);            
        }
        
    }
    
}
