package com.crisisgo.libs.cmailkit;

public class EmailBody {

    private String html_body = "";
    private String plain_text_body = "";

    public void addToHtmlBody(String in) {
        html_body = html_body.concat(in);
    }

    public void addPlainTextBody(String in) {
        plain_text_body = plain_text_body.concat(in);
    }

    public String getHtmlBody() {
        return html_body;
    }

    public String getPlainTextBody() {
        return plain_text_body;
    }
}
