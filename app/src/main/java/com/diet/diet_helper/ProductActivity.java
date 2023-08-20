package com.diet.diet_helper;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.androidnetworking.AndroidNetworking;
import com.diet.diet_helper.dao.AppDatabase;
import com.diet.diet_helper.dao.ProductDao;
import com.diet.diet_helper.dao.ProductDietDao;
import com.diet.diet_helper.model.Product;
import com.diet.diet_helper.service.ProductsService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductActivity extends BaseActivity {

    private Uri imageToCheck;

    private String imageName;

    private ProductsService productsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_product);

        productsService = new ProductsService(this);

        final ChipGroup chipGroup = findViewById(R.id.product_list);

        Long dietId = (Long) getIntent().getExtras().get("diet");

        AppDatabase appDatabase = ((MyAmplifyApp) getApplicationContext()).getAppDatabase();
        final ProductDietDao dietDao = appDatabase.productDietDao();
        final ProductDao productDao = appDatabase.productDao();

        AsyncTask.execute(() -> dietDao.loadDietAndProducts(dietId).forEach((key, value) -> {

            if ((List<Product>) value != null) {
                runOnUiThread(() -> {
                    chipGroup.removeAllViews();
                    ((List<Product>) value).forEach(product -> {
                        Chip chip = new Chip(chipGroup.getContext());
                        chip.setText(product.getName());
                        chip.setCloseIconVisible(true);

                        chip.setOnCloseIconClickListener(v -> {
                            chipGroup.removeView(chip);
                            AsyncTask.execute(() -> productDao.deleteProduct(product));
                        });
                        chipGroup.addView(chip);
                    });
                    chipGroup.requestLayout();
                });
            }
        }));

        setClickListeners(chipGroup);

        Toolbar toolbar = findViewById(R.id.my_toolbar_product);
        setSupportActionBar(toolbar);
    }

    private void setClickListeners(final ChipGroup chipGroup) {
        findViewById(R.id.add_product_button).setOnClickListener(view -> {
            TextInputLayout editText = findViewById(R.id.add_product_text);

            if (editText.getEditText().getText().toString().isBlank()) {
                return;
            }

            Long dietId = (Long) getIntent().getExtras().get("diet");
            Product product = new Product(editText.getEditText().getText().toString(), dietId);

            AppDatabase appDatabase = ((MyAmplifyApp) this.getApplicationContext()).getAppDatabase();
            final ProductDao productDao = appDatabase.productDao();

            AsyncTask.execute(() -> {
                productDao.insertProduct(product);

                runOnUiThread(() -> {
                    Chip chip = new Chip(chipGroup.getContext());
                    chip.setText(product.getName());
                    chip.setCloseIconVisible(true);

                    chip.setOnCloseIconClickListener(v -> {
                        chipGroup.removeView(chip);
                        AsyncTask.execute(() -> productDao.deleteProduct(product));
                    });
                    chipGroup.addView(chip);
                });
            });
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
            result -> {
                final ChipGroup chipGroup = findViewById(R.id.product_list);

                List<String> productNames = new ArrayList<>();

                int chipCount = chipGroup.getChildCount();
                for (int i = 0; i < chipCount; i++) {
                    View view = chipGroup.getChildAt(i);
                    if (view instanceof Chip) {
                        Chip chip = (Chip) view;
                        productNames.add(chip.getText().toString());
                    }
                }

                productsService.checkForBadProducts(getContentResolver(), imageToCheck, productNames, imageName);
            });


    ActivityResultLauncher<String> selectImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                imageName = "tmp_image_file" + new Date().getTime();
                imageToCheck = uri;

                final ChipGroup chipGroup = findViewById(R.id.product_list);

                List<String> productNames = new ArrayList<>();

                int chipCount = chipGroup.getChildCount();
                for (int i = 0; i < chipCount; i++) {
                    View view = chipGroup.getChildAt(i);
                    if (view instanceof Chip) {
                        Chip chip = (Chip) view;
                        productNames.add(chip.getText().toString());
                    }
                }

                productsService.checkForBadProducts(getContentResolver(), imageToCheck, productNames, imageName);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);

        addFlags(menu.findItem(R.id.action_flag_photo));

        return true;
    }
}
