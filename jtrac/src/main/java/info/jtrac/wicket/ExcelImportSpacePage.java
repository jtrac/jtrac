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

import info.jtrac.domain.Space;
import java.util.List;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;

/**
 * choose space to import into
 */
public class ExcelImportSpacePage extends BasePage {               
        
    private Space space;
    
    public ExcelImportSpacePage(final ExcelImportPage previous) { 
        
        Form form = new Form("form") {
            @Override
            public void onSubmit() {
                if(space == null) {
                    return;
                }
               previous.setSpace(space);
               setResponsePage(previous);
            }                  
        };
        add(form);        
        
        List<Space> spaces = getJtrac().findAllSpaces();        
        DropDownChoice spaceChoice = new DropDownChoice("space", spaces);
        spaceChoice.setModel(new PropertyModel(this, "space"));
        spaceChoice.setChoiceRenderer(new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                Space s = (Space) o;
                return s.getName() + " [" + s.getPrefixCode() + "]";
            }
            public String getIdValue(Object o, int i) {
                return ((Space) o).getId() + "";
            }
        }); 
        spaceChoice.setNullValid(true);
        
        form.add(spaceChoice);
        
        add(new Link("cancel") {
            public void onClick() {
                setResponsePage(previous);
            }
        });        
    }
    
}
