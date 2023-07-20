package com.example.diet_helper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.example.diet_helper.adapter.DietAdapter;
import com.example.diet_helper.dao.AppDatabase;
import com.example.diet_helper.dao.DietDao;
import com.example.diet_helper.model.Diet;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static List<Diet> diets = new ArrayList<>();

    private DietAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        setClickListeners();

        AppDatabase appDatabase = ((MyAmplifyApp) getApplicationContext()).getAppDatabase();
        final DietDao dietDao = appDatabase.dietDao();

        AsyncTask.execute(() -> {
            List<Diet> foundDiets = dietDao.getAll();
            runOnUiThread(() -> {
                diets.addAll(foundDiets);
                adapter = new DietAdapter(this, diets);

                ListView listView = findViewById(R.id.diet_list);
                listView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            });
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    private void setClickListeners() {
        findViewById(R.id.add_diet_button).setOnClickListener(view -> {
            TextInputLayout editText = findViewById(R.id.add_diet_text);

            if (editText.getEditText().getText().toString().isBlank()) {
                return;
            }

            Diet diet = new Diet(editText.getEditText().getText().toString());

            AppDatabase appDatabase = ((MyAmplifyApp) getApplicationContext()).getAppDatabase();
            final DietDao dietDao = appDatabase.dietDao();

            AsyncTask.execute(() -> dietDao.insertDiet(diet));

            adapter.add(diet);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        addFlags(menu.findItem(R.id.action_flag));

        return true;
    }
}