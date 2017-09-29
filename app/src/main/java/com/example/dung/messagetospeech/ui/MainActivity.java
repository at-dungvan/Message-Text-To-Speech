package com.example.dung.messagetospeech.ui;

import android.Manifest;
import android.content.ContentResolver;
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
import android.widget.Toast;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.models.Message;
import com.example.dung.messagetospeech.models.MyContact;

import java.util.ArrayList;
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
        getContact();
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
    }

    private void initView() {
        initTTS();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMessage);
        mAdapter = new MessageAdapter(mMessages, new MessageAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //callback here
                Log.d("aaaaaaaaaaaaaa", "OnItemClick: " + position);
                mCurrentPosition = position;
                String text = mMessages.get(mCurrentPosition).getMessage();
                ConvertTextToSpeech(text);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "Size message: " + mMessages.size() + ", Size contact: " + mContacts.size(), Toast.LENGTH_SHORT).show();
    }

    private void initTTS() {
        Log.d("aaaaaaaaaaaaaa", "initTTS: ");
        mTextToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("aaaaaaaaaaaaa", "This Language is not supported");
                    } else {
                        Log.d("aaaaaaaaaaaaa", "support language");
                    }
                } else
                    Log.d("aaaaaaaaaaaa", "Initilization Failed!");
            }
        });
    }

    public static void ConvertTextToSpeech(String text) {
        // TODO Auto-generated method stub
        Log.d("aaaaaaaaaaaaaaaa", "ConvertTextToSpeech: ");
        if (text == null || "".equals(text)) {
            text = "Content not available";
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    protected void onPause() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
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
            String formattedText = String.format(getResources().getString(R.string.sms_message), sender, message);
            mMessages.add(new Message(getContactByPhone(sender), message));
//            Toast.makeText(MainActivity.this, "Data read: " + formattedText + "/n : " + getContactByPhone(sender), Toast.LENGTH_SHORT).show();
        } while (smsInboxCursor.moveToNext());
    }

    public static void updateList(final Message newSms) {
//        mAdapter.insert(newSms, 0);
        //TODO Or here
        mMessages.add(newSms);
        mAdapter.notifyItemInserted(mMessages.size());
    }

    /**
     * Method get list your-contact
     */
    public void getContact() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "");
            MainActivity.mContacts.add(new MyContact(name, phoneNumber));
        }
        phones.close();
    }

    /**
     * Method get your-contact by phone
     */
    public static String getContactByPhone(String phone) {
        int size = MainActivity.mContacts.size();
        for (int i = 0; i< size; i++) {
            if (phone.equals(MainActivity.mContacts.get(i).getPhone())) {
                return MainActivity.mContacts.get(i).getName();
            }
        }
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
