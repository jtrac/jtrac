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

import info.jtrac.domain.Attachment;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utils that deal with Attachments, upload / download and
 * file name String manipulation
 */
public class AttachmentUtils {
    
    private static String getJtracHome() {
        return System.getProperty("jtrac.home");
    }
    
    public static String cleanFileName(String path) {
        // the client browser could be on Unix or Windows, we don't know
        int index = path.lastIndexOf('/');
        if (index == -1) {
            index = path.lastIndexOf('\\');
        }
        return (index != -1 ? path.substring(index + 1) : path);
    }
    
    public static File getNewFile(Attachment attachment) {
        return new File(getJtracHome() + "/attachments/" + attachment.getFilePrefix() + "_" + attachment.getFileName());
    }
    
    public static void download(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String fileName = URLDecoder.decode(cleanFileName(request.getRequestURI()), "UTF-8");
        String filePrefix = request.getParameter("filePrefix");
        File file = new File(getJtracHome() + "/attachments/" + filePrefix + "_" + fileName);        
        if (file.canRead()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(file);
                if (in != null) {
                    out = new BufferedOutputStream(response.getOutputStream());
                    in = new BufferedInputStream(in);
                    // first try to identify content type for better browser experience
                    String contentType = servletContext.getMimeType(fileName);
                    if (contentType == null) {
                        contentType = "application/octet-stream";
                    }
                    response.setContentType(contentType);
                    // Otherwise Firefox will offer only the first word as the default filename
                    String name = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                    // disposition as attachment should force a download, instead of using content type
                    // attempt to encode non-ascii characters to UTF-8 and force Firefox to 
                    // acknowledge the encoding (with the filename*=... trick)
                    response.setHeader("Content-Disposition", "attachment; filename*=" + name);
                    int c;
                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                    return;
                }
            } finally {
                in.close();
                out.close();
            }
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
}
