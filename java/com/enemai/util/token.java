package com.enemai.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class token {

    private Context mContext;
    private Activity mActivity;

    public token(Activity mActivity) {
        this.mActivity = mActivity;
        this.mContext = mActivity.getApplicationContext();
    }

    private boolean setToken(String token){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("tokenID",token);
        edit.apply();
        edit.commit();
        return true;
    }

    public void obterTokenFirebase(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(
                new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("failed", "getInstanceId failed", task.getException());
                            return;
                        }
                        setToken(task.getResult().getToken());
                    }
                });
    }

    public String obterToken(){
        codigo code = new codigo(mActivity);
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return prefb.getString("tokenID",code.getCodeUser());
    }

    public String obterTokenPayment(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return prefb.getString("purchaseToken","");
    }

    public String obterOrderPayment(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return prefb.getString("purchaseOrderId","");
    }

}
