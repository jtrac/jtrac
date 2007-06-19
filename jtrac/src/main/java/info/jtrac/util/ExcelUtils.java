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

package info.jtrac.util;

import info.jtrac.domain.AbstractItem;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.ItemSearch;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.MessageSource;

/**
 * Excel Sheet generation helper
 */
public class ExcelUtils {
    
    private HSSFSheet sheet;
    private List<AbstractItem> items;
    private ItemSearch itemSearch;
    private HSSFCellStyle csBold;
    private HSSFCellStyle csDate; 
    private HSSFWorkbook wb;
    
    public ExcelUtils(List items, ItemSearch itemSearch) {
        this.wb = new HSSFWorkbook();
        this.sheet = wb.createSheet("jtrac");
        this.sheet.setDefaultColumnWidth((short) 12);
        
        HSSFFont fBold = wb.createFont();
        fBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        this.csBold = wb.createCellStyle();
        this.csBold.setFont(fBold);
        
        this.csDate = wb.createCellStyle();
        this.csDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));        
        
        this.items = items;
        this.itemSearch = itemSearch;
    }
    
    private static String fmt(String key, MessageSource messageSource, Locale locale) {
        try {
            return messageSource.getMessage("item_list." + key, null, locale);
        } catch (Exception e) {
            return "???item_list." + key + "???";
        }
    }    
    
    private HSSFCell getCell(int row, int col) {
        HSSFRow sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        HSSFCell cell = sheetRow.getCell((short) col);
        if (cell == null) {
            cell = sheetRow.createCell((short) col);
        }
        return cell;
    }         
    
    private void setHeader(int row, int col, String text) {
        HSSFCell cell = getCell(row, col);
        cell.setCellStyle(csBold);        
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(text);      
    }
    
    private void setText(int row, int col, String text) {
        HSSFCell cell = getCell(row, col);        
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(text);      
    }    
    
    private void setDate(int row, int col, Date date) {
        if (date == null) {
            return;
        }
        HSSFCell cell = getCell(row, col);
        cell.setCellValue(date);
        cell.setCellStyle(csDate);        
    }
    
    private void setDouble(int row, int col, Double value) {
        if (value == null) {
            return;
        }        
        HSSFCell cell = getCell(row, col);
        cell.setCellValue(value);          
    }    
    
    public HSSFWorkbook exportToExcel(MessageSource ms, Locale loc) {
                
        boolean showDetail = itemSearch.isShowDetail();
        boolean showHistory = itemSearch.isShowHistory();
        List<Field> fields = itemSearch.getFields();
                
        int row = 0;
        int col = 0;
        
        // begin header row
        setHeader(row, col++, fmt("id", ms, loc));
        setHeader(row, col++, fmt("summary", ms, loc));
                
        if (showDetail) {
            setHeader(row, col++, fmt("detail", ms, loc));
        }
        
        setHeader(row, col++, fmt("loggedBy", ms, loc));
        setHeader(row, col++, fmt("status", ms, loc));
        setHeader(row, col++, fmt("assignedTo", ms, loc));
        
        for(Field field : fields) {
            setHeader(row, col++, field.getLabel());            
        }
        
        setHeader(row, col++, fmt("timeStamp", ms, loc));        
        
        // iterate over list
        for(AbstractItem item : items) {
            row++; col = 0; 
            // begin data row
            setText(row, col++, item.getRefId());
            setText(row, col++, item.getSummary());
            
            if (showDetail) {
                if (showHistory) {
                    History h = (History) item;
                    if(h.getIndex() > 0) {
                        setText(row, col++, h.getComment());
                    } else {
                        setText(row, col++, h.getDetail());
                    }
                } else {
                    setText(row, col++, item.getDetail());
                }
            }
            
            setText(row, col++, item.getLoggedBy().getName());
            setText(row, col++, item.getStatusValue());
            setText(row, col++, (item.getAssignedTo() == null ? "" : item.getAssignedTo().getName()));
            
            for(Field field : fields) {                
                if (field.getName().getType() == 4) { // double
                    setDouble(row, col++, (Double) item.getValue(field.getName()));
                } else if (field.getName().getType() == 6) { // date
                    setDate(row, col++, (Date) item.getValue(field.getName()));
                } else {
                    setText(row, col++, item.getCustomValue(field.getName()));
                }               
            }
            
            setDate(row, col++, item.getTimeStamp());            
        }
        return wb;
    }
    
}
