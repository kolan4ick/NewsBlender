package com.example.newsblender.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.newsblender.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        findPreference("theme").setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.equals("Темна"))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            return true;
        });
        findPreference("font_size").setOnPreferenceChangeListener(((preference, newValue) -> {
//            TODO: change all visible texts according to chosen value
            return true;
        }));
        findPreference("font").setOnPreferenceChangeListener(((preference, newValue) -> {
//            TODO: change all visible texts according to chosen value
            return true;
        }));
        findPreference("language").setOnPreferenceChangeListener(((preference, newValue) -> {
//            TODO: change all visible texts according to chosen value
            return true;
        }));
    }

//    private void onClick() {
//        changeLocale("en");
//    }
//
//    private void changeLocale(String language) {
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        if (language == "uk") {
//            conf.setLocale(Locale.getDefault());
//        }
//        else {
//            conf.setLocale(new Locale(language));
//            res.updateConfiguration(conf, dm);
//        }
//    }
}