package com.example.diet_helper.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Product;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.diet_helper.ImageResultActivity;
import com.example.diet_helper.MainActivity;
import com.example.diet_helper.ProductActivity;

import org.json.JSONArray;

import java.util.List;

public class ProductsService {

    private Activity activity;

    public ProductsService(Activity activity) {
        this.activity = activity;
    }

    public void checkForBadProducts(ContentResolver contentResolver, Uri imageToCheck, List<Product> badProducts) {
        try {
            Amplify.Storage.uploadInputStream(
                    "ingredients",
                    contentResolver.openInputStream(imageToCheck),
                    success -> Log.e("", ""),
                    error -> Log.e("MyAmplifyApp", "Identify text failed", error)
            );

            ANRequest.GetRequestBuilder getRequestBuilder = AndroidNetworking.get("http://10.0.2.2:8080/products/bad");

            badProducts.forEach(product -> getRequestBuilder.addQueryParameter("dietBadProducts", product.getName()));

            getRequestBuilder
                    .addQueryParameter("fileName", "public/ingredients")
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
                                myIntent.putExtra("badProducts", response.toString());
                                activity.startActivity(myIntent);
                            }
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
