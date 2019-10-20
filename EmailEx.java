package com.crisisgo.libs.cmailkit;

import com.crisisgo.libs.cmailkit.Attachment;
import com.crisisgo.libs.cmailkit.Contact;

import java.util.ArrayList;

public class EmailEx {

    private String subject;
    private String sender_email;
    private String greeting;
    private String main_text;
    private String signature;
    private String signature_logo_path;
    private ArrayList<Contact> receiver_list = new ArrayList<>();
    private ArrayList<Contact> cc_list = new ArrayList<>();
    private ArrayList<Contact> bcc_list = new ArrayList<>();
    private ArrayList<Attachment> attachments = new ArrayList<>();

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSenderEmail(String sender_email) {
        this.sender_email = sender_email;
    }

    public String getSenderEmail() {
        return sender_email;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setMainText(String main_text) {
        this.main_text = main_text;
    }

    public String getMainText() {
        return main_text;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignatureLogoPath(String signature_logo_path) {
        this.signature_logo_path = signature_logo_path;
    }

    public String getSignatureLogoPath() {
        return signature_logo_path;
    }

    public void setReceiverList(ArrayList<Contact> receiver_list) {
        this.receiver_list = receiver_list;
    }

    public void setReceiver(Contact receiver) {
        ArrayList<Contact> single_receiver_list = new ArrayList<Contact>();
        single_receiver_list.add(receiver);
        this.receiver_list = single_receiver_list;
    }

    public ArrayList<Contact> getReceiverList() {
        return receiver_list;
    }

    public void setCcList(ArrayList<Contact> cc_list) {
        this.cc_list = cc_list;
    }

    public void setCC(Contact cc) {
        ArrayList<Contact> single_cc = new ArrayList<Contact>();
        single_cc.add(cc);
        this.cc_list = single_cc;
    }

    public ArrayList<Contact> getCcList() {
        return cc_list;
    }

    public void setBccList(ArrayList<Contact> bcc_list) {
        this.bcc_list = bcc_list;
    }

    public ArrayList<Contact> getBccList() {
        return bcc_list;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }
}
