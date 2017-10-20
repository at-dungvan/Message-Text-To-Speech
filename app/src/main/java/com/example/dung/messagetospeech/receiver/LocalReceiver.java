package com.example.dung.messagetospeech.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dung.messagetospeech.lib.CustomTTSService;

/**
 * Created by dungvand on 20/10/17.
 */

public class LocalReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
            CustomTTSService.initTTS(context.getApplicationContext());
        }
    }
}
