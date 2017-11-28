package com.example.dung.messagetospeech.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.lib.CustomTTSService;
import com.example.dung.messagetospeech.models.Message;
import com.example.dung.messagetospeech.models.MyContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private static MessageAdapter mAdapter;

    private int mCurrentPosition;
    private static List<Message> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeListContacts();
        setContentView(R.layout.activity_main);
        checkPermission();
        CustomTTSService.initTTS(getApplication().getApplicationContext());
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMessage);
        mAdapter = new MessageAdapter(mMessages, new MessageAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //callback here
                mCurrentPosition = position;
                String message = mMessages.get(mCurrentPosition).getMessage();
                CustomTTSService.ConvertTextToSpeech("", message, getApplicationContext());
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void readSMS() {
        mMessages = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int senderIndex = smsInboxCursor.getColumnIndex("address");
        int messageIndex = smsInboxCursor.getColumnIndex("body");
        if (messageIndex < 0 || !smsInboxCursor.moveToFirst()) return;
        do {
            String sender = smsInboxCursor.getString(senderIndex);
            String message = smsInboxCursor.getString(messageIndex);
            mMessages.add(new Message(MyContact.getContactByPhone(sender), message));
        } while (smsInboxCursor.moveToNext());
    }

    public static void updateList(final Message newSms) {
        //TODO Or here
        mMessages.add(0, newSms);
        mAdapter.notifyItemInserted(0);
    }

    /**
     * Method get list your-contact
     */
    public void makeListContacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "").replace("+", "").replace("(", "").replace(")", "");
            if (phoneNumber.startsWith("84")) {
                phoneNumber = phoneNumber.replaceFirst("84", "0");
            }
            MyContact.mContacts.add(new MyContact(name, phoneNumber));
        }
        phones.close();
    }

    /**
     * Method request permission for app
     */
    private void checkPermission() {
        // Permission read contact
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        }
        // Permission read sms
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 2);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 2);
            }
        }
        readSMS();
    }
}
