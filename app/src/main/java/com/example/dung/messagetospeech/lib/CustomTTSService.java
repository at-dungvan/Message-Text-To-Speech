package com.example.dung.messagetospeech.lib;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.dung.messagetospeech.R;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by dungvand on 20/10/17.
 */

public class CustomTTSService {

    private static TextToSpeech mTextToSpeech;
    private static String sender = "";
    private static String message = "";

    public static void initTTS(final Context context) {
        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
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
                        CustomTTSService.ConvertTextToSpeech(CustomTTSService.sender, CustomTTSService.message, context);
                    }
                } else
                    Log.d("initTTS", "Initilization Failed!");
            }
        });
    }

    public static void ConvertTextToSpeech(String sender, String message, Context context) {
        if (mTextToSpeech == null) {
            CustomTTSService.sender = sender;
            CustomTTSService.message = message;
            initTTS(context);
        } else {
            // TODO Auto-generated method stub
            CustomTTSService.sender = "";
            CustomTTSService.message = "";
            if (sender.equals("") && message.equals("")) {
                return;
            }
            String textSender = "";
            try {
                mTextToSpeech.speak(sender, TextToSpeech.QUEUE_FLUSH, null);
                Double.parseDouble(sender);
                char[] from = sender.toCharArray();
                String readFrom = String.format(context.getResources().getString(R.string.read_sender), "");
                mTextToSpeech.speak(readFrom, TextToSpeech.QUEUE_FLUSH, null);
                for (char c : from) {
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
    }
}
