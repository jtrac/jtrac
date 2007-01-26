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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Formatting helper, currently date formats are hard-coded for the entire app
 * hence the use of static SimpleDateFormat instances, although they are known not to be synchronized
 */
public class DateUtils {
    
    private static Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static Format dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static String format(Date date) {
        return date == null ? "" : dateFormat.format(date);
    }

    public static String formatTimeStamp(Date date) {
        return date == null ? "" : dateTimeFormat.format(date);
    }
    
}
