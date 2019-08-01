package com.enemai.utilMe;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class myFirebaseMessaging extends FirebaseMessagingService {
    public myFirebaseMessaging() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size() > 0){
            Log.d("dataRemoteMessage", remoteMessage.getData().get("content"));
        }
    }

    private boolean setToken(String token){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("tokenID",token);
        edit.apply();
        edit.commit();
        return true;
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        this.setToken(s);

    }
}
