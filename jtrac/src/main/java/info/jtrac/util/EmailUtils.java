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

import info.jtrac.domain.Item;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.User;
import java.util.Date;
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
    private JavaMailSenderImpl sender;
    private String prefix;
    private String from;
    private String url;
    
    public EmailUtils(String host, String port, String url, String from, String prefix) {
        if (prefix == null) {
            this.prefix = "[jtrac]";
        }         
        if (from == null) {
            this.from = "jtrac";
        }
        if (url == null) {
            this.url = "http://localhost/jtrac/";
        } else {
            if (!url.endsWith("/")) {
                this.url = url + "/";
            } else {
                this.url = url;
            }            
        }

        int p = 25;
        if (port != null) {
           try {
               p = Integer.parseInt(port);
           } catch (NumberFormatException e) {
               logger.error("mail.server.port not an integer : '" + port + "', defaulting to 25");
           }
        }
        sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(p);
    }

    /**
     * we bend the rules a little and fire off a new thread for sending
     * an email message.  This has the advantage of not slowing down the item
     * create and update screens, i.e. the system returns the next screen
     * after "submit" without blocking.  This has been used in production
     * for quite a while now, on Tomcat without any problems.  This helps a lot
     * especially when the SMTP server is slow to respond, etc.
     */
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
    
    private String getItemViewAnchor(Item item) {
        return "<p><a href='" + url + "flow.htm?_flowId=itemView&itemId=" + item.getId() + "'>Click here to access " + item.getRefId() + "</a></p>";
    }
    
    private String getSubject(Item item) {       
        String summary = null;
        if (item.getSummary() == null) {
            summary = "";
        } else if (item.getSummary().length() > 80) {
            summary = item.getSummary().substring(0, 80);
        } else {
            summary = item.getSummary();
        }
        return prefix + " #" + item.getRefId() + " " + summary;
    }
    
    public void send(Item item) {
        // prepare message content
        StringBuffer sb = new StringBuffer();
        String anchor = getItemViewAnchor(item);
        sb.append(anchor);
        sb.append(ItemUtils.getAsHtml(item, null));
        sb.append(anchor);
        // prepare message
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);        
        try {
            helper.setText(addHeaderAndFooter(sb), true);
            helper.setSubject(getSubject(item));
            helper.setSentDate(new Date());
            helper.setFrom(from);
            // set TO            
            if (item.getAssignedTo() != null) {
                helper.setTo(item.getAssignedTo().getEmail());
            } else {
                helper.setTo(item.getLoggedBy().getEmail());
            }
            // set CC
            if (item.getItemUsers() != null) {
                String[] cc = new String[item.getItemUsers().size()];
                int i = 0;
                for (ItemUser itemUser : item.getItemUsers()) {
                    cc[i++] = itemUser.getUser().getEmail();
                }
                helper.setCc(cc);
            }
            // send message
            sendInNewThread(message);
        } catch (Exception e) {
            logger.error("failed to prepare e-mail: " + e);
        }              
    }
    
    public void sendUserPassword(User user, boolean newUser) {        
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(user.getEmail());
            if (newUser) {
                helper.setSubject(prefix + " User ID created by Admin");
            } else {
                helper.setSubject(prefix + " Password reset by Admin");
            }
            StringBuffer sb = new StringBuffer();
            sb.append("<p>Hi " + user.getName()+ ",</p>");
            if (newUser) {
                sb.append("<p>A User ID has been created for you in JTrac.</p>");
            } else {
                sb.append("<p>Your JTrac password has been reset by the Administrator.</p>");
            }
            sb.append("<p>Your login details are as follows:</p>");
            sb.append("<table class='jtrac'>");
            sb.append("<tr><th>Login Name</th><td>" + user.getLoginName() + "</td></tr>");
            sb.append("<tr><th>Password</th><td>" + user.getPassword() + "</td></tr>");
            sb.append("</table>");
            sb.append("<p>Use the link below to log in:</p>");       
            sb.append("<p><a href='" + url + "'>" + url + "</a></p>");
            helper.setText(addHeaderAndFooter(sb), true);
            helper.setSentDate(new Date());
            helper.setCc(from);
            helper.setFrom(from);
            sendInNewThread(message);
        } catch (Exception e) {
            logger.error("failed to prepare e-mail: " + e);
        }
    }
    
}
