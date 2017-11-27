package com.example.dung.messagetospeech.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.example.dung.messagetospeech.lib.CustomTTSService;
import com.example.dung.messagetospeech.models.MyContact;
import com.example.dung.messagetospeech.receiver.LocalReceiver;
import com.example.dung.messagetospeech.receiver.ReceiverMessage;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by dungvand on 27/11/17.
 */

public class BackgroundServices extends Service {
    private static final String SMS_ACTION="android.provider.Telephony.SMS_RECEIVED";
    private static final String LOCALE_ACTION="android.intent.action.LOCALE_CHANGED";
    private BroadcastReceiver SMSReceiver;
    private BroadcastReceiver localeReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        makeListContacts();

        final IntentFilter theLocaleFilter = new IntentFilter();
        theLocaleFilter.addAction(LOCALE_ACTION);
        this.localeReceiver = new LocalReceiver();
        this.registerReceiver(this.localeReceiver, theLocaleFilter);

        final IntentFilter theSMSFilter = new IntentFilter();
        theSMSFilter.addAction(SMS_ACTION);
        this.SMSReceiver = new ReceiverMessage();
        this.registerReceiver(this.SMSReceiver, theSMSFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Method get list your-contact
     */
    public void makeListContacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "");
            MyContact.mContacts.add(new MyContact(name, phoneNumber));
        }
        if(MyContact.mContacts.size() > 1) {
            Collections.sort(MyContact.mContacts, new Comparator<MyContact>() {
                @Override
                public int compare(MyContact myContact1, MyContact myContact2) {
                    return myContact1.getName().compareTo(myContact2.getName());
                }
            });
        }
        phones.close();
    }
}
