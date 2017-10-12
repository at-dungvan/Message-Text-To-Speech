package com.example.dung.messagetospeech.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.models.Message;
import com.example.dung.messagetospeech.models.MyContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private static MessageAdapter mAdapter;

    private static TextToSpeech mTextToSpeech;
    private int mCurrentPosition;
    private static List<Message> mMessages;
    public static ArrayList<MyContact> mContacts = new ArrayList<MyContact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeListContacts();
        setContentView(R.layout.activity_main);
        checkPermission();
        initTTS();
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
                ConvertTextToSpeech("", message, getApplicationContext());
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initTTS() {
        mTextToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    Locale lang = Locale.getDefault();
                    int result = mTextToSpeech.setLanguage(lang);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("initTTS", "The Language" + lang.toString()  + " is not supported");
                    } else {
                        Log.d("initTTS", "support language " + lang.toString());
                    }
                } else
                    Log.d("initTTS", "Initilization Failed!");
            }
        });
    }

    public static void ConvertTextToSpeech(String sender, String message, Context context) {
        // TODO Auto-generated method stub
        String textSender = "";
        try {
            mTextToSpeech.speak(sender, TextToSpeech.QUEUE_FLUSH, null);
            Double.parseDouble(sender);
            char[] from = sender.toCharArray();
            String readFrom = String.format(context.getResources().getString(R.string.read_sender), "");
            mTextToSpeech.speak(readFrom, TextToSpeech.QUEUE_FLUSH, null);
            for (char c: from) {
                textSender += c + " ";
            }
        } catch (Exception e) {
            textSender = sender;
        }
        textSender = textSender != "" ? String.format(context.getResources().getString(R.string.read_sender), textSender) : "";
        message = message != "" ? String.format(context.getResources().getString(R.string.read_message), message) : "";
        if (message == null || "".equals(message)) {
            message = context.getResources().getString(R.string.message_unavailable);
        }
        mTextToSpeech.speak(textSender + message, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    protected void onPause() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
        }
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
            mMessages.add(new Message(getContactByPhone(sender), message));
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
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "");
            MainActivity.mContacts.add(new MyContact(name, phoneNumber));
        }
        if(mContacts.size() > 1) {
            Collections.sort(mContacts, new Comparator<MyContact>() {
                @Override
                public int compare(MyContact myContact1, MyContact myContact2) {
                    return myContact1.getName().compareTo(myContact2.getName());
                }
            });
        }
        phones.close();
    }

    /**
     * Method get your-contact by phone
     */
    public static String getContactByPhone(String phone) {
        int index = Collections.binarySearch(mContacts, new MyContact(null, phone), new Comparator<MyContact>() {
            @Override
            public int compare(MyContact myContact1, MyContact myContact2) {
                return myContact1.getPhone().compareTo(myContact2.getPhone());
            }
        });
        phone = index > 0 ? mContacts.get(index).getName() : phone;
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
