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
import info.jtrac.domain.Space;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * excel import and cleanup
 */
public class ExcelImportPage extends BasePage {    
    
    private ExcelFile excelFile;    
    private int action;
    private Space space;

    public void setSpace(Space space) {
        this.space = space;
    }        
        
    public ExcelImportPage() {
        final FileUploadField fileUploadField = new FileUploadField("file");        
        Form uploadForm = new Form("uploadForm") {
            @Override
            public void onSubmit() {
                if(fileUploadField.getFileUpload() == null) {
                    return;
                }
                InputStream is = null;
                try {
                    is = fileUploadField.getFileUpload().getInputStream();                    
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
                excelFile = new ExcelFile(is);                
            }
            
            @Override
            public boolean isVisible() {
                return excelFile == null;
            }            
        };
        add(uploadForm);
        uploadForm.add(fileUploadField);        
        
        Form form = new Form("form") {             
            @Override
            public void onSubmit() { 
                if(action == 0) {
                    return;
                }
                switch(action) {
                    case 1: excelFile.deleteSelectedRowsAndColumns(); break;
                    case 2: excelFile.convertSelectedColumnsToDate(); break;
                    case 3: excelFile.concatenateSelectedColumns(); break;
                    case 4: excelFile.extractSummaryFromSelectedColumn(); break;
                }
                action = 0;
                excelFile.clearSelected();                
            }     
            
            @Override
            public boolean isVisible() {
                return excelFile != null;
            }               
        };          
        
        add(form);
        
        form.add(new Label("selectedSpace", new AbstractReadOnlyModel() {
            public Object getObject() {
                if(space == null) {
                    return localize("excel_view.noSpaceSelected");
                }
                return space.getName() + " [" + space.getPrefixCode() + "]";
            }
        }));
        
        form.add(new Link("selectSpace") {
            public void onClick() {
                setResponsePage(new ExcelImportSpacePage(ExcelImportPage.this));
            }            
        });        
        
        DropDownChoice actionChoice = new DropDownChoice("action", Arrays.asList(new Integer[] { 0, 1, 2, 3, 4}));
        actionChoice.setModel(new PropertyModel(this, "action"));        
        actionChoice.setChoiceRenderer(new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {                
                int i = (Integer) o;
                switch(i) {       
                    case 0: return "-- " + localize("excel_view.selectActionToPerform") + " --";
                    case 1: return localize("excel_view.deleteSelected");
                    case 2: return localize("excel_view.convertToDate");
                    case 3: return localize("excel_view.concatenateFields");
                    case 4: return localize("excel_view.extractFirstEighty");
                }
                return "";
            }
            public String getIdValue(Object o, int i) {
                return o.toString();
            }
        });   
        
        form.add(actionChoice);
        
        CheckGroup colsCheckGroup = new CheckGroup("colsCheckGroup", new PropertyModel(this, "excelFile.selectedColumns"));
        form.add(colsCheckGroup);        
        colsCheckGroup.add(new ColumnCheckboxes("checks"));
        
        form.add(new ColumnHeadings("headings"));
        
        CheckGroup rowsCheckGroup = new CheckGroup("rowsCheckGroup", new PropertyModel(this, "excelFile.selectedRows"));
        form.add(rowsCheckGroup);
        rowsCheckGroup.add(new RowsListView("rows"));
        
    }
    
    private class ColumnCheckboxes extends ReadOnlyRefreshingView {
        
        public ColumnCheckboxes(String id) {
            super(id);
        }
        
        public List getObjectList() {
            return excelFile.getColumns();
        }                
        
        protected void populateItem(Item item) {
            item.add(new Check("check", new PropertyModel(item, "index")));
        }
        
    }    
    
    private class ColumnHeadings extends ReadOnlyRefreshingView {
        
        public ColumnHeadings(String id) {
            super(id);
        }       
        
        public List getObjectList() {
            return excelFile.getColumns();
        }
        
        protected void populateItem(Item item) {            
            Column column = (Column) item.getModelObject(); 
            Link link = new Link("link") {
                public void onClick() {
                    
                }                
            };
            item.add(link);
            Label label = new Label("cell", new PropertyModel(column, "label"));
            label.setRenderBodyOnly(true);            
            link.add(label);            
        }
        
    }
    
    private class RowsListView extends ReadOnlyRefreshingView {
        
        final SimpleAttributeModifier alt = new SimpleAttributeModifier("class", "alt");
        
        public RowsListView(String id) {
            super(id);
        }
        
        public List getObjectList() {
            return excelFile.getRows();
        }                
        
        protected void populateItem(Item rowItem) {
            if(rowItem.getIndex() % 2 == 1) {
                rowItem.add(alt);
            }
            rowItem.add(new Check("check", new PropertyModel(rowItem, "index")));
            List<Cell> rowCells = (List<Cell>) rowItem.getModelObject();
            rowItem.add(new ListView("cols", rowCells) {
                protected void populateItem(ListItem colItem) {                    
                    Cell cell = (Cell) colItem.getModelObject();
                    Label label = new Label("cell", new PropertyModel(cell, "valueAsString"));
                    label.setRenderBodyOnly(true);
                    label.setEscapeModelStrings(false);
                    colItem.add(label);
                }
            });            
        }
        
    }
    
    private abstract class ReadOnlyRefreshingView<T> extends RefreshingView {                
        
        public ReadOnlyRefreshingView(String id) {
            super(id);            
        }
        
        public abstract List<T> getObjectList();        
        
        @Override
        protected Iterator getItemModels() {
            List<T> list = getObjectList();
            List<IModel> models = new ArrayList<IModel>(list.size());
            for(final T o : list) {
                models.add(new AbstractReadOnlyModel() {                    
                    public Object getObject() {
                        return o;
                    }
                });
            }
            return models.iterator();
        }        
        
    }
    
}
