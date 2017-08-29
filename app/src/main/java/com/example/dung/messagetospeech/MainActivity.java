package com.example.dung.messagetospeech;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private static MainActivity sActivity;

    private List<String> mMessage;

    public static MainActivity instance() {
        return sActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initList() {
        mMessage = new ArrayList<>();
        mMessage.add("Xin chao Viet Nam");
        mMessage.add("Xin chao Lao");
        mMessage.add("Xin chao Campuchia");
        mMessage.add("Xin chao Thai Lan");
        mMessage.add("Xin chao Singapor");
    }

    private void initView() {
        initList();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMessage);
        mAdapter = new MessageAdapter(mMessage, new MessageAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Toast.makeText(MainActivity.this, "Click item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        checkPermission();
    }

    public void readSMS() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int senderIndex = smsInboxCursor.getColumnIndex("address");
        int messageIndex = smsInboxCursor.getColumnIndex("body");
        if (messageIndex < 0 || !smsInboxCursor.moveToFirst()) return;
//        mAdapter.clear();
        do {
            String sender = smsInboxCursor.getString(senderIndex);
            String message = smsInboxCursor.getString(messageIndex);
            String formattedText = String.format(getResources().getString(R.string.sms_message), sender, message);
//            mAdapter.add(Html.fromHtml(formattedText).toString());
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final String newSms) {
//        mAdapter.insert(newSms, 0);
        mMessage.add(newSms);
        mAdapter.notifyItemInserted(mMessage.size());
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        readSMS();
    }
}
