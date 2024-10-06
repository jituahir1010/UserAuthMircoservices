package org.example.for_doc1.services;



import org.example.for_doc1.config.EmailUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendingEmail {

    @Async
    public String  sendit(String to, Integer otp, String names) {


        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jiturao998@gmail.com", "qwhkihlqkvhgoyoq");
            }
        };
        Session session = Session.getInstance(props, auth);
        String subject = "OTP Verification";
//        String body = "Your OTP is: " + otp;
        String body = String.format("Hello %s!, /n <h1>We welcome you, Kindly verify your OTP:- %o", names, otp );

        EmailUtil.sendEmail(session, to, subject, body);

        return  "Sending email bro form Jiturao998@gmail.com";
//        return null;
    }
}

