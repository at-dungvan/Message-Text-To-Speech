package com.example.dung.messagetospeech.models;

/**
 * Created by dung on 22/09/2017.
 */

public class MyContact {

    String name;
    String phone;

    public MyContact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
