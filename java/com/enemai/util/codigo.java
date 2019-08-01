package com.enemai.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class codigo {

    private Context mContext = null;
    private Activity mActivity = null;

    public codigo(Activity mActivity) {
        this.mContext = mActivity.getApplicationContext();
        this.mActivity = mActivity;
    }

    public String setCodeUser() {

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext,"Sem permissão",Toast.LENGTH_LONG).show();
            return "";
        }

        telephonyManager.getDeviceId();

        String Imei = telephonyManager.getDeviceId();

        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("code",Imei);
        edit.apply();
        edit.commit();

        return Imei;
    }

    public String getCodeUser(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return prefb.getString("code","");
    }

    public boolean setCodeHash(String idHash){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("idHash",idHash);
        edit.apply();
        edit.commit();
        return true;
    }

    public String getCodeHash(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return prefb.getString("idHash","");
    }

}
