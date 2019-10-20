package com.crisisgo.libs.cmailkit;

import com.sun.mail.util.BASE64DecoderStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.*;

public class EmailParser {

    private static final Logger LOGGER = Logger.getLogger(EmailParser.class.getName());

    /**
     *
     * @param eml_file_path - a file of .eml type that will be parsed
     * @return - A email object with values corresponding to those from the .eml file
     */
    public Email parseEmail(String eml_file_path) {
        Email email = new Email();
        InputStream eml_file = null;
        try {
            eml_file = new FileInputStream(eml_file_path);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, ".eml file not found", e);
        }
        Properties props = new Properties();
        Session session =  Session.getInstance(props, null);
        try {
            MimeMessage message = new MimeMessage(session, eml_file);
            ArrayList<Contact> cc_list = new ArrayList<>();
            ArrayList<Contact> receivers = new ArrayList<>();
            ArrayList<Contact> bcc_list = new ArrayList<>();
            // get cc list
            getReceivers(message, cc_list, Message.RecipientType.CC);
            // get general receivers
            getReceivers(message, receivers, Message.RecipientType.TO);
            // get bcc list
            getReceivers(message, bcc_list, Message.RecipientType.BCC);
            email.setBccList(bcc_list); // set bcc list
            email.setReceiverList(receivers); // set general receivers
            email.setDate(message.getSentDate().toString()); // set receive date
            email.setCcList(cc_list); // set cc list
            // get and set both html and plain text parts of body
            EmailBody body = new EmailBody();
            try {
                body.addPlainTextBody(new MimeMessageParser(message).parse().getPlainContent());
            } catch (Exception e) {
                body.addPlainTextBody("");
                //LOGGER.log(Level.SEVERE, "Error getting plain text content from message");
            }
            try {
                body.addToHtmlBody(new MimeMessageParser(message).parse().getHtmlContent().trim());
            } catch (Exception e) {
                body.addToHtmlBody("");
                //LOGGER.log(Level.SEVERE, "Error getting html text content from message");
            }
            email.setBody(body); // set email body
            LOGGER.log(Level.FINE, "Plain body: " + email.getBody().getPlainTextBody());
            LOGGER.log(Level.FINE, "HTML body: " + email.getBody().getHtmlBody());
            email.setSenderEmail(message.getFrom()[0].toString()); // get email sender
            LOGGER.log(Level.FINE, "From: " + message.getFrom()[0].toString());
            email.setSubject(message.getSubject()); // set email subject line
            LOGGER.log(Level.FINE, "Subject: " + message.getSubject());
            email.setAttachments(parseAttachments(message)); // parse and set attachments
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error parsing .eml message", e);
            e.printStackTrace();
        }
        return email;
    }

    private void getReceivers(MimeMessage message, ArrayList<Contact> contactArrayList, Message.RecipientType recipientType) throws MessagingException {
        if (message.getRecipients(recipientType) != null) {
            for (int i = 0; i < message.getRecipients(recipientType).length; i++) {
                Contact temp = new Contact();
                int endNameIndex = message.getRecipients(recipientType)[i].toString().lastIndexOf("<");
                int length = message.getRecipients(recipientType)[i].toString().length();
                if (endNameIndex != -1) {
                    temp.setName(message.getRecipients(recipientType)[i].toString().substring(0, endNameIndex - 1));
                    temp.setEmail(message.getRecipients(recipientType)[i].toString().substring(endNameIndex + 1, length - 1));
                } else {
                    temp.setName(null);
                    temp.setEmail(message.getRecipients(recipientType)[i].toString());
                }
                contactArrayList.add(temp);
            }
        }
    }


    private ArrayList<Attachment> parseAttachments(Message message) {
        ArrayList<Attachment> attachments = new ArrayList<>();
        try {
            Object content = message.getContent();
            if (content instanceof Multipart) {
                Multipart multi = (Multipart) content;
                LOGGER.log(Level.FINE, "Multipart");
                for(int i=0;i<multi.getCount();i++) {
                    MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(i);
                    Attachment temp = new Attachment();
                    if(part.getFileName() != null) {
                        if (part.getFileName().contains(".")) {
                            int index = part.getFileName().lastIndexOf(".");
                            temp.setName(part.getFileName().substring(0, index));
                        } else {
                            temp.setName(part.getFileName());
                        }
                    }
                    LOGGER.log(Level.FINE, "Filename: " + part.getFileName());
                    if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {

                        if(part.getContentType().contains("text/plain")) {
                            byte[] byteArray = part.getContent().toString().getBytes();
                            temp.setByteArray(byteArray);
                        }

                        if(part.getContent() instanceof  BASE64DecoderStream) {
                            BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) part.getContent();
                            byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
                            /* byte[] encodeBase64 = Base64.encodeBase64(byteArray);
                            System.out.println("print base 64 string... " + new String(encodeBase64, "UTF-8")); */
                            temp.setByteArray(byteArray);
                        }
                        if(part.getFileName().contains(".")) {
                            temp.setFileExtension(part.getFileName().substring(part.getFileName().lastIndexOf(".")));
                        } else {
                            temp.setFileExtension(null);
                        }
                        LOGGER.log(Level.FINE, "Extension: " + temp.getFileExtension());
                        attachments.add(temp);
                    }
                }
            }
        } catch (IOException | MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error reading .eml message", e);
        }
        return attachments;
    }

    public void redirectLogger(String path) {
        Handler consoleHandler;
        Handler fileHandler;
        try {
            // creating handlers
            consoleHandler = new ConsoleHandler();
            fileHandler = new FileHandler(path);

            // adding handlers
            LOGGER.addHandler(consoleHandler);
            LOGGER.addHandler(fileHandler);

            // setting handler properties
            consoleHandler.setLevel(Level.ALL);
            fileHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);
            LOGGER.config("Finished moving logger to file: " + path);

            // remove console handler

        } catch(IOException e) {
            LOGGER.log(Level.SEVERE, "Error with logging file", e);
        }
    }

    public void resetLogger() {
        Handler consol = new ConsoleHandler();
        LOGGER.addHandler(consol);
        consol.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);
    }
}
