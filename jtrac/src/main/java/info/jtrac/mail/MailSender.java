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

package info.jtrac.mail;

import info.jtrac.domain.Item;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.User;
import info.jtrac.util.ItemUtils;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.mail.Header;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

/**
 * Class to handle sending of E-mail and pre-formatted messages
 */
public class MailSender {
    
    private final Log logger = LogFactory.getLog(getClass());    
    private JavaMailSenderImpl sender;
    private String prefix;
    private String from;
    private String url;
    private MessageSource messageSource;
    private Locale defaultLocale;
    
    public MailSender(Map<String, String> config, MessageSource messageSource, String defaultLocale) {
        // initialize email sender
        this.messageSource = messageSource;
        this.defaultLocale = StringUtils.parseLocaleString(defaultLocale);
        String host = config.get("mail.server.host");
        if (host == null) {
            logger.warn("'mail.server.host' config is null, mail adapter not initialized");
            return;
        }        
        String port = config.get("mail.server.port");       
        String url = config.get("jtrac.url.base");
        from = config.get("mail.from");
        prefix = config.get("mail.subject.prefix");
        String userName = config.get("mail.server.username");
        String password = config.get("mail.server.password");
        String startTls = config.get("mail.server.starttls.enable");
        logger.debug("initializing email adapter: host = '" + host + "', port = '" + 
                port + "', url = '" + url + "', from = '" + from + "', prefix = '" + prefix + "'");        
        this.prefix = prefix == null ? "[jtrac]" : prefix;
        this.from = from == null ? "jtrac" : from;
        this.url = url == null ?  "http://localhost/jtrac/" : url;
        if (!this.url.endsWith("/")) {
            this.url = url + "/";
        }          
        int p = 25;
        if (port != null) {
           try {
               p = Integer.parseInt(port);
           } catch (NumberFormatException e) {
               logger.warn("mail.server.port not an integer : '" + port + "', defaulting to 25");
           }
        }
        sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(p);
        if (userName != null) {
            // authentication requested
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            if (startTls != null && startTls.toLowerCase().equals("true")) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            sender.setJavaMailProperties(props);
            sender.setUsername(userName);
            sender.setPassword(password);
        }
        logger.info("email sender initialized: host = '" + host + "', port = '" + p + "'");
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
                logger.debug("send mail thread start");
                try {
                    sender.send(message);
                    logger.debug("send mail thread successfull");
                } catch (Exception e) {
                    logger.error("send mail thread failed", e);
                    logger.error("mail headers dump start");
                    try {
                        Enumeration headers = message.getAllHeaders();
                        while(headers.hasMoreElements()) {
                            Header h = (Header) headers.nextElement();
                            logger.info(h.getName() + ": " + h.getValue());
                        }
                    } catch (Exception f) {
                        // :(
                    }
                    logger.error("mail headers dump end");
                }
            }
        }.start();
    }
    
    private String fmt(String key, Locale locale) {
        try {
            return messageSource.getMessage("mail_sender." + key, null, locale);
        } catch (Exception e) {
            logger.debug(e);
            return "???mail_sender." + key + "???";
        }
    }    

    private String addHeaderAndFooter(StringBuffer html) {
        StringBuffer sb = new StringBuffer();
        // additional cosmetic tweaking of e-mail layout
        // style just after the body tag does not work for a minority of clients like gmail, thunderbird etc.
        // ItemUtils adds the main inline CSS when generating the email content, so we gracefully degrade
        sb.append("<html><body><style type='text/css'>table.jtrac th, table.jtrac td { padding-left: 0.2em; padding-right: 0.2em; }</style>");
        sb.append(html);
        sb.append("<hr/></html>");
        return sb.toString();
    }
    
    private String getItemViewAnchor(Item item, Locale locale) {
        return "<p><a href='" + url + "flow/item_view?itemId=" + item.getId() + "'>" 
                + fmt("clickHereToAccess", locale) + " " + item.getRefId() + "</a></p>";
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
    
    public void send(Item item, MessageSource messageSource) {
        // TODO make this locale sensitive per recipient        
        logger.debug("attempting to send mail for item update");
        // prepare message content
        StringBuffer sb = new StringBuffer();
        String anchor = getItemViewAnchor(item, defaultLocale);
        sb.append(anchor);
        sb.append(ItemUtils.getAsHtml(item, messageSource, defaultLocale));
        sb.append(anchor);
        if (logger.isDebugEnabled()) {
            logger.debug("html content: " + sb);
        }
        // prepare message
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");        
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
            logger.error("failed to prepare e-mail", e);
        }              
    }
    
    public void sendUserPassword(User user, String clearText) {
        logger.debug("attempting to send mail for user password");
        String localeString = user.getLocale();
        Locale locale = null;
        if(localeString == null) {
            locale = defaultLocale;
        } else {
            locale = StringUtils.parseLocaleString(localeString);
        }
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(user.getEmail());
            helper.setSubject(prefix + " " + fmt("loginMailSubject", locale));
            StringBuffer sb = new StringBuffer();
            sb.append("<p>" + fmt("loginMailGreeting", locale) + " " + user.getName()+ ",</p>");      
            sb.append("<p>" + fmt("loginMailLine1", locale) + "</p>");           
            sb.append("<table class='jtrac'>");
            sb.append("<tr><th style='background: #CCCCCC'>" + fmt("loginName", locale) 
                + "</th><td style='border: 1px solid black'>" + user.getLoginName() + "</td></tr>");
            sb.append("<tr><th style='background: #CCCCCC'>" + fmt("password", locale) 
                + "</th><td style='border: 1px solid black'>" + clearText + "</td></tr>");
            sb.append("</table>");
            sb.append("<p>" + fmt("loginMailLine2", locale) + "</p>");       
            sb.append("<p><a href='" + url + "'>" + url + "</a></p>");
            helper.setText(addHeaderAndFooter(sb), true);
            helper.setSentDate(new Date());
            helper.setCc(from);
            helper.setFrom(from);
            sendInNewThread(message);
        } catch (Exception e) {
            logger.error("failed to prepare e-mail", e);
        }
    }
    
}
