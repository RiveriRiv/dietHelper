package com.example.diet_helper;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.diet_helper.dao.AppDatabase;
import com.example.diet_helper.service.PropertiesReader;

import java.util.Locale;

public class MyAmplifyApp extends Application {

    private AppDatabase appDatabase;

    public void onCreate() {
        super.onCreate();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Configuration config = new Configuration();
            config.setLocale(Locale.getDefault());
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            appDatabase = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.NAME)
                    .fallbackToDestructiveMigration()
                    .build();

            PropertiesReader.initialize(this);
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
