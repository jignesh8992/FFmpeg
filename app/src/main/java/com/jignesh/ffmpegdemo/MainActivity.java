package com.jignesh.ffmpegdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Button btnSimple;
    private Button btnService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        initViews();
        initActions();

    }

    private void initViews() {
        btnSimple = findViewById(R.id.btn_simple);
        btnService = findViewById(R.id.btn_service);

    }

    private void initActions() {

        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SimpleActivity.class));
            }
        });

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ServiceActivity.class));
            }
        });

    }
}
