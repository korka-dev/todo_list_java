package org.post.email;

import org.post.config.Settings;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class EmailSender {


    public static void sendHtmlEmail(String toEmail, String subject, String htmlBody) {

        Settings settings = new Settings();

        // Set up the JavaMail session
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.host", settings.getSmtpHost());
        properties.put("mail.smtp.port", settings.getSmtpPort());

        // Create the session
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getSmtpUser(), settings.getSmtpPassword());
            }
        });

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the sender and recipient addresses
            message.setFrom(new InternetAddress(settings.getEmailSender()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            // Set the subject
            message.setSubject(subject);

            // Create the HTML part of the message
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlBody, "text/html");

            // Create a multipart message and add the HTML part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            // Set the content of the message
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

            System.out.println("Email envoyer avec succès !");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Errerur lors du envoie de mail : " + e.getMessage());
        }
    }

}
