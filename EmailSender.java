package com.crisisgo.libs.cmailkit;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSender {

    private static final Logger LOGGER = Logger.getLogger(EmailSender.class.getName());
    private String smtp_server_host;
    private String smtp_server_port;
    private String username;
    private String password;
    private String sender_name;
    private String prefix;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSenderName(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setSmtpServerHost(String smtp_server_host) {
        this.smtp_server_host = smtp_server_host;
    }

    public void setSmtpServerPort(String smtp_server_port) {
        this.smtp_server_port = smtp_server_port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @param e - email object whose values will be used in creating the email that will be sent.
     */
    public void send(Email e) {
        Properties properties = new Properties();
        properties.setProperty("mail.debug", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", smtp_server_host);
        properties.put("mail.smtp.port", smtp_server_port);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.ssl.enable", "true");

        setSocketFactory(properties);

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        MimeMessage msg = new MimeMessage(session);

        String subject = e.getSubject();
        if(prefix != null) {
            subject = prefix + " " + subject;
        }

        try {
            msg.setSubject(subject);
            Multipart multipart = new MimeMultipart();

            if(!e.getBody().getHtmlBody().equals("")) {
                LOGGER.log(Level.FINE, "Html body");
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(e.getBody().getHtmlBody(), null, "html");
                multipart.addBodyPart(messageBodyPart);
            } else if(!e.getBody().getPlainTextBody().equals("")) {
                LOGGER.log(Level.FINE, "Plain text body");
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(e.getBody().getPlainTextBody());
                multipart.addBodyPart(messageBodyPart);
            }

            if(sender_name != null) {
                msg.setFrom(new InternetAddress(username, sender_name));
            } else {
                msg.setFrom(new InternetAddress(username));
            }
            if(e.getAttachments().size() != 0) {
                setAttachments(multipart, e.getAttachments());
            }
            setReceiverList(msg, e.getReceiverList());
            setCCList(msg, e.getCcList());
            setBccList(msg, e.getBcc_list());
            msg.setContent(multipart);
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException me) {
            LOGGER.log(Level.SEVERE, "Error setting message information", me);
        }
    }

    /**
     *
     * @param ex -  a EmailEx type object whose valuse will be used to fill in the email that will be sent.
     *              Provides more options/style choices than the Email object in allowing for greeter and signature.
     */
    public void send(EmailEx ex) {
        Properties properties = new Properties();
        properties.setProperty("mail.debug", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", smtp_server_host);
        properties.put("mail.smtp.port", smtp_server_port);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.ssl.enable", "true");

        setSocketFactory(properties);

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        MimeMessage msg = new MimeMessage(session);

        String subject = ex.getSubject();
        if(prefix != null) {
            subject = prefix + " " + subject;
        }

        try {
            msg.setFrom(new InternetAddress(username, sender_name));
            Multipart multipart = new MimeMultipart();
            String body = "";
            msg.setSubject(subject);
            BodyPart messageBodyPart = new MimeBodyPart();
            if(ex.getGreeting() != null) {
                body = body.concat(ex.getGreeting() + "\n\n");
            }
            if(ex.getMainText() != null) {
                body = body.concat(ex.getMainText() + "\n\n");
            }
            if(ex.getSignature()!= null) {
                body = body.concat(ex.getSignature() + "\n");
            }
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);
            if(ex.getSignatureLogoPath() != null) {
                BodyPart htmlShort = new MimeBodyPart();
                htmlShort.setContent("<img src=\"cid:image\">", "text/html");
                multipart.addBodyPart(htmlShort);
                BodyPart msgLogo = new MimeBodyPart();
                DataSource logo = new FileDataSource(ex.getSignatureLogoPath());
                msgLogo.setDataHandler(new DataHandler(logo));
                msgLogo.setHeader("Content-id", "<image>");
                multipart.addBodyPart(msgLogo);
            }

            setReceiverList(msg, ex.getReceiverList());
            setCCList(msg, ex.getCcList());
            setBccList(msg, ex.getBccList());

            if(ex.getAttachments().size() != 0) {
                setAttachments(multipart, ex.getAttachments());
            }

            msg.setContent(multipart);
            Transport.send(msg);

        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Error setting email attributes", e);
        }
    }

    /**
     *
     * @param ex - email ex object whose values will be used
     * @param email_template_path -  a simple .txt file with optional fields ([greeting], [main text], [signature], and
     *                               [logo]) the fields will be filled in with the values of the EmailEx instance. Allows
     *                               for greater customization in email format and styling.
     */
    public void send(EmailEx ex, String email_template_path) {
        Properties properties = new Properties();
        properties.setProperty("mail.debug", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", smtp_server_host);
        properties.put("mail.smtp.port", smtp_server_port);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.ssl.enable", "true");
        setSocketFactory(properties);

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        MimeMessage msg = new MimeMessage(session);

        String subject = ex.getSubject();
        if(prefix != null) {
            subject = prefix + " " + subject;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(email_template_path)));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Formatting file not found", e);
        }

        Multipart multipart = new MimeMultipart();
        String st;
        try {
            while ((st = br.readLine()) != null) {
                String body = "";
                BodyPart messageBodyPart = new MimeBodyPart();
                if(st.contains("[greeting]")) {
                    if(ex.getGreeting() == null) {
                        LOGGER.log(Level.SEVERE, "[Greeting] in format file but value not found in class");
                    }
                    body = body.concat(ex.getGreeting());
                    body = body.concat("\n");
                }
                if(st.contains("[main text]")) {
                    if(ex.getMainText() == null) {
                        LOGGER.log(Level.SEVERE, "[Main Text] in format file but value not found in class");
                    }
                    body = body.concat(ex.getMainText());
                    body = body.concat("\n");
                }
                if(st.contains("[signature]")) {
                    if(ex.getSignature() == null) {
                        LOGGER.log(Level.SEVERE, "[Signature] in format file but value not found in class");
                    }
                    body = body.concat(ex.getSignature());
                    body = body.concat("\n");
                }
                if(!body.equals("")) {
                    messageBodyPart.setText(body);
                    multipart.addBodyPart(messageBodyPart);
                }
                if(st.contains("[logo]")) {
                    BodyPart htmlBP = new MimeBodyPart();
                    htmlBP.setContent("<img src=\"cid:image\">", "text/html");
                    multipart.addBodyPart(htmlBP);
                    BodyPart msgLogo = new MimeBodyPart();
                    if(ex.getSignatureLogoPath() == null) {
                        LOGGER.log(Level.SEVERE, "[Logo] found but no path in class");
                    }
                    DataSource logo = new FileDataSource(ex.getSignatureLogoPath());
                    msgLogo.setDataHandler(new DataHandler(logo));
                    msgLogo.setHeader("Content-id", "<image>");
                    multipart.addBodyPart(msgLogo);

                }
                if(st.equals("") || st.equals("\n") || st.equals("\r") || st.equals("\r\n")) {
                    LOGGER.log(Level.FINE, "its a new line");
                    BodyPart newLineBP = new MimeBodyPart();
                    newLineBP.setText("\n");
                    multipart.addBodyPart(newLineBP);
                }
            }
        } catch (IOException | MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error reading template file", e);
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "File is empty/corrupt", e);
        }

        try {
            msg.setFrom(new InternetAddress(username, sender_name));
            msg.setSubject(subject);
            setReceiverList(msg, ex.getReceiverList());
            setCCList(msg, ex.getCcList());
            setBccList(msg, ex.getBccList());

            if(ex.getAttachments().size() != 0) {
                setAttachments(multipart, ex.getAttachments());
            }
            msg.setContent(multipart);
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Error setting email", e);
        }
    }

    private void setAttachments(Multipart multipart, ArrayList<Attachment> attachments) throws MessagingException {
        if(attachments != null) {
            for (Attachment attachment : attachments) {
                BodyPart msgAttachment = new MimeBodyPart();
                String filename = attachment.getName() + attachment.getFileExtension();
                if (attachment.getPath() != null) {
                    DataSource source = new FileDataSource(attachment.getPath());
                    msgAttachment.setDataHandler(new DataHandler(source));
                }
                msgAttachment.setFileName(filename);
                multipart.addBodyPart(msgAttachment);
            }
        }
    }

    private void setReceiverList(MimeMessage msg, ArrayList<Contact> receiver_list) throws MessagingException {
        if(receiver_list != null) {
            InternetAddress[] toAddress = new InternetAddress[receiver_list.size()];
            for(int i=0;i<toAddress.length;i++) {
                toAddress[i] = new InternetAddress(receiver_list.get(i).getEmail());
                msg.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
        }
    }

    private void setCCList(MimeMessage msg, ArrayList<Contact> cc_list) throws MessagingException {
        if(cc_list != null) {
            InternetAddress[] toAddress = new InternetAddress[cc_list.size()];
            for(int i=0;i<toAddress.length;i++) {
                toAddress[i] = new InternetAddress(cc_list.get(i).getEmail());
                msg.addRecipient(Message.RecipientType.CC, toAddress[i]);
            }
        }
    }

    private void setBccList(MimeMessage msg, ArrayList<Contact> bcc_list) throws MessagingException {
        if(bcc_list != null) {
            InternetAddress[] toAddress = new InternetAddress[bcc_list.size()];
            for(int i=0;i<toAddress.length;i++) {
                toAddress[i] = new InternetAddress(bcc_list.get(i).getEmail());
                msg.addRecipient(Message.RecipientType.BCC, toAddress[i]);
            }
        }
    }

    private void setSocketFactory(Properties properties) {
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.socketFactory", sf);
        } catch (GeneralSecurityException gse) {
            LOGGER.log(Level.SEVERE, gse.getMessage());
        }
    }

    public void resetLogger() {
        Handler consol = new ConsoleHandler();
        LOGGER.addHandler(consol);
        consol.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);
    }
}

