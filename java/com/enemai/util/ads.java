package com.enemai.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import java.util.Date;

public class ads{

    private String idAdMob = "";
    private String idAdVideo = "";
    private String idAdBanner = "";
    private Context mContext = null;

    public ads(Context mContext) {
        this.mContext = mContext;
    }

    private void mudarDataExibicao(SharedPreferences prefb, String data){
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("adsVideo", data);
        edit.apply();
        edit.commit();
    }

    public boolean chkExibicao(){
        Date d = new Date();
        CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());

        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        if(prefb.getString("adsVideo","").equals("")){

            mudarDataExibicao(prefb, s.toString());

            return true;
        }else{
            if(prefb.getString("adsVideo","").equals(s.toString())){
                return false;
            }else{
                mudarDataExibicao(prefb, s.toString());
                return true;
            }
        }
    }

    public String getIdAdBanner() {
        return idAdBanner;
    }

    public void setIdAdBanner(String idAdBanner) {
        this.idAdBanner = idAdBanner;
    }

    public String getIdAdMob() {
        return idAdMob;
    }

    public void setIdAdMob(String idAdMob) {
        this.idAdMob = idAdMob;
    }

    public String getIdAdVideo() {
        return idAdVideo;
    }

    public void setIdAdVideo(String idAdVideo) {
        this.idAdVideo = idAdVideo;
    }
}
