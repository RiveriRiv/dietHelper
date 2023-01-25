package com.example.diet_helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.amplifyframework.predictions.models.TextFormatType;
import com.amplifyframework.predictions.result.IdentifyTextResult;
import com.amplifyframework.rx.RxAmplify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Uri tmpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setClickListeners();
    }

    private void setClickListeners() {
        findViewById(R.id.take_image_button).setOnClickListener(view -> {
            getTmpFileUri();
            mGetContent.launch(tmpFile);
        });

        findViewById(R.id.select_image_button).setOnClickListener(view -> {
            selectImageFromGallery.launch("image/*");
        });
    }

    ActivityResultLauncher<Uri> mGetContent = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpFile);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.WEBP,0,stream);
                    byte[] byteArray = stream.toByteArray();

                    Bitmap yourCompressBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                    RxAmplify.Predictions.identify(TextFormatType.PLAIN, yourCompressBitmap)
                            .subscribe(
                                    resultText -> {
                                        IdentifyTextResult identifyResult = (IdentifyTextResult) resultText;
                                        Log.i("MyAmplifyApp", identifyResult.getFullText());
                                    },
                                    error -> Log.e("MyAmplifyApp", "Identify text failed", error)
                            );
                } catch (Exception e) {

                }

            });


    ActivityResultLauncher<String> selectImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
            });

    private Uri getTmpFileUri() {
        try {
            tmpFile = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File.createTempFile("tmp_image_file", ".png",
                            getCacheDir()));
        } catch (IOException e) {

        }

        return tmpFile;
    }
}