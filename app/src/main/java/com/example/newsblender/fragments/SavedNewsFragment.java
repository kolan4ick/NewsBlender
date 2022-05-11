package com.example.newsblender.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newsblender.R;
import com.example.newsblender.classes.TelegramNews;
import com.example.newsblender.classes.TelegramNewsContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

public class SavedNewsFragment extends Fragment {
    private View mView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser fUser;
    private FirebaseAuth fAuth;

    public static SavedNewsFragment newInstance(String param1, String param2) {
        SavedNewsFragment fragment = new SavedNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_saved_news, container, false);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<TelegramNews> telegramNews = new ArrayList<>();

        db.collection("saved_news").whereEqualTo("Uid", fUser.getUid()).orderBy("date_added", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String body = document.getString("body");
                    String linkToNews = document.getString("link_to_news");
                    String ownerName = document.getString("owner_name");
                    LocalDateTime localDateTime = convertToLocalDateViaInstant(Objects.requireNonNull(document.getDate("date")).toInstant());
                    ArrayList<String> links_to_photos = (ArrayList<String>) document.get("photo_links");
                    telegramNews.add(new TelegramNews(ownerName, localDateTime, linkToNews, body, links_to_photos, true));
                }
                new TelegramNewsContent(telegramNews, getContext(), mView.findViewById(R.id.progressBarSavedNewsFragment), mView.findViewById(R.id.scrollViewSavedNewsFragment)).execute();
            } else {
                Log.d("DEBUG", "Error getting documents: ", task.getException());
            }
        });

        return mView;
    }

    public LocalDateTime convertToLocalDateViaInstant(Instant dateToConvert) {
        return dateToConvert.atZone(ZoneId.of("Europe/Kiev")).toLocalDateTime();
    }
}