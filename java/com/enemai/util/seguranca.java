package com.enemai.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.enemai.util.codigo;

public class seguranca {

    private Context mContext = null;
    private Activity mActivity = null;

    public seguranca(Activity mActivity) {
        this.mContext = mActivity.getApplicationContext();
        this.mActivity = mActivity;
    }

    public boolean isPermission(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return prefb.getString("permissao","") != "";
    }

    public boolean permitirAcesso(){

        codigo cod = new codigo(this.mActivity);
        String code = cod.getCodeUser();
        if(code != ""){

            SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
            SharedPreferences.Editor edit = prefb.edit();
            edit.putString("permissao", code);
            edit.apply();
            edit.commit();

            return true;
        }

        return false;
    }
}
