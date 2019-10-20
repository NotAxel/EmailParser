package com.crisisgo.libs.cmailkit;

import java.util.ArrayList;

public class Email {

    private String subject;
    private String sender_email;
    private String date;
    private EmailBody body;
    private ArrayList<Contact> receiver_list = new ArrayList<>();
    private ArrayList<Contact> cc_list = new ArrayList<>();
    private ArrayList<Contact> bcc_list = new ArrayList<>();
    private ArrayList<Attachment> attachments = new ArrayList<>();

    public ArrayList<Contact> getBcc_list() {
        return bcc_list;
    }

    public void setBccList(ArrayList<Contact> bcc_list) {
        this.bcc_list = bcc_list;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailBody getBody() {
        return body;
    }

    public void setBody(EmailBody body) {
        this.body = body;
    }

    public ArrayList<Contact> getReceiverList() {
        return receiver_list;
    }

    public void setReceiverList(ArrayList<Contact> receiver_list) {
        this.receiver_list = receiver_list;
    }

    public void setReceiver(Contact receiver) {
        ArrayList<Contact> single_receiver_list = new ArrayList<Contact>();
        single_receiver_list.add(receiver);
        this.receiver_list = single_receiver_list;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getSenderEmail() {
        return sender_email;
    }

    public void setSenderEmail(String name) {
        this.sender_email = name;
    }

    public ArrayList<Contact> getCcList() {
        return cc_list;
    }

    public void setCcList(ArrayList<Contact> cc_list) {
        this.cc_list = cc_list;
    }

    public void setCC(Contact cc) {
        ArrayList<Contact> single_cc = new ArrayList<Contact>();
        single_cc.add(cc);
        this.cc_list = single_cc;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }
}
