package com.utility;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtility {

    public static void sendFailureMail(
            String statusMessage
    ) throws Exception {

        final String fromEmail =
                "jubin.thomas@diatoz.com";

        final String appPassword =
                "snia jgtn vrms dxys";

        String[] recipients = {
                "jubin.thomas@diatoz.com"
        };

        Properties properties =
                new Properties();

        properties.put(
                "mail.smtp.auth",
                "true"
        );

        properties.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        properties.put(
                "mail.smtp.host",
                "smtp.gmail.com"
        );

        properties.put(
                "mail.smtp.port",
                "587"
        );

        Session session =
                Session.getInstance(
                        properties,
                        new Authenticator() {

                            protected PasswordAuthentication
                            getPasswordAuthentication() {

                                return new PasswordAuthentication(
                                        fromEmail,
                                        appPassword
                                );
                            }
                        });

        Message message =
                new MimeMessage(session);

        message.setFrom(
                new InternetAddress(fromEmail)
        );

        InternetAddress[] addressList =
                new InternetAddress[recipients.length];

        for (int i = 0;
             i < recipients.length;
             i++) {

            addressList[i] =
                    new InternetAddress(
                            recipients[i]
                    );
        }

        message.setRecipients(
                Message.RecipientType.TO,
                addressList
        );

        message.setSubject(
                "PRODUCTION ALERT - APPLICATION DOWN"
        );

        message.setText(
                statusMessage
        );

        Transport.send(
                message
        );

        System.out.println(
                "FAILURE ALERT MAIL SENT SUCCESSFULLY"
        );
    }
}