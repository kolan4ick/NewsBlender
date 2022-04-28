package com.example.newsblender.classes;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.example.newsblender.R;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        TextView ownerName = new TextView(context);
        ownerName.setText(Html.fromHtml(getOwnerName()));
        ownerName.setId(View.generateViewId());

        TextView body = new TextView(context);
        body.setText(Html.fromHtml(getBody()));
        body.setId(View.generateViewId());

        TextView date = new TextView(context);
        date.setText(getDate().toString());
        date.setId(View.generateViewId());

        SliderView sliderView = new SliderView(context);
        sliderView.setId(View.generateViewId());
        if (getPhotoLinks().size() > 0) {
            getPhotoLinks().forEach(photoLink -> {
                ImageView imageView = new ImageView(context);
                Glide.with(context).load(photoLink).into(imageView);
                sliderView.addView(imageView);
            });
        }

        constraintLayout.addView(ownerName, 0);
        constraintLayout.addView(body, 1);
        constraintLayout.addView(date, 2);
        constraintLayout.addView(sliderView);
        set.clone(constraintLayout);
        set.connect(ownerName.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 60);
        set.connect(sliderView.getId(), ConstraintSet.TOP, ownerName.getId(), ConstraintSet.TOP, 60);
        set.connect(body.getId(), ConstraintSet.TOP, sliderView.getId(), ConstraintSet.TOP, 60);
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
