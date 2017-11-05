package com.example.dung.messagetospeech.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by dung on 22/09/2017.
 */

public class MyContact {

    String name;
    String phone;
    public static ArrayList<MyContact> mContacts = new ArrayList<MyContact>();

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

    /**
     * Method get your-contact by phone
     */
    public static String getContactByPhone(String phone) {
        phone = phone.replace(" ", "").replace("-", "").replace("+", "").replace("(", "").replace(")", "");
        if (phone.startsWith("84")){
            phone = phone.replaceFirst("84", "0");
        }
        int index = Collections.binarySearch(MyContact.mContacts, new MyContact(null, phone), new Comparator<MyContact>() {
            @Override
            public int compare(MyContact myContact1, MyContact myContact2) {
                return myContact1.getPhone().compareTo(myContact2.getPhone());
            }
        });
        phone = index >= 0 ? MyContact.mContacts.get(index).getName() : phone;
        return phone;
    }
}
