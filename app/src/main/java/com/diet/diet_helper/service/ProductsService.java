package com.diet.diet_helper.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.amplifyframework.core.Amplify;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.diet.diet_helper.ImageResultActivity;
import com.diet.diet_helper.R;

import java.util.List;

public class ProductsService {

    private Activity activity;

    public ProductsService(Activity activity) {
        this.activity = activity;
    }

    public void checkForBadProducts(ContentResolver contentResolver, Uri imageToCheck, List<String> productNames, String imageName) {
        try {
            activity.runOnUiThread(() -> activity.findViewById(R.id.overlayLayout).setVisibility(View.VISIBLE));

            Amplify.Storage.uploadInputStream(
                    imageName,
                    contentResolver.openInputStream(imageToCheck),
                    success -> check(productNames, imageName),
                    error -> Log.e("MyAmplifyApp", "Identify text failed", error)
            );
        } catch (Exception e) {
            Log.e("MyAmplifyApp", "Error occurred while checking for bad products: " + e);
        }

    }

    public void check(List<String> productNames, String imageName) {
        ANRequest.GetRequestBuilder getRequestBuilder = AndroidNetworking.get(PropertiesReader.getDietServiceUrl());

        productNames.forEach(productName -> getRequestBuilder.addQueryParameter("dietBadProducts", productName));

        getRequestBuilder
                .addQueryParameter("fileName", "public/" + imageName)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("MyAmplifyApp", "Response: " + response);

                        Intent myIntent = new Intent(activity, ImageResultActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        myIntent.putExtra("badProducts", response);
                        activity.startActivity(myIntent);
                        activity.findViewById(R.id.overlayLayout).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("MyAmplifyApp", "Error: " + error);
                        activity.runOnUiThread(() -> activity.findViewById(R.id.overlayLayout).setVisibility(View.GONE));
                    }
                });
    }
}
