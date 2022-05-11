package com.example.newsblender.classes;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.newsblender.MainActivity;
import com.example.newsblender.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TelegramNews {
    public TelegramNews(String ownerName, LocalDateTime date, String linkToNews, String body, ArrayList<String> photoLinks, boolean isSaved) {
        this.ownerName = ownerName;
        this.date = date;
        this.linkToNews = linkToNews;
        this.body = body;
        this.photoLinks = photoLinks;
        this.isSaved = isSaved;
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

    public CardView getNews(Context context) {
        CardView cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.news_card, null);
        ConstraintLayout constraintLayout = cardView.findViewById(R.id.newsCardConstraintLayout);
//        constraintLayout.setBackgroundColor(context.getColor(R.color.gray_tint));

        /* Initialize ownerName */
        TextView ownerName = constraintLayout.findViewById(R.id.ownerName);
        ownerName.setMaxWidth(900);
        ownerName.setText(Html.fromHtml(getOwnerName()));

        /* Initialize body */
        TextView body = constraintLayout.findViewById(R.id.body);
        body.setText(Html.fromHtml(getBody()));

        /* Initialize date */
        TextView date = constraintLayout.findViewById(R.id.date);
        date.setText(getDate().toString());

        /* Initialize photos */
        SliderView sliderView = constraintLayout.findViewById(R.id.imageSliderView);
        if (getPhotoLinks().size() > 0) {
            ImageView imageView = new ImageView(context);
            sliderView.setIndicatorEnabled(true);
            sliderView.setSliderAdapter(new CustomSliderAdapter(context, photoLinks), false);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setIndicatorSelectedColor(Color.BLACK);
            sliderView.setIndicatorUnselectedColor(Color.WHITE);
            sliderView.setScrollTimeInSec(3);
            sliderView.setIndicatorRadius(5);
            sliderView.addView(imageView);
            sliderView.setMinimumHeight(768);
        } else {
            constraintLayout.removeView(sliderView);
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            set.connect(body.getId(), ConstraintSet.TOP, ownerName.getId(), ConstraintSet.BOTTOM);
            set.applyTo(constraintLayout);
        }

        /* Initialize button */
        ImageView detailsBtn = constraintLayout.findViewById(R.id.buttonDetails);
        detailsBtn.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate((isSaved ? R.layout.pop_up_window_news_saved : R.layout.pop_up_window_news), null);
            DrawerLayout mDrawerLayout = ((MainActivity) context).findViewById(R.id.drawer_layout);

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            /* When popup is dismissed */
            popupWindow.setOnDismissListener(() -> {
                if (mDrawerLayout.getChildAt(mDrawerLayout.getChildCount() - 1).getClass().equals(View.class))
                    mDrawerLayout.removeViewAt(mDrawerLayout.getChildCount() - 1);
            });

            popupWindow.showAtLocation(view, Gravity.END | Gravity.TOP, 5, Util.getLocationOnScreen(view).y + view.getHeight() + 15);

            popupView.findViewById(R.id.shareTextView).setOnClickListener(item -> {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "t.me/" + getLinkToNews());
                context.startActivity(Intent.createChooser(sharingIntent, "Share text via"));
            });
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference savedNews = db.collection("saved_news");
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            if (isSaved) {
                popupView.findViewById(R.id.deleteTextView).setOnClickListener(item -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage(context.getString(R.string.are_you_sure));

                    builder.setTitle(context.getString(R.string.deleting));

                    builder.setCancelable(false);

                    builder.setPositiveButton(
                            context.getString(R.string.yes),
                            (dialog, which) -> {
                                savedNews.whereEqualTo("Uid", Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).whereEqualTo("link_to_news", getLinkToNews()).get().addOnCompleteListener(task -> {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                });
                                popupWindow.dismiss();
                                ((MainActivity) context).recreate();
                            });

                    builder.setNegativeButton(
                            context.getString(R.string.no),
                            (dialog, which) -> {
                                // If user click no
                                // then dialog box is canceled.
                                dialog.cancel();
                            });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();

                    // Show the Alert Dialog box
                    alertDialog.show();

                });
            } else {
                popupView.findViewById(R.id.saveTextView).setOnClickListener(item -> {

                    Map<String, Object> saved_news = new HashMap<>();
                    saved_news.put("body", getBody());
                    saved_news.put("date", new Timestamp(Date.from(getDate().toInstant(ZoneOffset.UTC))));
                    saved_news.put("link_to_news", getLinkToNews());
                    saved_news.put("owner_name", getOwnerName());
                    saved_news.put("photo_links", getPhotoLinks());
                    saved_news.put("Uid", Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                    saved_news.put("date_added", Timestamp.now());
                    savedNews.document().set(saved_news).addOnCompleteListener(runnable -> {
                        Toast.makeText(context, "Success added news", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    });
                });
            }

            View view1 = new View(context);
            view1.setBackgroundResource(R.drawable.background_tint);
            mDrawerLayout.addView(view1);
        });
        return cardView;
    }

    private final String ownerName;
    private final LocalDateTime date;
    private final String linkToNews;
    private final String body;
    private final ArrayList<String> photoLinks;
    private final boolean isSaved;
}
