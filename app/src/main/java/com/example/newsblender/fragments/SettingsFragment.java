package com.example.newsblender.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.newsblender.MainActivity;
import com.example.newsblender.R;
import com.example.newsblender.classes.ItemViewModel;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {
    private ItemViewModel mViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        findPreference("theme").setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.equals(getString(R.string.dark)))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            return true;
        });
        findPreference("font_size").setOnPreferenceChangeListener(((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        }));
        findPreference("font").setOnPreferenceChangeListener(((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        }));
        findPreference("language").setOnPreferenceChangeListener(((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        }));
    }
}