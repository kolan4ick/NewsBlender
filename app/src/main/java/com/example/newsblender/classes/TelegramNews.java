package com.example.newsblender.classes;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.newsblender.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TelegramNews {
    public TelegramNews(String ownerName, LocalDateTime date, String linkToNews, String body, ArrayList<String> photoLinks) {
        this.ownerName = ownerName;
        this.date = date;
        this.linkToNews = linkToNews;
        this.body = body;
        this.photoLinks = photoLinks;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLinkToNews() {
        return linkToNews;
    }

    public String getBody() {
        return body;
    }

    public ArrayList<String> getPhotoLinks() {
        return photoLinks;
    }

    public ConstraintLayout getNews(Context context) {
        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 20, 30, 0);
        constraintLayout.setLayoutParams(layoutParams);
        ConstraintSet set = new ConstraintSet();
        constraintLayout.setBackgroundColor(context.getColor(R.color.gray_tint));

        /* Initialize ownerName */
        TextView ownerName = new TextView(context);
        ownerName.setText(Html.fromHtml(getOwnerName()));
        ownerName.setId(View.generateViewId());

        /* Initialize body */
        TextView body = new TextView(context);
        body.setText(Html.fromHtml(getBody()));
        body.setId(View.generateViewId());

        /* Initialize date */
        TextView date = new TextView(context);
        date.setText(getDate().toString());
        date.setId(View.generateViewId());

        /* Initialize photos */
        SliderView sliderView = new SliderView(context);
        sliderView.setId(View.generateViewId());
        if (getPhotoLinks().size() > 0) {
            ImageView imageView = new ImageView(context);
            imageView.setId(View.generateViewId());
            sliderView.setIndicatorEnabled(true);
            sliderView.setSliderAdapter(new CustomSliderAdapter(context, photoLinks), false);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setIndicatorSelectedColor(Color.BLACK);
            sliderView.setIndicatorUnselectedColor(Color.WHITE);
            sliderView.setScrollTimeInSec(3);
            sliderView.setIndicatorRadius(5);
            sliderView.addView(imageView);
        }

        /* Add all views to ConstraintLayout */
        sliderView.setMinimumHeight(768);
        constraintLayout.addView(ownerName);
        Log.println(Log.DEBUG, "Custom", Integer.toString(photoLinks.size()));
        if (photoLinks.size() > 0)
            constraintLayout.addView(sliderView);
        constraintLayout.addView(body);
        constraintLayout.addView(date);
        set.clone(constraintLayout);
        set.connect(ownerName.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 60);
        if (photoLinks.size() > 0)
            set.connect(sliderView.getId(), ConstraintSet.TOP, ownerName.getId(), ConstraintSet.BOTTOM, 60);
        set.connect(body.getId(), ConstraintSet.TOP, photoLinks.size() > 0 ? sliderView.getId() : ownerName.getId(), ConstraintSet.BOTTOM, 60);
        set.connect(date.getId(), ConstraintSet.TOP, body.getId(), ConstraintSet.BOTTOM, 10);
        set.applyTo(constraintLayout);

        return constraintLayout;
    }

    private final String ownerName;
    private final LocalDateTime date;
    private final String linkToNews;
    private final String body;
    private final ArrayList<String> photoLinks;
}
