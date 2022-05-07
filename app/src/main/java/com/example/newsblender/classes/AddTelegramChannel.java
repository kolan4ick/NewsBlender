package com.example.newsblender.classes;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.newsblender.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddTelegramChannel extends AsyncTask<Void, Void, Void> {
    private String link;
    private FirebaseAuth fAuth;
    private PopupWindow popupWindow;
    private PopupWindow parentPopupWindow;
    private String channelName;

    public AddTelegramChannel(String link, FirebaseAuth fAuth, PopupWindow popupWindow, PopupWindow parentPopupWindow) {
        this.link = link;
        this.fAuth = fAuth;
        this.popupWindow = popupWindow;
        this.parentPopupWindow = parentPopupWindow;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference newsResources = db.collection("news_resources");
        Document doc = null;
        try {
            doc = Jsoup.connect(this.link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        channelName = Objects.requireNonNull(doc.getElementsByClass("tgme_page_title").first()).select("span").text();
        Map<String, Object> newsResource = new HashMap<>();
        newsResource.put("channel_name", channelName);
        newsResource.put("selected", true);
        newsResource.put("channel_link", this.link);
        newsResource.put("Uid", Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        newsResource.put("date_added", Timestamp.now());
        newsResources.document().set(newsResource);
                    /*addOnCompleteListener(runnable -> {
                Toast.makeText(this, "Success added channel", Toast.LENGTH_SHORT).show();
            });*/
        return null;
    }

    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Toast.makeText(popupWindow.getContentView().getContext(), R.string.success, Toast.LENGTH_SHORT).show();

        ConstraintLayout constraintLayout = parentPopupWindow.getContentView().findViewById(R.id.scrollView2).findViewById(R.id.popUpNewsResourcesConstraintLayout);

        AppCompatCheckBox appCompatCheckBox = new AppCompatCheckBox(parentPopupWindow.getContentView().getContext());
        appCompatCheckBox.setButtonDrawable(R.drawable.checkbox);
        appCompatCheckBox.setText(channelName);
        appCompatCheckBox.setPadding(12, 0, 0, 0);
        constraintLayout.addView(appCompatCheckBox);
        appCompatCheckBox.setId(View.generateViewId());
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(appCompatCheckBox.getId(), ConstraintSet.TOP, constraintLayout.getChildAt(constraintLayout.getChildCount() - 2).getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(appCompatCheckBox.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        constraintSet.applyTo(constraintLayout);
        popupWindow.dismiss();
    }

}
