package com.example.diet_helper;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.util.StringUtil;

import com.androidnetworking.AndroidNetworking;

public class ImageResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_result);

        TextView resultView = (TextView) findViewById(R.id.result_text);

        String text = ((String) getIntent().getExtras().get("badProducts"));

        if (text == null || text.isBlank()) {
            resultView.setText(R.string.products_not_exist);
        }

        resultView.setText(getString(R.string.products_exist, text));

        findViewById(R.id.back).setOnClickListener(view -> finish());
    }
}
