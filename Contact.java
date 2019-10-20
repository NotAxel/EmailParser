package com.crisisgo.libs.cmailkit;

public class Contact {

    public Contact(){
        // Do nothing
    }

    public Contact(String name, String email){
        this.name = name;
        this.email = email;
    }

    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
