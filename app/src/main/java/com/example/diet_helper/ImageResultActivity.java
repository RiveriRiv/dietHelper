package com.example.diet_helper;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;

public class ImageResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_result);

        ((TextView) findViewById(R.id.result_text)).setText(((String) getIntent().getExtras().get("badProducts")));

        findViewById(R.id.back).setOnClickListener(view -> finish());
    }
}
