package com.example.diet_helper;

import android.content.Context;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private static Locale currentLocale;

    @Override
    protected void attachBaseContext(Context newBase) {
        if (currentLocale != null) {
            Configuration config = newBase.getResources().getConfiguration();
            config.setLocale(currentLocale);
            Context context = newBase.createConfigurationContext(config);
            super.attachBaseContext(context);
        } else {
            super.attachBaseContext(newBase);
            currentLocale = Locale.getDefault();
        }
    }

    public void addFlags(MenuItem flagItem) {
        View flagView = flagItem.getActionView();

        Locale locale = BaseActivity.getCurrentLocale();

        ImageView flagButton = flagView.findViewById(R.id.button_flag);

        flagButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.flag_gb, getTheme()));

        if (locale.getLanguage().startsWith("ru")) {
            flagButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.flag_ru, getTheme()));
        } else if (locale.getLanguage().startsWith("de")) {
            flagButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.flag_de, getTheme()));
        } else if (locale.getLanguage().startsWith("uk")) {
            flagButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.flag_uk, getTheme()));
        }

        flagButton.setOnClickListener(this::showFlagPopupMenu);
    }

    private void showFlagPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_flags, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            Locale locale = new Locale("en");
            int selectedItem = item.getItemId();

            if (selectedItem == R.id.flag_ru) {
                locale = new Locale("ru");
            } else if (selectedItem == R.id.flag_de) {
                locale = new Locale("de");
            } else if (selectedItem == R.id.flag_uk) {
                locale = new Locale("uk");
            }

            BaseActivity.setCurrentLocale(locale);
            recreate();

            return true;
        });

        popupMenu.show();
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
