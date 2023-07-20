package com.example.diet_helper.service;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static String dietServiceUrl;

    public static void initialize(Context context) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dietServiceUrl = properties.getProperty("dietServiceUrl");
    }

    public static String getDietServiceUrl() {
        return dietServiceUrl;
    }
}
