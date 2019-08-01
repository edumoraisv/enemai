package com.enemai;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.enemai.util.api;
import com.enemai.util.codigo;
import com.enemai.util.seguranca;
import com.enemai.util.token;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import com.enemai.util.ads;

import org.json.JSONException;
import org.json.JSONObject;

public class inicio extends AppCompatActivity implements RewardedVideoAdListener {

    private boolean isNotify = false;
    private RewardedVideoAd mRewardedVideoAd = null;
    private ads mAds = null;

    private void initPolitica(){
        /**/

        Intent poli = new Intent(this, politica.class);
        poli.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(poli);
        finish();

    }

    private void initApp(){
        Intent app = new Intent(this, app.class);
        app.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(app);
        finish();
    }

    private void initNovoApp(){
        Intent napp = new Intent(this, permissao.class);
        napp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(napp);
        finish();
    }

    private boolean isPrimeiraVez(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefb.getBoolean("isPrimeiraVez",true);
    }

    private void init(){
        if(isPrimeiraVez()){
            initPolitica();
        }else{
            seguranca seg = new seguranca(this);
            if(seg.isPermission()){
                initApp();
            }else{
                initNovoApp();
            }
        }
    }

    private void levelErro(int level){
        if(level == 1){
            inicializacao();
        }
        if(level == 2){
            finish();
        }
    }

    private void exibirAlertaErro(String msg, final int level){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        AlertDialog alerta = builder.create();
        alerta.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        getSupportActionBar().hide();

        token tok = new token(this);
        tok.obterTokenFirebase();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            for (String key : extras.keySet()) {
                if (extras.getString(key) != null) {
                    if (extras.getString("id") != null) {
                        String id = extras.getString("id");
                        String tipo = extras.getString("tipo");
                        initNotification(id, tipo);
                        isNotify = true;
                        break;
                    }
                }
            }
        }

        if(!isNotify) {
            mAds = new ads(getApplicationContext());
            MobileAds.initialize(this, mAds.getIdAdMob());

            chkDados();
        }

    }

    private void initNotification(String id, String tipo){
        //tipo: free/premium
        Toast.makeText(getApplicationContext(), "id:"+id+" tipo:"+tipo, Toast.LENGTH_SHORT).show();
    }

    private void chkDados(){

        api Api = new api();
        final codigo code = new codigo(this);
        token tok = new token(this);
        String token = tok.obterToken();
        String idDevice = code.getCodeUser();

        if(token.equals("") || idDevice.equals("")){
            init();
            return;
        }

        String url = Api.getUrlGerarAcesso()+"?token="+token+"&idDevice="+idDevice;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            if(response.getInt("sucesso") == 0){
                                exibirAlertaErro(response.getString("msg"),2);
                                return;
                            }

                            String idHash = response.getString("idHash");
                            if(!idHash.equals("")){
                                code.setCodeHash(idHash);
                            }
                            inicializacao();
                        }catch (JSONException e){
                            try {
                                exibirAlertaErro(e.getMessage(), 1);
                            }catch (NullPointerException a){
                                a.printStackTrace();
                                inicializacao();
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        exibirAlertaErro("Houve um problema de conexão.", 1);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void inicializacao(){
        if (mAds.chkExibicao()) {
            video();
        }else{
            init();
        }
    }

    public void video(){
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(mAds.getIdAdVideo(),
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if(mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        init();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        init();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d("sku", "erro");
        init();
    }

    @Override
    public void onRewardedVideoCompleted() {
        init();
    }
}
