package com.enemai;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.enemai.model.infoPermissao;
import com.enemai.util.api;
import com.enemai.util.codigo;
import com.enemai.util.seguranca;
import com.enemai.util.token;
import com.enemai.util.vendas;

import org.json.JSONException;
import org.json.JSONObject;

public class permissao extends AppCompatActivity {

    private TextView link_acesso = null;
    private TextView contador = null;
    private Button bt_share = null;
    public Button bt_pusher = null;
    private String code = null;
    private api Api = null;
    public static final int MY_READ_PHONE_STATE = 1;
    private infoPermissao info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissao);

        getSupportActionBar().hide();

        link_acesso = (TextView)findViewById(R.id.linkAcesso);
        contador = (TextView)findViewById(R.id.permissao_comp_contador);
        bt_share = (Button)findViewById(R.id.bt_compartilhar);
        bt_pusher = (Button)findViewById(R.id.bt_comprar);

        bt_share.setEnabled(false);
        link_acesso.setText("...");
        link_acesso.setEnabled(false);

        Api = new api();
        codigo mCode = new codigo(this);
        code = mCode.getCodeHash();
        if(code != ""){
            link_acesso.setText(Api.getUrlCodeAcesso() + code);
        }else{
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_READ_PHONE_STATE);
            }else{
                code = mCode.setCodeUser();
                link_acesso.setText(Api.getUrlCodeAcesso() + code);
            }

        }

        link_acesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(getApplicationContext(), Api.getUrlCodeAcesso() + code);
            }
        });

        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareAppCode();
            }
        });

        Activity pusher = this;
        final vendas vend = new vendas(pusher, bt_pusher);
        vend.iniciarBilling();

        bt_pusher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vend.comprar_acesso();
            }
        });

        contador.setText("...");

        if(mCode.getCodeHash().equals("")) {
            chkDados();
        }else{
            obterInfoPermissao();
        }

    }

    private void acessar(){
        Intent intro = new Intent(this, introducao.class);
        intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intro);
        finish();
    }

    private void changeTokenPurchase(String token){
        SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefb.edit();
        edit.putString("purchaseToken", token);
        edit.apply();
        edit.commit();
    }

    private void init(){
        contador.setText(String.valueOf(info.getClickInLink()));
        changeTokenPurchase(info.getTokenPurchase());
        if(info.getClickInLink() >= 5){
            seguranca seg = new seguranca(this);
            seg.permitirAcesso();
            acessar();
        }

        if(info.getIsAllowed() == 1){
            seguranca seg = new seguranca(this);
            seg.permitirAcesso();
            acessar();
        }

        if(info.getPayed() == 1){
            seguranca seg = new seguranca(this);
            seg.permitirAcesso();
            acessar();
        }
    }

    private void levelErro(int level){
        if(level == 1){
            Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show();
        }
        if(level == 2){
            finish();
        }
    }

    private void showMeta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ganhe acesso grátis!");
        builder.setIcon(R.drawable.ic_star);
        builder.setMessage(R.string.descricao);
        builder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //copyToClipboard(getApplicationContext(), "");
            }
        });

        builder.setNegativeButton("Enviar Feedback", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent sender = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.enemai"));
                startActivity(sender);
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        AlertDialog alerta = builder.create();
        alerta.show();
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

    private void chkDados(){

        final api Api = new api();
        final codigo mCode = new codigo(this);
        token tok = new token(this);
        String token = tok.obterToken();
        String idDevice = mCode.getCodeUser();

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
                                code = idHash;
                                mCode.setCodeHash(idHash);
                                link_acesso.setText(Api.getUrlCodeAcesso() + idHash);
                            }
                            obterInfoPermissao();
                        }catch (JSONException e){
                            try {
                                exibirAlertaErro(e.getMessage(), 1);
                            }catch (NullPointerException a){
                                a.printStackTrace();
                                obterInfoPermissao();
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

    private void activeElements(){
        bt_share.setEnabled(true);
        link_acesso.setEnabled(true);
    }

    private void obterInfoPermissao(){
        api Api = new api();
        final codigo code = new codigo(this);
        token tok = new token(this);
        String token = tok.obterToken();
        String idDevice = code.getCodeUser();
        String url = Api.getUrlInfoPermissao()+"?token="+token+"&idDevice="+idDevice;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            if(response.getInt("sucesso") == 0){
                                exibirAlertaErro(response.getString("msg"),2);
                                return;
                            }

                            JSONObject resultado = response.getJSONObject("resultado");

                            info = new infoPermissao();
                            info.setIsAllowed(resultado.getInt("isAllowed"));
                            info.setPayed(resultado.getInt("payed"));
                            info.setClickInLink(resultado.getInt("clickInLink"));
                            info.setTokenPurchase(resultado.getString("tokenPayed"));

                            init();
                            showMeta();
                            activeElements();

                        }catch (JSONException e){
                            try {
                                exibirAlertaErro(e.getMessage(), 1);
                            }catch (NullPointerException a){
                                a.printStackTrace();
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

    public void shareAppCode(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Ei, me ajuda a conquistar minha meta no convite do Enemai! Através desse app vou ter uma base de qual será o tema do Enem2019. Tudo usando inteligência artificial e previsão estatístico. \n\n Clique no meu convite de acesso: "+Api.getUrlCodeAcesso()+code+"\n\n Código: *"+code+"*");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText(
                                context.getResources().getString(
                                        R.string.link), text);
                clipboard.setPrimaryClip(clip);
            }

            Toast.makeText(getApplicationContext(),"Link foi copiado",Toast.LENGTH_SHORT).show();

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    codigo mCode = new codigo(this);
                    code = mCode.setCodeUser();
                    link_acesso.setText("https://base9.io/code/" + code);

                    Toast.makeText(getApplicationContext(),code,Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),"Sem permissão",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
