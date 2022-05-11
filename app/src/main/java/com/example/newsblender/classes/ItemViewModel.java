package com.example.newsblender.classes;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<Integer> newsNavigationType;
    private final MutableLiveData<NavController> navController;

    public ItemViewModel() {
        this.newsNavigationType = new MutableLiveData<>();
        this.navController = new MutableLiveData<>();
    }

    public Integer getNewsNavigationTypeValue() {
        return newsNavigationType.getValue();
    }

    public void setNewsNavigationTypeValue(Integer navigationType) {
        newsNavigationType.setValue(navigationType);
    }

    public NavController getNavigationViewValue() {
        return this.navController.getValue();
    }

    public void setNavigationView(NavController navigationView) {
        this.navController.setValue(navigationView);
    }
}
