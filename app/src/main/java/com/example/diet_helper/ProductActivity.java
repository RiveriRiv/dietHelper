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

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.QueryOptions;
import com.amplifyframework.core.model.query.predicate.QueryPredicate;
import com.amplifyframework.datastore.generated.model.Diet;
import com.amplifyframework.datastore.generated.model.Product;
import com.androidnetworking.AndroidNetworking;
import com.example.diet_helper.service.ProductsService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private Uri imageToCheck;

    private String imageName;

    private static List<Product> products = new ArrayList<>();

    private ProductAdapter adapter;

    private ProductsService productsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_product);

        productsService = new ProductsService(this);

        final ChipGroup chipGroup = findViewById(R.id.product_list);

        String dietId = (String) getIntent().getExtras().get("diet");

            Amplify.API.query(
                ModelQuery.get(Diet.class, dietId),
                response -> {
                    Diet foundDiet = (Diet) (response.getData());
                    List<Product> products = foundDiet.getProducts();

                    if (products != null) {
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                        chipGroup.removeAllViews();
                        products.forEach(product -> {
                            Chip chip = new Chip(chipGroup.getContext());
                            chip.setText(product.getName());
                            chip.setCloseIconVisible(true);
                            chipGroup.addView(chip);
                        });
                       chipGroup.requestLayout();
                    }});
                    }},
                error -> Log.e("MyAmplifyApp", error.toString(), error)
        );

        setClickListeners(chipGroup);
    }

    private void setClickListeners(final ChipGroup chipGroup) {
        findViewById(R.id.add_product_button).setOnClickListener(view -> {
            TextInputLayout editText = findViewById(R.id.add_product_text);

            if (editText.getEditText().getText().toString().isBlank()) {
                return;
            }

            String dietId = (String) getIntent().getExtras().get("diet");
            Product product = Product.builder()
                    .name(editText.getEditText().getText().toString())
                    .dietProductsId(dietId)
                    .build();

            Amplify.API.query(
                    ModelQuery.get(Diet.class, dietId),
                    response -> {
                        Diet foundDiet = (Diet) (response.getData());

                        foundDiet.getProducts().add(product);

                        Amplify.API.mutate(ModelMutation.update(foundDiet),
                                success -> Log.i("MyAmplifyApp", "Created a new product successfully"),
                                error -> Log.e("MyAmplifyApp",  "Error creating product", error)
                        );
                    },
                    error -> Log.e("MyAmplifyApp", error.toString(), error)
            );

            Chip chip = new Chip(this);
            chip.setText(product.getName());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                chipGroup.removeView(chip);
                Amplify.DataStore.delete(product,
                        success -> Log.i("MyAmplifyApp", "Deleted the product successfully"),
                        error -> Log.e("MyAmplifyApp",  "Error deleting product", error)
                );
            });
            chipGroup.addView(chip);
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
            result -> productsService.checkForBadProducts(getContentResolver(), imageToCheck, products, imageName));


    ActivityResultLauncher<String> selectImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                imageName = "tmp_image_file" + new Date().getTime();
                imageToCheck = uri;
                productsService.checkForBadProducts(getContentResolver(), imageToCheck, products, imageName);
            });

    private void createTmpFileUri() {
        try {
            imageName = "tmp_image_file" + new Date().getTime();
            imageToCheck = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File.createTempFile(imageName, ".jpg",
                            getCacheDir()));
        } catch (IOException e) {
            Log.e("MyAmplifyApp", "Error occurred while creating tmp image: " + e);
        }
    }
}
