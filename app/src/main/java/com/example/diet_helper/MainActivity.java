package com.example.diet_helper;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Diet;
import com.androidnetworking.AndroidNetworking;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<Diet> diets = new ArrayList<>();

    private DietAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        setClickListeners();

        adapter = new DietAdapter(this, diets);

        ListView listView = findViewById(R.id.diet_list);
        listView.setAdapter(adapter);

        Amplify.API.query(
                ModelQuery.list(Diet.class),
                response -> {
                    if (response.getData() != null) {
                    response.getData().forEach(diet -> {
                        adapter.add(diet);
                        Log.i("MyAmplifyApp", "Title: " + diet.getName());
                    });
                }},
                failure -> Log.e("MyAmplifyApp", "Query failed.", failure)
        );

        adapter.notifyDataSetChanged();
    }

    private void setClickListeners() {
        findViewById(R.id.add_diet_button).setOnClickListener(view -> {
            TextInputLayout editText = findViewById(R.id.add_diet_text);

            if (editText.getEditText().getText().toString().isBlank()) {
                return;
            }

            Diet diet = Diet.builder()
                    .name(editText.getEditText().getText().toString())
                    .build();

            Amplify.API.mutate(ModelMutation.create(diet),
                    success -> Log.i("MyAmplifyApp", "Created a new diet successfully"),
                    error -> Log.e("MyAmplifyApp", "Error creating diet", error)
            );

            adapter.add(diet);
            adapter.notifyDataSetChanged();
        });
    }
}