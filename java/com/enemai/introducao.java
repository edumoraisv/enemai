package com.enemai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class introducao extends AppCompatActivity {

    private Button bt;

    private void initApp(){
        Intent app = new Intent(this, app.class);
        app.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(app);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducao);

        getSupportActionBar().hide();

        bt = (Button)findViewById(R.id.bt_concluir);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initApp();
            }
        });

    }
}
