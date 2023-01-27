package com.example.diet_helper;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.amplifyframework.core.Amplify;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Uri imageToCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        setClickListeners();
    }

    private void setClickListeners() {
        findViewById(R.id.take_image_button).setOnClickListener(view -> {
            createTmpFileUri();
            mGetContent.launch(imageToCheck);
        });

        findViewById(R.id.select_image_button).setOnClickListener(view -> {
            selectImageFromGallery.launch("image/*");
        });
    }

    ActivityResultLauncher<Uri> mGetContent = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> checkForBadProducts());


    ActivityResultLauncher<String> selectImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                imageToCheck = uri;
                checkForBadProducts();
            });

    private void createTmpFileUri() {
        try {
            imageToCheck = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File.createTempFile("tmp_image_file", ".jpg",
                            getCacheDir()));
        } catch (IOException e) {
            Log.e("MyAmplifyApp", "Error occurred while creating tmp image: " + e);
        }
    }

    private void checkForBadProducts() {
        try {
            Amplify.Storage.uploadInputStream(
                    "ingredients",
                    getContentResolver().openInputStream(imageToCheck),
                    success -> Log.e("", ""),
                    error -> Log.e("MyAmplifyApp", "Identify text failed", error)
            );

            AndroidNetworking.get("http://10.0.2.2:8080/products/bad")
                    .addQueryParameter("fileName", "public/ingredients")
                    .addQueryParameter("dietBadProducts", "sugar")
                    .addQueryParameter("dietBadProducts", "wheat")
                    .setTag(this)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.i("MyAmplifyApp", "Response: " + response);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("MyAmplifyApp", "Error: " + error);
                        }
                    });
        } catch (Exception e) {
            Log.e("MyAmplifyApp", "Error occurred while checking for bad products: " + e);
        }
    }
}