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

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Class that encapsulates an Excel Sheet / Workbook
 * and is used to process, cleanse and import contents of an 
 * uploaded excel file into JTrac
 */
public class ExcelFile implements Serializable {    
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private List<String> labels;
    private List<List> rows;

    public List<List> getRows() {
        return rows;
    }

    public List<String> getLabels() {
        return labels;
    }
    
    //==========================================================================
    // form binding stuff
    
    private int[] selCols;
    private int[] selRows;
     
    public int[] getSelCols() {
        return selCols;
    }

    public void setSelCols(int[] selCols) {
        this.selCols = selCols;
    }

    public int[] getSelRows() {
        return selRows;
    }

    public void setSelRows(int[] selRows) {
        this.selRows = selRows;
    }    
    
    //==========================================================================
    // edits
    
    public void delete() {
        int cursor = 0;
        if (selRows != null) {
            for(int i : selRows) {
                rows.remove(i - cursor);
                cursor++;
            }
        }
        cursor = 0;
        if (selCols != null) {
            for(int i : selCols) {
                labels.remove(i - cursor);
                for(List rowData : rows) {                
                    rowData.remove(i - cursor);
                }
                cursor++;
            }
        }
    }
    
    //==========================================================================
    
    public ExcelFile() {
        // zero arg constructor
    }    
    
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
        labels = new ArrayList<String>();
        //========================== HEADER ====================================
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
            labels.add(value.trim());
            col++;
        }
        //============================ DATA ====================================
        rows = new ArrayList<List>();
        while(true) {
            row++;            
            r = sheet.getRow(row);
            if (r == null) {
                break;
            }
            List rowData = new ArrayList(labels.size());
            boolean isEmptyRow = true;
            for(col = 0; col < labels.size(); col++) {
                c = r.getCell((short) col);
                String value = null;
                switch(c.getCellType()) {
                    case(HSSFCell.CELL_TYPE_STRING) : value = c.getStringCellValue(); break;
                    case(HSSFCell.CELL_TYPE_NUMERIC) : value = c.getNumericCellValue() + ""; break;
                    case(HSSFCell.CELL_TYPE_BLANK) : break;
                }
                if (value != null && value.trim().length() > 0) {
                    isEmptyRow = false;
                    rowData.add(value.trim());
                } else {
                    rowData.add(null);
                }
            }
            if(isEmptyRow) {
                break;
            }
            rows.add(rowData);
        }
    }
    
}
