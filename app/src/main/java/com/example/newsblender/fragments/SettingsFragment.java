package com.example.newsblender.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.preference.PreferenceFragmentCompat;

import com.example.newsblender.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
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