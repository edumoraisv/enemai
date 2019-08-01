package com.enemai.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.enemai.R;
import com.enemai.introducao;
import com.enemai.model.infoPermissao;
import com.enemai.util.seguranca;

import org.json.JSONException;
import org.json.JSONObject;

public class vendas implements PurchasesUpdatedListener {

    private Context mContext = null;
    private Activity mActivity = null;
    private BillingClient billingClient;
    private Button btPurchase = null;
    private boolean comprou = false;

    private SkuDetails skuAcesso = null;

    public vendas(Activity act, Button btPurchase) {
        this.mContext = act.getApplicationContext();
        this.mActivity = act;
        this.btPurchase = btPurchase;
    }

    public void comprar_acesso(){
        if (billingClient.isReady() && skuAcesso != null){
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuAcesso)
                    .build();
            BillingResult responseCode = billingClient.launchBillingFlow(this.mActivity, flowParams);
        }
    }

    public void comprar_remove_ads(SkuDetails skuDetails){

    }

    private void acessar(){
        Intent intro = new Intent(mActivity, introducao.class);
        intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intro);
        mActivity.finish();
    }

    public void iniciarBilling() {
        billingClient = BillingClient.newBuilder(this.mContext).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("Enemai-log","Billing iniciado");
                    checarPagamentos();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("Enemai-log","Billing problema");
            }
        });
    }

    private void checarPagamentos() {

        List<String> skuList = new ArrayList<>();
        skuList.add("acesso");
        //skuList.add("com.enemai.acesso");
        //skuList.add("remove_ads");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                String sku = skuDetails.getSku();
                                if("acesso".equals(sku)){
                                    Log.d("sku", sku);
                                    skuAcesso = skuDetails;
                                }
                            }
                        }
                    }
                });

    }

    public boolean confirmarCompra(String tokenPurchase){
        ConsumeParams.Builder consumeParams = ConsumeParams.newBuilder();
        consumeParams.setPurchaseToken(tokenPurchase);
        billingClient.consumeAsync(consumeParams.build(), new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if(BillingClient.BillingResponseCode.OK == billingResult.getResponseCode() && purchaseToken!=null){
                    comprou = true;
                }else{
                    comprou = false;
                }
            }
        });
        return false;
    }

    public boolean setPurchaseInApp(String token, String orderId){
        //Log.d("sku", String.valueOf((mContext != null || mActivity != null)));
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("purchaseToken", token);
        edit.putString("purchaseOrderId", orderId);
        edit.apply();
        edit.commit();

        if(btPurchase != null){
            btPurchase.setText("Processando...");
            btPurchase.setEnabled(false);
        }

        return this.postPurchase(token, orderId);
    }

    private void levelErro(int level){
        if(level == 1){
            //Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show();
        }
        if(level == 2){
            mActivity.finish();
        }
    }

    private void exibirAlertaErro(String msg, final int level){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Erro!");
        builder.setIcon(R.drawable.ic_erro);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                levelErro(level);
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                levelErro(level);
            }
        });

        try {
            AlertDialog alerta = builder.create();
            alerta.show();
        }catch (IllegalStateException e){
            Log.w("sku","problema na exibição do alerta.");
        }
    }

    private boolean postPurchase(String tokenPurchase, String orderId){
        //Log.d("sku", String.valueOf((mContext != null || mActivity != null)));
        api Api = new api();
        final codigo code = new codigo(mActivity);
        token tok = new token(mActivity);
        String token = tok.obterToken();
        String idDevice = code.getCodeUser();
        String url = Api.getUrlPagamento()+"?token="+token+"&idDevice="+idDevice;

        if(tokenPurchase.length() == 0){
            tokenPurchase = "null";
        }

        if(orderId.length() == 0){
            orderId = "null";
        }

        JSONObject postParams = new JSONObject();
        try {
            postParams.put("tokenPurchase", tokenPurchase);
            postParams.put("orderId", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postJsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getInt("sucesso") == 0){
                                exibirAlertaErro(response.getString("msg"), 2);
                                return;
                            }
                            //Log.d("sku",response.getString("tokenPurchase"));
                            permitirAcesso();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("sku",String.valueOf(error.networkResponse.statusCode));
                        exibirAlertaErro("Houve um problema de conexão.",2);
                        //Toast.makeText(mContext,"Houve um problema de conexão.: "+error.networkResponse.statusCode,Toast.LENGTH_SHORT).show();
                    }
        });
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(postJsonObjReq);

        return false;
    }

    private void permitirAcesso(){
        seguranca seg = new seguranca(this.mActivity);
        if (seg.permitirAcesso()) {
            acessar();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

        token mToken = new token(mActivity);
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            this.setPurchaseInApp(mToken.obterTokenPayment(), "");
            //this.permitirAcesso();
            return;
        }

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                Log.d("sku","token: "+purchase.getPurchaseToken()+" order: "+purchase.getOrderId());
                this.setPurchaseInApp(purchase.getPurchaseToken(),purchase.getOrderId());
            }
            return;
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this.mContext,"Compra cancelada",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.mContext,"Problema na compra " + String.valueOf(billingResult.getResponseCode()),Toast.LENGTH_SHORT).show();
        }
    }
}
