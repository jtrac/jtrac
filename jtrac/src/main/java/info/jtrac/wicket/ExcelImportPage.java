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

import info.jtrac.domain.ColumnHeading;
import info.jtrac.domain.ColumnHeading.Name;
import info.jtrac.domain.ExcelFile;
import info.jtrac.domain.ExcelFile.Cell;
import info.jtrac.domain.ExcelFile.Column;
import info.jtrac.domain.Space;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
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

    public Space getSpace() {
        return space;
    }

    public ExcelFile getExcelFile() {
        return excelFile;
    }                    
    
    public ExcelImportPage() {
        add(new FeedbackPanel("feedback"));
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
                    excelFile = new ExcelFile(is);
                } catch(Exception e) {
                    error(localize("excel_upload.error.invalidFile"));
                    return;
                }                                
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
                    error(localize("excel_view.error.noActionSelected"));
                    return;
                }                
                switch(action) {
                    case 1: // delete
                        if(!excelFile.isColumnSelected() && !excelFile.isRowSelected()) {
                            error(localize("excel_view.error.noColumnOrRowSelected"));
                            return;
                        }
                        excelFile.deleteSelectedRowsAndColumns(); 
                        break;
                    case 2: // convert to date
                        if(!excelFile.isColumnSelected()) {
                            error(localize("excel_view.error.noColumnSelected"));
                            return;
                        }
                        excelFile.convertSelectedColumnsToDate(); 
                        break;
                    case 3: // concatenate
                        if(excelFile.getSelectedColumns().size() < 2) {
                            error(localize("excel_view.error.atLeastTwoColumns"));
                            return;
                        }
                        excelFile.concatenateSelectedColumns(); 
                        break;
                    case 4: // extract summary into new column
                        if(!excelFile.isColumnSelected()) {
                            error(localize("excel_view.error.noColumnSelected"));
                            return;
                        }                        
                        excelFile.extractSummaryFromSelectedColumn(); 
                        break;
                    case 5: // duplicate column
                        if(!excelFile.isColumnSelected()) {
                            error(localize("excel_view.error.noColumnSelected"));
                            return;
                        }                        
                        excelFile.duplicateSelectedColumn(); 
                        break;                        
                    case 6: // map column
                        if(space == null) {
                            error(localize("excel_view.error.noSpaceSelected"));
                            return;                            
                        }                        
                        if(!excelFile.isColumnSelected()) {
                            error(localize("excel_view.error.noColumnSelected"));
                            return;
                        }
                        int colIndex = excelFile.getSelectedColumns().get(0);
                        setResponsePage(new ExcelImportColumnPage(ExcelImportPage.this, colIndex)); 
                        break;
                    case 7: // edit row
                        if (!excelFile.isRowSelected()) {
                            error(localize("excel_view.error.noRowSelected"));
                            return;
                        }
                        int rowIndex = excelFile.getSelectedRows().get(0);
                        setResponsePage(new ExcelImportRowPage(ExcelImportPage.this, rowIndex));
                        break;
                    case 8: // import !
                        if(space == null) {
                            error(localize("excel_view.error.noSpaceSelected"));
                            return;                            
                        }
                        Map<Name, String> labelsMap = BasePage.getLocalizedLabels(ExcelImportPage.this);
                        ColumnHeading duplicate = excelFile.getDuplicatedColumnHeadings();
                        if(duplicate != null) {
                            error(localize("excel_view.error.duplicateMapping", labelsMap.get(duplicate.getName())));
                            return;                              
                        }
                        List<ColumnHeading> unMapped = excelFile.getUnMappedColumnHeadings();
                        if(unMapped.size() > 0) {                            
                            for(ColumnHeading ch : unMapped) {
                                error(localize("excel_view.error.notMapped", labelsMap.get(ch.getName())));
                            }
                            return;
                        }                        
                        List<info.jtrac.domain.Item> items = excelFile.getAsItems(space);
                        getJtrac().storeItems(items);
                        info(localize("excel_view.importSuccess"));
                        setResponsePage(new ExcelImportPage());
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
        
        final Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        map.put(0, "excel_view.selectActionToPerform");
        map.put(1, "excel_view.deleteSelected");
        map.put(2, "excel_view.convertToDate");
        map.put(3, "excel_view.concatenateFields");
        map.put(4, "excel_view.extractFirstEighty");
        map.put(5, "excel_view.duplicateColumn");
        map.put(6, "excel_view.mapToField");
        map.put(7, "excel_view.editRow");
        map.put(8, "excel_view.import");
        
        DropDownChoice actionChoice = new DropDownChoice("action", new ArrayList(map.keySet()));
        actionChoice.setModel(new PropertyModel(this, "action"));        
        actionChoice.setChoiceRenderer(new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {                
                int i = (Integer) o;
                return localize(map.get(i));
            }
            public String getIdValue(Object o, int i) {
                return i + "";
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
    
    private static final SimpleAttributeModifier CLASS_SELECTED = new SimpleAttributeModifier("class", "selected");
    
    private class ColumnHeadings extends ReadOnlyRefreshingView {        
        
        public ColumnHeadings(String id) {
            super(id);
        }       
        
        public List getObjectList() {
            return excelFile.getColumns();
        }
        
        protected void populateItem(Item item) {            
            final Column column = (Column) item.getModelObject();
            if(column.getColumnHeading() != null) {
                item.add(CLASS_SELECTED);
            }
            Label label = new Label("cell", new PropertyModel(column, "label"));
            label.setRenderBodyOnly(true);            
            item.add(label);            
        }
        
    }
    
    private class RowsListView extends ReadOnlyRefreshingView {                
        
        public RowsListView(String id) {
            super(id);
        }
        
        public List getObjectList() {
            return excelFile.getRows();
        }                
        
        protected void populateItem(Item rowItem) {
            if(rowItem.getIndex() % 2 == 1) {
                rowItem.add(CLASS_ALT);
            }
            rowItem.add(new Check("check", new PropertyModel(rowItem, "index")));
            rowItem.add(new Label("index", rowItem.getIndex() + 1 + ""));
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
    
}
