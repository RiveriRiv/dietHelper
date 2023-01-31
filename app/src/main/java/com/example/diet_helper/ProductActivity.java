package com.example.diet_helper;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Diet;
import com.amplifyframework.datastore.generated.model.Product;
import com.androidnetworking.AndroidNetworking;
import com.example.diet_helper.service.ProductsService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private Uri imageToCheck;

    private static List<Product> products = new ArrayList<>();

    private ProductAdapter adapter;

    private ProductsService productsService;

    private Diet diet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_product);

        productsService = new ProductsService(this);

        setClickListeners();

        diet = (Diet) getIntent().getExtras().get("diet");
        List<Product> products = diet.getProducts();
        if (products != null) {
            this.products.addAll(products);
        }
        adapter = new ProductAdapter(this, this.products, (Diet) getIntent().getExtras().get("diet"));

        ListView listView = findViewById(R.id.product_list);
        listView.setAdapter(adapter);
    }

    private void setClickListeners() {
        findViewById(R.id.add_product_button).setOnClickListener(view -> {
            EditText editText = findViewById(R.id.add_product_text);

            if (editText.getText().toString().isBlank()) {
                return;
            }

            Product product = Product.builder()
                    .name(editText.getText().toString())
                    .dietProductsId(diet.getPrimaryKeyString())
                    .build();

            Amplify.DataStore.save(product,
                    success -> Log.i("MyAmplifyApp", "Created a new product successfully"),
                    error -> Log.e("MyAmplifyApp",  "Error creating product", error)
            );

            adapter.add(product);
            adapter.notifyDataSetChanged();
        });

        findViewById(R.id.back).setOnClickListener(view -> finish());

        findViewById(R.id.take_image_button).setOnClickListener(view -> {
            createTmpFileUri();
            mGetContent.launch(imageToCheck);
        });

        findViewById(R.id.select_image_button).setOnClickListener(view -> {
            selectImageFromGallery.launch("image/*");
        });
    }

    ActivityResultLauncher<Uri> mGetContent = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> productsService.checkForBadProducts(getContentResolver(), imageToCheck, products));


    ActivityResultLauncher<String> selectImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                imageToCheck = uri;
                productsService.checkForBadProducts(getContentResolver(), imageToCheck, products);
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
}
