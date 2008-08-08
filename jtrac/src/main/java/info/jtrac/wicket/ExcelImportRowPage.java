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

import info.jtrac.domain.ExcelFile;
import info.jtrac.domain.ExcelFile.Cell;
import info.jtrac.domain.ExcelFile.Column;
import java.util.Date;
import java.util.List;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * edit data in a row
 */
public class ExcelImportRowPage extends BasePage {                                                       
    
    public ExcelImportRowPage(final ExcelImportPage previous, final int index) { 
        
        add(new FeedbackPanel("feedback"));
        
        add(new Link("cancel") {
            public void onClick() {
                setResponsePage(previous);
            }
        });          
        
        final ExcelFile excelFile = previous.getExcelFile();  
        
        final List<Cell> rowCells = excelFile.getRowCellsCloned(index);
        
        Form form = new Form("form") {
            @Override
            public void onSubmit() {
                excelFile.setRowCells(index, rowCells);
                setResponsePage(previous);
            }
        };
        
        add(form);
        
        final SimpleAttributeModifier CLASS_SELECTED = new SimpleAttributeModifier("class", "selected");
        
        ListView listView = new ListView("cells", rowCells) {
            protected void populateItem(ListItem item) {
                Column column = excelFile.getColumns().get(item.getIndex());
                item.add(new Label("heading", column.getLabel()));
                final Cell cell = (Cell) item.getModelObject();
                TextArea textArea = new TextArea("value");
                textArea.setModel(new IModel() {
                    public Object getObject() {
                        return cell.getValue();
                    }
                    public void setObject(Object o) {
                        cell.setValue(o);
                    }
                    public void detach() {
                    }                    
                });
                textArea.setLabel(new Model(column.getLabel()));
                textArea.add(new ErrorHighlighter());
                Object value = cell.getValue();
                if (value != null) {
                    if (value instanceof Date) {
                        textArea.setType(Date.class);
                    } else if (value instanceof Double) {
                        textArea.setType(Double.class);
                    }
                }
                item.add(textArea);
                if (column.getColumnHeading() != null) {
                    item.add(CLASS_SELECTED);
                    textArea.setEnabled(false);
                }
            }
        };
        
        listView.setReuseItems(true);
        
        form.add(listView);
        
    }
    
}
