package com.enemai;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.enemai.adapter.listaAdapter;
import com.enemai.model.data;
import com.enemai.util.ads;
import com.enemai.util.api;
import com.enemai.util.codigo;
import com.enemai.util.token;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class app extends AppCompatActivity {

    private RecyclerView rv;
    private ProgressBar bar;
    private ads mAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        getSupportActionBar().setTitle("Temas");

        rv = (RecyclerView)findViewById(R.id.rv);
        bar = (ProgressBar)findViewById(R.id.bar);
        mAds = new ads(this);

        init();


        MobileAds.initialize(this,
                mAds.getIdAdMob());

        initBanner();

    }

    private void initBanner(){
        MobileAds.initialize(this,
                mAds.getIdAdMob());

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void showInformations(){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.alerta, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private void setFirst(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("first", "1");
        edit.apply();
        edit.commit();

        showInformations();
    }

    private boolean isFirst(){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this);
        return prefb.getString("first","") == "";
    }

    private void levelErro(int level){
        if(level == 1){
            Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show();
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

    private void init(){
        rv.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        final List<data> dados = new ArrayList<>();

        final api Api = new api();
        final codigo mCode = new codigo(this);
        token tok = new token(this);
        String token = tok.obterToken();
        String idDevice = mCode.getCodeUser();
        String url = Api.getUrlTemas()+"?token="+token+"&idDevice="+idDevice;

        //Log.d("sku", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            if(response.getInt("sucesso") == 0){
                                exibirAlertaErro(response.getString("msg"),2);
                                return;
                            }

                            JSONArray conjunto = null;

                            conjunto = response.getJSONArray("0");

                            for(int i = 0;i < conjunto.length(); i ++){
                                data mDados = new data();
                                JSONObject obj = conjunto.getJSONObject(i);

                                mDados.setTitulo(obj.getString("titulo"));
                                mDados.setDescricao(obj.getString("descricao"));
                                mDados.setPrecisao(obj.getInt("precisao"));
                                mDados.setId(obj.getInt("id"));

                                dados.add(mDados);
                            }

                            initRecyclerView(dados);

                        }catch (JSONException e){
                            try {
                                exibirAlertaErro(e.getMessage(), 1);
                            }catch (NullPointerException a){
                                a.printStackTrace();
                                exibirAlertaErro(a.getMessage(), 2);
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

        initRecyclerView(dados);
    }

    private void initRecyclerView(List<data> dados){
        bar.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(new listaAdapter(this, dados));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

        if(isFirst()){
            setFirst();
        }
    }
}
