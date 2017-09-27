package com.example.dung.messagetospeech.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.models.Message;
import com.example.dung.messagetospeech.ui.MainActivity;

/**
 * Created by dung on 29/08/2017.
 */
public class ReceiverMessage extends BroadcastReceiver {

    final SmsManager mSms = SmsManager.getDefault();
    private Message mMessage;

    public void onReceive(Context context, Intent intent) {
        // Get the SMS message received
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                // A PDU is a "protocol data unit". This is the industrial standard for SMS message
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    // This will create an SmsMessage object from the received pdu
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    // Get sender phone number
                    String phoneNumber = sms.getDisplayOriginatingAddress();
                    String sender = phoneNumber;
                    String message = sms.getDisplayMessageBody();
                    String formattedText = String.format(context.getResources().getString(R.string.sms_message), sender, message);
                    // Display the SMS message in a Toast
                    Toast.makeText(context, formattedText, Toast.LENGTH_LONG).show();
                    MainActivity inst = MainActivity.instance();
                    mMessage = new Message(inst.getContactByPhone(sender), message);
                    inst.updateList(mMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
