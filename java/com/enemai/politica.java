package com.enemai;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.enemai.util.codigo;

public class politica extends AppCompatActivity {

    private Button aceito;
    public static final int MY_READ_PHONE_STATE = 1;
    private Activity mActivity;

    private void initNovoApp(){
        Intent napp = new Intent(this, permissao.class);
        napp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(napp);
        finish();
    }

    private void initGetPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão");
        builder.setIcon(R.drawable.ic_check);
        builder.setMessage("O Enemai necessita de acesso ao ID do seu dispositivo. Para evitar entradas indevidas e para autenticação do app. Permita o acesso!");
        builder.setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_READ_PHONE_STATE);
                }else{
                    initNovoApp();
                }
            }
        });

        builder.setNegativeButton("Depois", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alerta = builder.create();
        alerta.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica);

        getSupportActionBar().hide();

        mActivity = this;
        aceito = (Button)findViewById(R.id.bt_aceito);
        aceito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initGetPermission();

                SharedPreferences prefb = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = prefb.edit();
                edit.putBoolean("isPrimeiraVez", false);
                edit.apply();
                edit.commit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initNovoApp();

                } else {
                    Toast.makeText(getApplicationContext(),"Sem permissão",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


}
