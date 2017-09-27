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
import android.widget.Toast;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.models.Message;
import com.example.dung.messagetospeech.models.MyContact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private static MainActivity sActivity;

    private List<Message> mMessages;
    private ArrayList<MyContact> mContacts;

    public static MainActivity instance() {
        return sActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMessage);
        mAdapter = new MessageAdapter(mMessages, new MessageAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Toast.makeText(MainActivity.this, "Message: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "Size message: " + mMessages.size() + ", Size contact: " + mContacts.size(), Toast.LENGTH_SHORT).show();
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
            String formattedText = String.format(getResources().getString(R.string.sms_message), sender, message);
            Toast.makeText(MainActivity.this, sender + " : " + getContactByPhone(sender), Toast.LENGTH_SHORT).show();
            mMessages.add(new Message(getContactByPhone(sender), message));
//            Toast.makeText(MainActivity.this, "Data read: " + formattedText + "/n : " + getContactByPhone(sender), Toast.LENGTH_SHORT).show();
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final Message newSms) {
//        mAdapter.insert(newSms, 0);
        mMessages.add(newSms);
        mAdapter.notifyItemInserted(mMessages.size());
    }

    /**
     * Method get list your-contact
     */
    public String getContactByPhone(String phone) {
        mContacts = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            mContacts.add(new MyContact(name, phoneNumber));
            Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            if (phone.equals(phoneNumber)) {
                phones.close();
                return name;
            }
        }
        phones.close();
        return phone;
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
