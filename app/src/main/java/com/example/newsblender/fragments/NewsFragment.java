package com.example.newsblender.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.newsblender.MainActivity;
import com.example.newsblender.R;
import com.example.newsblender.classes.ItemViewModel;
import com.example.newsblender.classes.TelegramNews;
import com.example.newsblender.classes.TelegramNewsContent;
import com.example.newsblender.classes.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {
    /* Constants for type of news */
    private static final int ALL_NEWS = 0;
    private static final int VINNUTSIA = 1;
    private static final int VOLYN = 2;
    private static final int DNIPROPETROVSK = 3;
    private static final int TRANSCARPATHIAN = 4;
    private static final int ZAPORIZHZHIA = 5;
    private static final int IVANO_FRANKIVSK = 6;
    private static final int KYIV = 7;
    private static final int KIROVOHRAD = 8;
    private static final int LVIV = 9;
    private static final int MYKOLAYIV = 10;
    private static final int ODESA = 11;
    private static final int RIVNE = 12;
    private static final int TERNOPIL = 13;
    private static final int KHARKIV = 14;
    private static final int KHERSON = 15;
    private static final int KHMELNYTSKY = 16;
    private static final int CHERKASY = 17;
    private static final int CHERNIHIV = 18;
    private static final int CHERNIVTSI = 19;
    private static final int ZHYTOMYR = 20;
    private static final int POLTAVA = 21;
    private static final int LUHANSK = 22;
    private static final int DONETSK = 23;
    private static final int SUMY = 24;
    private static String[] telegramLinks = {"_", "https://t.me/vinnytskaODA", "https://t.me/volynskaODA", "https://t.me/dnipropetrovskaODA",
            "https://t.me/zakarpatskaODA", "https://t.me/starukhofficial", "https://t.me/onyshchuksvitlana", "https://t.me/kyivoda",
            "https://t.me/chornamary", "https://t.me/kozytskyy_maksym_official", "https://t.me/mykolaivskaODA", "https://t.me/odeskaODA",
            "https://t.me/vitalykoval8", "https://t.me/ternopilskaODA", "https://t.me/synegubov", "https://t.me/khersonskaODA",
            "https://t.me/khmelnytskaODA", "https://t.me/cherkaskaODA", "https://t.me/chernigivskaODA", "https://t.me/chernivetskaODA",
            "https://t.me/zhytomyrskaODA", "https://t.me/DMYTROLUNIN", "https://t.me/luhanskaVTSA", "https://t.me/pavlokyrylenko_donoda",
            "https://t.me/Zhyvytskyy"};
    /* Variables */
    private ItemViewModel mViewModel;
    private ArrayList<TelegramNews> mTelegramNews;
    private ProgressBar mProgressBarNewsFragment;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_news, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        mProgressBarNewsFragment = mView.findViewById(R.id.progressBarNewsFragment);
        assignNews();
        return mView;
    }

    public void assignNews() {
        if (mViewModel.getNewsNavigationTypeValue() == ALL_NEWS) {
            Toast.makeText(getContext(), "ALL", Toast.LENGTH_SHORT).show();
        } else {
            new TelegramNewsContent(new StringBuilder(telegramLinks[mViewModel.getNewsNavigationTypeValue()]), getContext(), mProgressBarNewsFragment).execute();
        }
    }
}