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

package info.jtrac.domain;

import info.jtrac.domain.ColumnHeading.Name;
import static info.jtrac.domain.ColumnHeading.Name.*;
import info.jtrac.util.DateUtils;
import info.jtrac.util.ItemUtils;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that encapsulates an Excel Sheet / Workbook
 * and is used to process, cleanse and import contents of an 
 * uploaded excel file into JTrac
 */
public class ExcelFile implements Serializable {    
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelFile.class);    
    
    /**
     * represents a column heading and mapping to a Space built-in / custom field
     */
    public class Column implements Serializable {        
        
        private String label;
        private ColumnHeading columnHeading;        
        
        public Column(String label) {            
            this.label = label;
        }

        public String getLabel() {
            return label;
        }        
        
        public void setLabel(String label) {
            this.label = label;
        }        
        
        public void setColumnHeading(ColumnHeading columnHeading) {
            this.columnHeading = columnHeading;
        }       
        
        public ColumnHeading getColumnHeading() {
            return columnHeading;
        } 
        
        public Column getClone() {
            Column column = new Column(label);
            column.setColumnHeading(columnHeading);
            return column;
        }
        
    }
    
    /**
     * represents a cell value, acts as object holder
     */    
    public class Cell implements Serializable {
        
        private Object value;
        // internal key value for cells mapped to drop downs
        private Object key;

        public Cell(Object value) {
            this.value = value;
        }

        private boolean isEmpty() {
            return value == null || value.toString().trim().length() == 0;
        }
        
        public boolean isValid(ColumnHeading ch) {
            if(ch.isField()) {
                switch(ch.getField().getName().getType()) {
                    case 1:
                    case 2:
                    case 3:
                        if(key == null || key instanceof String) {
                            return true;
                        }
                        break;
                    case 4: 
                        if(value == null || value instanceof Double) {
                            return true;
                        }
                        break;
                    case 5:
                        return true;                        
                    case 6:
                        if(value == null || value instanceof Date) {
                            return true;
                        }
                        break;
                }
                
            } else {
                switch(ch.getName()) {
                    case SUMMARY:
                    case DETAIL:
                        if(!isEmpty()) {
                            return true;
                        }
                        break;
                    case LOGGED_BY:
                    case ASSIGNED_TO:
                        if(key != null && key instanceof User) {
                            return true;
                        }
                        break;
                    case STATUS:
                        if(key != null && key instanceof Integer) {
                            return true;
                        }
                        break;
                    case TIME_STAMP:
                        if(value == null || value instanceof Date) {
                            return true;
                        }
                        break;
                }
            }
            return false;
        }
        
        public void setValue(Object value) {
            this.value = value;
        }                

        public void setKey(Object key) {
            this.key = key;
        }

        public Object getKey() {
            return key;
        }                
                
        public String getValueAsString() {
            if (value == null) {
                return "";
            }
            if (value instanceof String) {
                return ItemUtils.fixWhiteSpace((String) value);
            }
            if(value instanceof Date) {
                return DateUtils.formatTimeStamp((Date) value);
            }
            return value.toString();
        }
        
        public Cell getClone() {
            Cell cell = new Cell(value);
            cell.setKey(key);
            return cell;
        }
        
    }
    
    //==========================================================================
    // grid data
    
    private List<Column> columns;
    private List<List<Cell>> rows;

    public List<List<Cell>> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }
            
    //==========================================================================
    // form binding
    
    private List<Integer> selectedColumns = new ArrayList<Integer>();
    private List<Integer> selectedRows = new ArrayList<Integer>();

    public List<Integer> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(List<Integer> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }   

    public List<Integer> getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(List<Integer> selectedRows) {
        this.selectedRows = selectedRows;
    }
    
    //==========================================================================
    // operations
    
    public boolean isColumnSelected() {
        return selectedColumns.size() > 0;
    }
    
    public boolean isRowSelected() {
        return selectedRows.size() > 0;
    }    
    
    public Column getFirstSelectedColumn() {
        if(selectedColumns.size() == 0) {
            return null;
        }
        int index = selectedColumns.get(0);
        return columns.get(index);
    }
    
    public List<Cell> getFirstSelectedRow() {
        if(selectedRows.size() == 0) {
            return null;
        }
        int index = selectedRows.get(0);
        return rows.get(index);        
    }
    
    public void clearSelected() {
        selectedColumns = new ArrayList<Integer>();
        selectedRows = new ArrayList<Integer>();
    }    
    
    public List<ColumnHeading> getMappedColumnHeadings() {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>();
        for(Column c : columns) {
            if(c.columnHeading != null) {
                list.add(c.columnHeading);
            }
        }
        return list;
    }
    
    public ColumnHeading getDuplicatedColumnHeadings() {
        Set<ColumnHeading> set = new HashSet<ColumnHeading>();
        for(ColumnHeading ch : getMappedColumnHeadings()) {
            if(set.contains(ch)) {
                return ch;
            }
            set.add(ch);
        }
        return null;
    }
    
    public List<ColumnHeading> getUnMappedColumnHeadings() {
        // status will default to Open
        // timestamp will default to now
        // custom field mandatory check will be ignored if any
        // the following 4 are the only MANDATORY fields for import
        Set<ColumnHeading> set = new HashSet<ColumnHeading>();        
        ColumnHeading summary = new ColumnHeading(Name.SUMMARY);
        ColumnHeading detail = new ColumnHeading(Name.DETAIL);
        ColumnHeading loggedBy = new ColumnHeading(Name.LOGGED_BY);
        ColumnHeading assignedTo = new ColumnHeading(Name.ASSIGNED_TO);
        set.add(summary);
        set.add(detail);
        set.add(loggedBy);
        set.add(assignedTo);
        set.removeAll(getMappedColumnHeadings());
        return new ArrayList(set);
    }
    
    public List<Cell> getColumnCells(int index) {
        List<Cell> list = new ArrayList<Cell>(rows.size());
        for(List<Cell> rowCells : rows) {
            list.add(rowCells.get(index));
        }
        return list;
    }
    
    public List<Cell> getColumnCellsCloned(int index) {
        List<Cell> list = new ArrayList<Cell>(rows.size());
        for(List<Cell> rowCells : rows) {
            list.add(rowCells.get(index).getClone());
        }
        return list;        
    }
    
    public void setColumnCells(int index, List<Cell> columnCells) {
        int count = 0;
        for(List<Cell> rowCells : rows) {
            rowCells.set(index, columnCells.get(count));
            count++;
        }
    }
    
    public List<String> getColumnDistinctCellValues(int index) {
        Set<String> set = new TreeSet<String>();
        for(List<Cell> rowCells : rows) {
            set.add(rowCells.get(index).getValueAsString());
        }
        return new ArrayList(set);
    }
    
    public List<Item> getAsItems(Space s) {
        List<Item> items = new ArrayList<Item>(rows.size());
        for(List<Cell> rowCells : rows) {
            Item item = new Item();
            item.setSpace(s);
            for(int i = 0; i < columns.size(); i++) {
                ColumnHeading ch = columns.get(i).columnHeading;
                if(ch == null) {
                    continue;
                }
                Cell cell = rowCells.get(i);
                if(ch.isField()) {                    
                    Field field = ch.getField();
                    if(field.isDropDownType()) {
                        if(cell.key != null) {
                            item.setValue(field.getName(), cell.key);
                        }
                    } else {
                        if(cell.value != null) {
                            item.setValue(field.getName(), cell.value);
                        }
                    }
                } else {                    
                    switch(ch.getName()) {
                        // next 4 are the only MANDATORY fields in import                        
                        case SUMMARY:                            
                            item.setSummary(cell.value.toString());
                            break;
                        case DETAIL: 
                            item.setDetail(cell.value.toString());
                            break;
                        case LOGGED_BY: 
                            item.setLoggedBy((User) cell.key);
                            break;
                        case ASSIGNED_TO: 
                            item.setAssignedTo((User) cell.key);
                            break;
                        case STATUS:
                            if(cell.key != null) {
                                item.setStatus((Integer) cell.key);
                            }
                            break;
                        case TIME_STAMP:
                            // timestamp will be set by JtracImpl if null
                            if(cell.value != null) {
                                item.setTimeStamp((Date) cell.value);
                            }
                            break;
                    }
                }              
            }
            // if no status, assume Open
            if(item.getStatus() == null) {
                item.setStatus(State.OPEN);
            }
            items.add(item);
        } 
        return items;
    }
    
    //==========================================================================
    // edits
        
    public void deleteSelectedRowsAndColumns() {
        int cursor = 0;        
        for(int i : selectedRows) {
            rows.remove(i - cursor);
            cursor++;
        }        
        cursor = 0;        
        for(int i : selectedColumns) {
            columns.remove(i - cursor);
            for(List<Cell> cells : rows) {                
                cells.remove(i - cursor);
            }
            cursor++;
        }        
    }
    
    public void convertSelectedColumnsToDate() {
        // could not find a better way to convert excel number to date
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell((short) 0);
        for(int i : selectedColumns) {
            for(List<Cell> cells : rows) {
                Cell c = cells.get(i);                
                if (c != null && c.value instanceof Double) {                    
                    cell.setCellValue((Double) c.value);                    
                    c.value = cell.getDateCellValue();
                }
            }            
        }        
    }
    
    public void concatenateSelectedColumns() {
        List<Cell> list = new ArrayList<Cell>(rows.size());
        for(List<Cell> rowCells : rows) {
            list.add(new Cell(null));
        }
        int first = selectedColumns.get(0);
        for(int i : selectedColumns) {
            int rowIndex = 0;
            for(List<Cell> cells : rows) {
                Cell c = cells.get(i);                
                if (c != null) {
                    String s = (String) list.get(rowIndex).value;                    
                    if (s == null) {
                        s = (String) c.value;                        
                    } else {
                        s += "\n\n" + c.value;
                    }                    
                    list.set(rowIndex, new Cell(s));
                }
                rowIndex++;
            }            
        }
        // update the first column
        int rowIndex = 0;
        for(List<Cell> rowCells : rows) {
            rowCells.set(first, list.get(rowIndex));
            rowIndex++;
        }
    }
    
    public void extractSummaryFromSelectedColumn() {
        int first = selectedColumns.get(0);           
        for(List<Cell> cells : rows) {
            Cell c = cells.get(first);                
            if (c != null && c.value != null) {
                String s = c.value.toString();
                if (s.length() > 80) {
                    s = s.substring(0, 80);
                }
                cells.add(first, new Cell(s));                
            } else {
                cells.add(first, new Cell(null));
            }         
        }         
        columns.add(first, new Column("---"));   
    }
    
    public void duplicateSelectedColumn() {
        int first = selectedColumns.get(0);
        for(List<Cell> cells : rows) {
            Cell c = cells.get(first);
            if (c != null && c.value != null) {
                Cell clone = c.getClone();
                cells.add(first, clone);
            } else {
                cells.add(first, new Cell(null));
            }
        }
        columns.add(first, new Column("---"));
    }
    
    //========================================================================== 
    
    public ExcelFile(InputStream is) {
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        try {
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow r = null;
        HSSFCell c = null;
        int row = 0;
        int col = 0;
        columns = new ArrayList<Column>();
        //========================== HEADER ====================================
        // column headings are important, this routine assumes that the first
        // row is a header row and that reaching an empty cell means end of data
        r = sheet.getRow(row);       
        while(true) {            
            c = r.getCell((short) col);
            if (c == null) {          
                break;
            }
            String value = c.getStringCellValue();
            if (value == null || value.trim().length() == 0) {
                break;
            }
            Column column = new Column(value.trim());
            columns.add(column);
            col++;
        }
        //============================ DATA ====================================
        rows = new ArrayList<List<Cell>>();
        while(true) {
            row++;            
            r = sheet.getRow(row);
            if (r == null) {
                break;
            }
            List rowData = new ArrayList(columns.size());
            boolean isEmptyRow = true;
            for(col = 0; col < columns.size(); col++) {
                c = r.getCell((short) col);
                Object value = null;
                switch(c.getCellType()) {
                    case(HSSFCell.CELL_TYPE_STRING) : value = c.getStringCellValue(); break;
                    case(HSSFCell.CELL_TYPE_NUMERIC) :
                        // value = c.getDateCellValue();
                        value = c.getNumericCellValue(); 
                        break;
                    case(HSSFCell.CELL_TYPE_BLANK) : break;
                    default: // do nothing
                }
                if (value != null && value.toString().length() > 0) {
                    isEmptyRow = false;
                    rowData.add(new Cell(value));
                } else {
                    rowData.add(new Cell(null));
                }
            }
            if(isEmptyRow) {
                break;
            }
            rows.add(rowData);
        }
    }
    
}
