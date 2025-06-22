package com.supportportal.supportportal.service;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import static com.supportportal.supportportal.constant.EmailConstant.*;


@Service
public class EmailService {

    public void sentNewPasswordEmail(String firstName, String password, String email) throws MessagingException {

        Message message = createEmail(firstName, password, email);
        Session session = getEmailSession();
        SMTPTransport smtpTransport = (SMTPTransport) session.getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);

        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmail(String firstName, String password, String email) throws MessagingException {

        Message message =  new MimeMessage(getEmailSession());

        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + ", \n \n " + "Your new account password is: " + password + "\n \n" + "The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

    private Session getEmailSession()  {

        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_SSL_ENABLE, true);

        // Disable certificate verification. FOR LOCAL TESTING ONLY!
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.socketFactory", sf);
        properties.put("mail.smtp.ssl.checkserveridentity", false); // disable identify verification
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // accepts smtp.gmail.com


        Session session = Session.getInstance(properties, null);
        session.setDebug(true);
        return session;
    }
}
