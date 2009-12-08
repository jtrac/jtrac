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
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

/**
 * Class to handle sending of E-mail and pre-formatted messages
 */
public class MailSender {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());    
    
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
        String mailSessionJndiName = config.get("mail.session.jndiname");
        if(StringUtils.hasText(mailSessionJndiName)) {
            initMailSenderFromJndi(mailSessionJndiName);
        }
        if(sender == null) {            
            initMailSenderFromConfig(config);
        }
        // if sender is still null the send* methods will not
        // do anything when called and will just return immediately
        String tempUrl = config.get("jtrac.url.base");
        if(tempUrl == null) {
            tempUrl = "http://localhost/jtrac/";
        }
        if (!tempUrl.endsWith("/")) {
            tempUrl = tempUrl + "/";
        }
        this.url = tempUrl;
        logger.info("email hyperlink base url set to '" + this.url + "'");
    }

    /**
     * we bend the rules a little and fire off a new thread for sending
     * an email message.  This has the advantage of not slowing down the item
     * create and update screens, i.e. the system returns the next screen
     * after "submit" without blocking.  This has been used in production
     * (and now I guess in many JTrac installations worldwide)
     * for quite a while now, on Tomcat without any problems.  This helps a lot
     * especially when the SMTP server is slow to respond, etc.
     */
    private void sendInNewThread(final MimeMessage message) {
        new Thread(){
            @Override
            public void run() {
                logger.debug("send mail thread start");
                try {
                    try {
                        sender.send(message);
                        logger.debug("send mail thread successfull");
                    } catch (Exception e) {
                        logger.error("send mail thread failed", e);
                        logger.error("mail headers dump start");                    
                        Enumeration headers = message.getAllHeaders();
                        while(headers.hasMoreElements()) {
                            Header h = (Header) headers.nextElement();
                            logger.info(h.getName() + ": " + h.getValue());
                        }
                        logger.error("mail headers dump end");
                    }
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
    
    private String fmt(String key, Locale locale) {
        try {
            return messageSource.getMessage("mail_sender." + key, null, locale);
        } catch (Exception e) {
            logger.debug(e.getMessage());
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
        sb.append("</html>");
        return sb.toString();
    }
    
    private String getItemViewAnchor(Item item, Locale locale) {
        String itemUrl = url + "app/item/" + item.getRefId();
        return "<p style='font-family: Arial; font-size: 75%'><a href='" + itemUrl + "'>" + itemUrl + "</a></p>";
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
        if (sender == null) {
            logger.debug("mail sender is null, not sending notifications");
            return;
        }
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
        
        // Remember the TO person email to prevent duplicate mails 
        String toPersonEmail;
        try {
            helper.setText(addHeaderAndFooter(sb), true);
            helper.setSubject(getSubject(item));
            helper.setSentDate(new Date());
            helper.setFrom(from);
            // set TO            
            if (item.getAssignedTo() != null) {
                helper.setTo(item.getAssignedTo().getEmail());
                toPersonEmail = item.getAssignedTo().getEmail();
            } else {
                helper.setTo(item.getLoggedBy().getEmail());
                toPersonEmail = item.getLoggedBy().getEmail();
            }
            // set CC
            if (item.getItemUsers() != null) {
                String[] cc = new String[item.getItemUsers().size()];
                int i = 0;
                for (ItemUser itemUser : item.getItemUsers()) {
                // Send only, if person is not the TO assignee	
                	if (! toPersonEmail.equals(itemUser.getUser().getEmail())) {
                        cc[i++] = itemUser.getUser().getEmail();
                	}
                }
                helper.setCc(cc);
            }
            // send message
            // workaround: Some PSEUDO user has no email address. Because email address
            // is mandatory, you can enter "no" in email address and the mail will not
            // be sent.
            if (! "no".equals(toPersonEmail))
                sendInNewThread(message);
        } catch (Exception e) {
            logger.error("failed to prepare e-mail", e);
        }              
    }
    
    public void sendUserPassword(User user, String clearText) {
        if (sender == null) {
            logger.debug("mail sender is null, not sending new user / password change notification");
            return;
        }        
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
            // helper.setCc(from);
            helper.setFrom(from);
            sendInNewThread(message);
        } catch (Exception e) {
            logger.error("failed to prepare e-mail", e);
        }
    }
    
    private void initMailSenderFromJndi(String mailSessionJndiName) {
        logger.info("attempting to initialize mail sender from jndi name = '" + mailSessionJndiName + "'");
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName(mailSessionJndiName);    
        // "java:comp/env/" will be prefixed if the JNDI name doesn't already have it
        factoryBean.setResourceRef(true);        
        try {
            // this step actually does the JNDI lookup
            factoryBean.afterPropertiesSet();
        } catch(Exception e) {
            logger.warn("failed to locate mail session : " + e);
            return;
        }
        Session session = (Session) factoryBean.getObject();
        sender = new JavaMailSenderImpl();
        sender.setSession(session);
        logger.info("email sender initialized from jndi name = '" + mailSessionJndiName + "'");        
    }
    
    private void initMailSenderFromConfig(Map<String, String> config) {
        String host = config.get("mail.server.host");
        if (host == null) {
            logger.warn("'mail.server.host' config is null, mail sender not initialized");
            return;
        }        
        String port = config.get("mail.server.port");               
        from = config.get("mail.from");
        prefix = config.get("mail.subject.prefix");
        String userName = config.get("mail.server.username");
        String password = config.get("mail.server.password");
        String startTls = config.get("mail.server.starttls.enable");
        logger.info("initializing email adapter: host = '" + host + "', port = '"
                + port + "', from = '" + from + "', prefix = '" + prefix + "'");        
        this.prefix = prefix == null ? "[jtrac]" : prefix;
        this.from = from == null ? "jtrac" : from;       
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
        logger.info("email sender initialized from config: host = '" + host + "', port = '" + p + "'");        
    }
    
}
