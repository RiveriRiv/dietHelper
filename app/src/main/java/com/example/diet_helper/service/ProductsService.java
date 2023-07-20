package com.example.diet_helper.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.diet_helper.ImageResultActivity;
import com.example.diet_helper.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductsService {

    private Activity activity;

    public ProductsService(Activity activity) {
        this.activity = activity;
    }

    public void checkForBadProducts(ContentResolver contentResolver, Uri imageToCheck, List<String> productNames, String imageName) {
        try {
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
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("MyAmplifyApp", "Response: " + response);

                        if (response.length() > 0) {
                            Intent myIntent = new Intent(activity, ImageResultActivity.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            StringBuilder result = new StringBuilder();

                            if (response.length() == 0) {
                                activity.startActivity(myIntent);
                                return;
                            }

                            try {
                            for (int i = 0; i < response.length(); i++) {
                                if (i == 0) {
                                    result.append(((JSONObject) response.get(i)).get("productName"));
                                } else {
                                    result.append(", ").append(((JSONObject) response.get(i)).get("productName"));
                                }
                            }

                            } catch (JSONException e) {
                                Log.e("Bad result: ", e.getMessage());
                            }


                            myIntent.putExtra("badProducts", result.toString());
                            activity.startActivity(myIntent);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("MyAmplifyApp", "Error: " + error);
                    }
                });
    }
}
