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

import info.jtrac.Jtrac;
import info.jtrac.domain.Item;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Class to handle sending of E-mail and pre-formatted messages
 */
public class EmailUtils {
    
    private final Log logger = LogFactory.getLog(getClass());
    private Jtrac jtrac;
    private JavaMailSenderImpl sender;
    private String prefix;
    private String from;
    private String url;
    
    public EmailUtils(Jtrac jtrac) {
        this.jtrac = jtrac;
        String host = jtrac.loadConfig("mail.server.host");
        String port = jtrac.loadConfig("mail.server.port");
        if (host == null) {
            return;
        }
        prefix = jtrac.loadConfig("mail.subject.prefix");
        if (prefix == null) {
            prefix = "[jtrac]";
        }        
        from = jtrac.loadConfig("mail.from");
        if (from == null) {
            from = "jtrac";
        }
        url = jtrac.loadConfig("jtrac.url.base");
        if (url == null) {
            url = "http://localhost/jtrac/";
        }
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        int p = 25;
        if (port != null) {
           try {
               p = Integer.parseInt(port);
           } catch (NumberFormatException e) {
               logger.error("mail.server.port not an integer : '" + port + "'");
           }
        }
        sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(p);
    }

    private void sendInNewThread(final MimeMessage message) {
        new Thread(){
            public void run() {
                logger.debug("*** send mail thread start");
                try {
                    sender.send(message);
                    logger.debug("*** send mail thread successfull");
                } catch (Exception e) {
                    logger.error("*** send mail thread failed: " + e);                   
                }
            }
        }.start();
    }

    private String addHeaderAndFooter(StringBuffer html) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body><style type='text/css'> table.jtrac { border-collapse: collapse; font-family: Arial; font-size: 80% }");
        sb.append(" table.jtrac th, table.jtrac td { padding-left: 0.2em; padding-right: 0.2em; border: 1px solid black }");
        sb.append(" table.jtrac th, table.jtrac td.label { background: #CCCCCC } .alt { background: #DEDEFF }");
        sb.append(" .selected { background: #ADD8E6 } </style>");
        sb.append(html);
        sb.append("</html>");
        return sb.toString();
    }
    
    public void send(Item item) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        StringBuffer sb = new StringBuffer();       
    }
    
    
}
