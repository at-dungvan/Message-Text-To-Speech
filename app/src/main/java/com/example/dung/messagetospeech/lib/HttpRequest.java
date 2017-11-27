package com.example.dung.messagetospeech.lib;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dung.messagetospeech.config.Config;
import com.example.dung.messagetospeech.models.MyContact;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dungvand on 26/11/17.
 */

public class HttpRequest  extends AsyncTask<String, Void, String> {

    private String sender = "";
    private String message = "";
    private Context context;

    public HttpRequest (Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        String resMessage = "Error";
        try {
            if (strings.length > 1) {
                this.sender = strings[0];
                this.message = strings[1];
            }
            resMessage = doPostRequest(Config.URL_POST_REQUEST, message);
        } catch (Exception e) {
            Log.d("bbbbbb", "doInBackground: " + e.toString());
        }
        return resMessage;
    }

    public String doPostRequest(String url, String body) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        //add request header
        conn.setRequestMethod("POST");

        //send post request
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(body);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        CustomTTSService.ConvertTextToSpeech(MyContact.getContactByPhone(sender), message, context);
    }
}
