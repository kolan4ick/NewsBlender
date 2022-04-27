package com.example.newsblender.classes;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<Integer> newsNavigationType;

    public ItemViewModel() {
        this.newsNavigationType = new MutableLiveData<>();
    }

    public Integer getNewsNavigationTypeValue() {
        return newsNavigationType.getValue();
    }

    public void setNewsNavigationTypeValue(Integer navigationType) {
        newsNavigationType.setValue(navigationType);
    }
}
