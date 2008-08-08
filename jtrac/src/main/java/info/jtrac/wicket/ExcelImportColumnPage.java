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
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * choose space to import into
 */
public class ExcelImportColumnPage extends BasePage {                                                               
    
    private Column column;
    private Space space;
    private ExcelFile excelFile;
    private int index;
    private Map<String, IModel> mappedKeys;
    private Map<Object, String> mappedDisplayValues;
    private List<Cell> columnCells;        
    
    public ExcelImportColumnPage(final ExcelImportPage previous, final int index) {        
        
        add(new FeedbackPanel("feedback"));                
        
        space = previous.getSpace();
        excelFile = previous.getExcelFile();
        this.index = index;
        column = excelFile.getColumns().get(index).getClone();
        columnCells = excelFile.getColumnCellsCloned(index);
        final Map<Name, String> labelsMap = BasePage.getLocalizedLabels(this);           
        
        Form form = new Form("form");
        
        add(form);                        
        
        DropDownChoice columnChoice = new DropDownChoice("column");
        columnChoice.setChoices(new AbstractReadOnlyModel() {
            public Object getObject() {
                // avoid lazy init problem
                Space s = getJtrac().loadSpace(space.getId());
                List<ColumnHeading> list = ColumnHeading.getColumnHeadings(s);
                list.remove(new ColumnHeading(Name.ID));
                list.remove(new ColumnHeading(Name.SPACE));
                return list;
            }
        });                 
        columnChoice.setChoiceRenderer(new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                ColumnHeading ch = (ColumnHeading) o;
                if(ch.isField()) {
                    return ch.getLabel();
                }
                return labelsMap.get(ch.getName());
            }
            public String getIdValue(Object o, int i) {
                ColumnHeading ch = (ColumnHeading) o;
                return ch.getNameText();
            }
        });
        columnChoice.setModel(new PropertyModel(column, "columnHeading"));
        columnChoice.setNullValid(true);
        
        form.add(columnChoice);
        
        Button previewButton = new Button("preview") {
            @Override
            public void onSubmit() {                
                if(column.getColumnHeading() == null) {
                    return;
                }                
            }            
        };         
        
        form.add(previewButton);
        
        form.add(new Link("cancel") {
            public void onClick() {
                setResponsePage(previous);
            }
        });         
        
        final WebMarkupContainer columnCellsContainer = new WebMarkupContainer("columnCells") {
            @Override
            public boolean isVisible() {
                return column.getColumnHeading() != null;
            }
        };
        
        form.add(columnCellsContainer);
        
        final WebMarkupContainer distinctCellsContainer = new WebMarkupContainer("distinctCells") {
            @Override
            public boolean isVisible() {
                ColumnHeading ch = column.getColumnHeading();
                return ch != null && ch.isDropDownType();
            }
        };         
        
        columnCellsContainer.add(new Label("header", new PropertyModel(column, "label")));        
        columnCellsContainer.add(new ReadOnlyRefreshingView("rows") {            
            public List getObjectList() {
                return columnCells;
            }            
            protected void populateItem(Item item) {
                if(item.getIndex() % 2 == 1) {
                    item.add(CLASS_ALT);
                }           
                item.add(new Label("index", item.getIndex() + 1 + ""));
                Cell cell = (Cell) item.getModelObject();
                Label label = new Label("cell", new PropertyModel(cell, "valueAsString"));
                label.setEscapeModelStrings(false);                
                if(!cell.isValid(column.getColumnHeading())) {
                    label.add(CLASS_ERROR_BACK);
                }
                item.add(label);
                if(mappedDisplayValues != null && distinctCellsContainer.isVisible() && cell.getKey() != null) {
                    String mapped = mappedDisplayValues.get(cell.getKey());
                    item.add(new Label("mapped", mapped));
                } else {
                    item.add(new WebMarkupContainer("mapped"));                
                }
            }
        });
                
        form.add(distinctCellsContainer);
        
        distinctCellsContainer.add(new DistinctCellsView("rows"));
        
        Button updateButton = new Button("update") {
            @Override
            public void onSubmit() {
                if(distinctCellsContainer.isVisible()) {
                    ColumnHeading ch = column.getColumnHeading();
                    for(Cell cell : columnCells) {
                        IModel model = mappedKeys.get(cell.getValueAsString());
                        Object o = model.getObject();                        
                        cell.setKey(o);                        
                    }
                }               
            } 
            @Override
            public boolean isVisible() {
                return distinctCellsContainer.isVisible();
            }
        };
        
        form.add(updateButton);
        
        
        Button submitButton = new Button("submit") {
            @Override
            public void onSubmit() {
                ColumnHeading ch = column.getColumnHeading();
                for(Cell cell : columnCells) {
                    if(!cell.isValid(ch)) {
                        error(localize("excel_view.error.invalidValue"));
                        return;
                    }
                }                
                if(ch.isField()) {
                    column.setLabel(ch.getLabel());
                } else {
                    column.setLabel(labelsMap.get(column.getColumnHeading().getName()));
                }
                excelFile.getColumns().set(index, column);
                if(distinctCellsContainer.isVisible()) {
                    for(Cell cell : columnCells) {
                        cell.setValue(mappedDisplayValues.get(cell.getKey()));
                    }
                    excelFile.setColumnCells(index, columnCells);                    
                }
                setResponsePage(previous);
            } 
            @Override
            public boolean isVisible() {
                return columnCellsContainer.isVisible();
            }
        };
        
        form.add(submitButton);        
        
    }
    
    private class DistinctCellsView extends ReadOnlyRefreshingView {

        private IChoiceRenderer choiceRenderer;
        private IModel choicesModel;        
        
        public DistinctCellsView(String id) {
            super(id);
        }                        
        
        private void initChoices() {    
            // TODO reduce code duplication
            space = getJtrac().loadSpace(space.getId());
            ColumnHeading ch = column.getColumnHeading();
            if (ch.isField() || ch.getName() == ColumnHeading.Name.STATUS) {
                final Map<Integer, String> options;
                if(ch.isField()) {
                    options = ch.getField().getOptionsWithIntegerKeys();                            
                } else { // STATE
                    options = space.getMetadata().getStatesMap();
                    options.remove(State.NEW);
                }
                final List<Integer> keys;
                if (options != null) {
                    keys = new ArrayList(options.keySet());
                } else {
                    keys = new ArrayList<Integer>();
                }                                                
                choiceRenderer = new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        String value = options.get(o);
                        mappedDisplayValues.put(o, value);
                        return value;
                    }                    
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }                    
                };
                choicesModel = new AbstractReadOnlyModel() {
                    public Object getObject() {
                        return keys;
                    }
                };                                
            } else { // LOGGED_BY / ASSIGNED_TO             
                choiceRenderer = new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        String value = ((User) o).getName();
                        mappedDisplayValues.put(o, value);
                        return value;
                    }

                    public String getIdValue(Object o, int i) {
                        return ((User) o).getId() + "";
                    }
                };
                final List<User> users = getJtrac().findUsersForSpace(space.getId());
                choicesModel = new AbstractReadOnlyModel() {
                    public Object getObject() {
                        return users;
                    }
                };
            }            
        }

        public List getObjectList() {
            initChoices();
            if(mappedKeys == null) {
                mappedKeys = new HashMap<String, IModel>();
            }
            mappedDisplayValues = new HashMap<Object, String>();
            return excelFile.getColumnDistinctCellValues(index);
        }

        protected void populateItem(Item item) {
            if (item.getIndex() % 2 == 1) {
                item.add(CLASS_ALT);
            }
            String value = (String) item.getModelObject();
            Label label = new Label("cell", value);
            label.setEscapeModelStrings(false);
            item.add(label);
            IModel model = mappedKeys.get(value);
            if(model == null) {
                model = new Model(null);
                mappedKeys.put(value, model);
            }
            DropDownChoice choice = new DropDownChoice("key");
            choice.setChoiceRenderer(choiceRenderer);
            choice.setChoices(choicesModel);
            choice.setModel(model);
            choice.setNullValid(true);
            item.add(choice);
        }
    }
    
}
