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

import java.io.Serializable;
import java.util.Date;

import static info.jtrac.domain.Field.Name.*;
import info.jtrac.util.DateUtils;
import java.util.Set;
import org.springmodules.lucene.index.core.DocumentCreator;

/**
 * Abstract class that serves as base for both Item and History
 * this contains the fields that are common to both and persisted
 */
public abstract class AbstractItem implements Serializable, DocumentCreator {    

    private long id;
    private int version;
    private Item parent; // slightly different meaning for Item and History
    private String summary;
    private String detail;
    private User loggedBy;
    private User assignedTo;
    private Date timeStamp;
    private Double plannedEffort;  
    //===========================
    private Integer status;
    private Integer severity;
    private Integer priority;
    private Integer cusInt01;
    private Integer cusInt02;
    private Integer cusInt03;
    private Integer cusInt04;
    private Integer cusInt05;
    private Integer cusInt06;
    private Integer cusInt07;
    private Integer cusInt08;
    private Integer cusInt09;
    private Integer cusInt10;
    private Double cusDbl01;
    private Double cusDbl02;
    private Double cusDbl03;
    private String cusStr01;
    private String cusStr02;
    private String cusStr03;
    private String cusStr04;
    private String cusStr05;
    private Date cusTim01;
    private Date cusTim02;
    private Date cusTim03;    
    
    // probably belong to Item not AbstractItem, but convenient for item_view_form binding
    private Set<ItemUser> itemUsers;
    private Set<ItemItem> relatedItems;
    private Set<ItemItem> relatingItems;
    private Set<ItemTag> itemTags;
    
    // mvc form binding convenience not really domain, TODO refactor
    private boolean sendNotifications = true;
    
    // we could have used reflection or a Map but doing this way for performance
    public Object getValue(Field.Name fieldName) {
        switch(fieldName) {
            case SEVERITY: return severity;
            case PRIORITY: return priority;
            case CUS_INT_01: return cusInt01;
            case CUS_INT_02: return cusInt02;
            case CUS_INT_03: return cusInt03;
            case CUS_INT_04: return cusInt04;
            case CUS_INT_05: return cusInt05;
            case CUS_INT_06: return cusInt06;
            case CUS_INT_07: return cusInt07;
            case CUS_INT_08: return cusInt08;
            case CUS_INT_09: return cusInt09;
            case CUS_INT_10: return cusInt10;
            case CUS_DBL_01: return cusDbl01;
            case CUS_DBL_02: return cusDbl02;
            case CUS_DBL_03: return cusDbl03;
            case CUS_STR_01: return cusStr01;
            case CUS_STR_02: return cusStr02;
            case CUS_STR_03: return cusStr03;
            case CUS_STR_04: return cusStr04;
            case CUS_STR_05: return cusStr05;
            case CUS_TIM_01: return cusTim01;
            case CUS_TIM_02: return cusTim02;
            case CUS_TIM_03: return cusTim03;
            default: return null; // this should never happen
        }
    }
    
    // we could have used reflection or a Map but doing this way for performance
    public void setValue(Field.Name fieldName, Object value) {
        switch(fieldName) {
            case SEVERITY: severity = (Integer) value; break;
            case PRIORITY: priority = (Integer) value; break;
            case CUS_INT_01: cusInt01 = (Integer) value; break;
            case CUS_INT_02: cusInt02 = (Integer) value; break;
            case CUS_INT_03: cusInt03 = (Integer) value; break;
            case CUS_INT_04: cusInt04 = (Integer) value; break;
            case CUS_INT_05: cusInt05 = (Integer) value; break;
            case CUS_INT_06: cusInt06 = (Integer) value; break;
            case CUS_INT_07: cusInt07 = (Integer) value; break;
            case CUS_INT_08: cusInt08 = (Integer) value; break;
            case CUS_INT_09: cusInt09 = (Integer) value; break;
            case CUS_INT_10: cusInt10 = (Integer) value; break;
            case CUS_DBL_01: cusDbl01 = (Double) value; break;
            case CUS_DBL_02: cusDbl02 = (Double) value; break;
            case CUS_DBL_03: cusDbl03 = (Double) value; break;
            case CUS_STR_01: cusStr01 = (String) value; break;
            case CUS_STR_02: cusStr02 = (String) value; break;
            case CUS_STR_03: cusStr03 = (String) value; break;
            case CUS_STR_04: cusStr04 = (String) value; break;
            case CUS_STR_05: cusStr05 = (String) value; break;
            case CUS_TIM_01: cusTim01 = (Date) value; break;
            case CUS_TIM_02: cusTim02 = (Date) value; break;
            case CUS_TIM_03: cusTim03 = (Date) value;
            default: // this should never happen
        }
    }    
    
    // must override, History behaves differently from Item
    public abstract Space getSpace();
    public abstract String getRefId();    
    
    public String getCustomValue(Field.Name fieldName) {
        // using accessor for space, getSpace() is overridden in subclass History
        if (fieldName.getType() <= 3) {            
            return getSpace().getMetadata().getCustomValue(fieldName, (Integer) getValue(fieldName));
        } else {
            Object o = getValue(fieldName);
            if (o == null) {
                return "";
            }
            if (o instanceof Date) {
                return DateUtils.format((Date) o); 
            }
            return o.toString();
        }
    }

    public String getStatusValue() {
        // using accessor for space, getSpace() is overridden in subclass History
        return getSpace().getMetadata().getStatusValue(status);
    }    
    
    //===================================================
    
    public Integer getStatus() {
        return status;
    }

    public Integer getSeverity() {
        return severity;
    }

    public Integer getPriority() {
        return priority;
    }

    public Integer getCusInt01() {
        return cusInt01;
    }

    public Integer getCusInt02() {
        return cusInt02;
    }

    public Integer getCusInt03() {
        return cusInt03;
    }

    public Integer getCusInt04() {
        return cusInt04;
    }

    public Integer getCusInt05() {
        return cusInt05;
    }

    public Integer getCusInt06() {
        return cusInt06;
    }

    public Integer getCusInt07() {
        return cusInt07;
    }

    public Integer getCusInt08() {
        return cusInt08;
    }

    public Integer getCusInt09() {
        return cusInt09;
    }

    public Integer getCusInt10() {
        return cusInt10;
    }

    public Double getCusDbl01() {
        return cusDbl01;
    }

    public Double getCusDbl02() {
        return cusDbl02;
    }

    public Double getCusDbl03() {
        return cusDbl03;
    }

    public String getCusStr01() {
        return cusStr01;
    }

    public String getCusStr02() {
        return cusStr02;
    }

    public String getCusStr03() {
        return cusStr03;
    }

    public String getCusStr04() {
        return cusStr04;
    }

    public String getCusStr05() {
        return cusStr05;
    }

    public Date getCusTim01() {
        return cusTim01;
    }

    public Date getCusTim02() {
        return cusTim02;
    }

    public Date getCusTim03() {
        return cusTim03;
    }

    //===============================================================

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setCusInt01(Integer cusInt01) {
        this.cusInt01 = cusInt01;
    }

    public void setCusInt02(Integer cusInt02) {
        this.cusInt02 = cusInt02;
    }

    public void setCusInt03(Integer cusInt03) {
        this.cusInt03 = cusInt03;
    }

    public void setCusInt04(Integer cusInt04) {
        this.cusInt04 = cusInt04;
    }

    public void setCusInt05(Integer cusInt05) {
        this.cusInt05 = cusInt05;
    }

    public void setCusInt06(Integer cusInt06) {
        this.cusInt06 = cusInt06;
    }

    public void setCusInt07(Integer cusInt07) {
        this.cusInt07 = cusInt07;
    }

    public void setCusInt08(Integer cusInt08) {
        this.cusInt08 = cusInt08;
    }

    public void setCusInt09(Integer cusInt09) {
        this.cusInt09 = cusInt09;
    }

    public void setCusInt10(Integer cusInt10) {
        this.cusInt10 = cusInt10;
    }

    public void setCusDbl01(Double cusDbl01) {
        this.cusDbl01 = cusDbl01;
    }

    public void setCusDbl02(Double cusDbl02) {
        this.cusDbl02 = cusDbl02;
    }

    public void setCusDbl03(Double cusDbl03) {
        this.cusDbl03 = cusDbl03;
    }

    public void setCusStr01(String cusStr01) {
        this.cusStr01 = cusStr01;
    }

    public void setCusStr02(String cusStr02) {
        this.cusStr02 = cusStr02;
    }

    public void setCusStr03(String cusStr03) {
        this.cusStr03 = cusStr03;
    }

    public void setCusStr04(String cusStr04) {
        this.cusStr04 = cusStr04;
    }

    public void setCusStr05(String cusStr05) {
        this.cusStr05 = cusStr05;
    }

    public void setCusTim01(Date cusTim01) {
        this.cusTim01 = cusTim01;
    }

    public void setCusTim02(Date cusTim02) {
        this.cusTim02 = cusTim02;
    }

    public void setCusTim03(Date cusTim03) {
        this.cusTim03 = cusTim03;
    }

    //=======================================================================

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }    

    public Item getParent() {
        return parent;
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public User getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(User loggedBy) {
        this.loggedBy = loggedBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getPlannedEffort() {
        return plannedEffort;
    }

    public void setPlannedEffort(Double plannedEffort) {
        this.plannedEffort = plannedEffort;
    }    
    
    public boolean isSendNotifications() {
        return sendNotifications;
    }

    public void setSendNotifications(boolean sendNotifications) {
        this.sendNotifications = sendNotifications;
    }    
    
    public Set<ItemUser> getItemUsers() {
        return itemUsers;
    }

    public void setItemUsers(Set<ItemUser> itemUsers) {
        this.itemUsers = itemUsers;
    }  
    
    public Set<ItemItem> getRelatedItems() {
        return relatedItems;
    }

    public void setRelatedItems(Set<ItemItem> relatedItems) {
        this.relatedItems = relatedItems;
    } 
    
    public Set<ItemItem> getRelatingItems() {
        return relatingItems;
    }

    public void setRelatingItems(Set<ItemItem> relatingItems) {
        this.relatingItems = relatingItems;
    }    
    
    public Set<ItemTag> getItemTags() {
        return itemTags;
    }

    public void setItemTags(Set<ItemTag> itemTags) {
        this.itemTags = itemTags;
    }    
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; parent [").append(parent == null ? "" : parent.getId());
        sb.append("]; summary [").append(summary);
        sb.append("]; detail [").append(detail);
        sb.append("]; loggedBy [").append(loggedBy);
        sb.append("]; status [").append(status);
        sb.append("]; assignedTo [").append(assignedTo);
        sb.append("]; timeStamp [").append(timeStamp);        
        sb.append("]; severity [").append(severity);
        sb.append("]; priority [").append(priority);
        sb.append("]; cusInt01 [").append(cusInt01);
        sb.append("]; cusInt02 [").append(cusInt02);
        sb.append("]; cusInt03 [").append(cusInt03);
        sb.append("]; cusInt04 [").append(cusInt04);
        sb.append("]; cusInt05 [").append(cusInt05);
        sb.append("]; cusInt06 [").append(cusInt06);
        sb.append("]; cusInt07 [").append(cusInt07);
        sb.append("]; cusInt08 [").append(cusInt08);
        sb.append("]; cusInt09 [").append(cusInt09);
        sb.append("]; cusInt10 [").append(cusInt10);
        sb.append("]; cusDbl01 [").append(cusDbl01);
        sb.append("]; cusDbl02 [").append(cusDbl02);
        sb.append("]; cusDbl03 [").append(cusDbl03);
        sb.append("]; cusStr01 [").append(cusStr01);
        sb.append("]; cusStr02 [").append(cusStr02);
        sb.append("]; cusStr03 [").append(cusStr03);
        sb.append("]; cusStr04 [").append(cusStr04);
        sb.append("]; cusStr05 [").append(cusStr05);
        sb.append("]; cusTim01 [").append(cusTim01);
        sb.append("]; cusTim02 [").append(cusTim02);
        sb.append("]; cusTim03 [").append(cusTim03);
        sb.append("]");
        return sb.toString();
    }

}
