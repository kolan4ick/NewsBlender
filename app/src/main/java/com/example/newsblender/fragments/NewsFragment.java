package com.example.newsblender.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.newsblender.R;
import com.example.newsblender.classes.ItemViewModel;
import com.example.newsblender.classes.TelegramNews;
import com.example.newsblender.classes.TelegramNewsContent;
import com.example.newsblender.classes.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {
    /* Constants for type of news */
    private static final int ALL_NEWS = R.id.nav_news;
    private static final int VINNUTSIA = R.id.nav_vinnutsia;
    private static final int VOLYN = R.id.nav_volyn;
    private static final int DNIPROPETROVSK = R.id.nav_dnipropetrovsk;
    private static final int TRANSCARPATHIAN = R.id.nav_transcarpathian;
    private static final int ZAPORIZHZHIA = R.id.nav_zaporizhzhia;
    private static final int IVANO_FRANKIVSK = R.id.nav_ivano_frankivsk;
    private static final int KYIV = R.id.nav_kyiv;
    private static final int KIROVOHRAD = R.id.nav_kirovohrad;
    private static final int LVIV = R.id.nav_lviv;
    private static final int MYKOLAYIV = R.id.nav_mykolayiv;
    private static final int ODESA = R.id.nav_odesa;
    private static final int RIVNE = R.id.nav_rivne;
    private static final int TERNOPIL = R.id.nav_ternopil;
    private static final int KHARKIV = R.id.nav_kharkiv;
    private static final int KHERSON = R.id.nav_kherson;
    private static final int KHMELNYTSKY = R.id.nav_khmelnytsky;
    private static final int CHERKASY = R.id.nav_cherkasy;
    private static final int CHERNIHIV = R.id.nav_chernihiv;
    private static final int CHERNIVTSI = R.id.nav_chernivtsi;
    private static final int ZHYTOMYR = R.id.nav_zhytomyr;
    private static final int POLTAVA = R.id.nav_poltava;
    private static final int LUHANSK = R.id.nav_luhansk;
    private static final int DONETSK = R.id.nav_donetsk;
    private static final int SUMY = R.id.nav_sumy;
    private static final HashMap<Integer, String> telegram_news = new HashMap<Integer, String>() {
    {
        put(ALL_NEWS, "https://allnews");
        put(VINNUTSIA, "https://t.me/vinnytskaODA");
        put(VOLYN, "https://t.me/volynskaODA");
        put(DNIPROPETROVSK, "https://t.me/dnipropetrovskaODA");
        put(TRANSCARPATHIAN, "https://t.me/zakarpatskaODA");
        put(ZAPORIZHZHIA, "https://t.me/starukhofficial");
        put(IVANO_FRANKIVSK, "https://t.me/onyshchuksvitlana");
        put(KYIV, "https://t.me/kyivoda");
        put(KIROVOHRAD, "https://t.me/chornamary");
        put(LVIV, "https://t.me/kozytskyy_maksym_official");
        put(MYKOLAYIV, "https://t.me/mykolaivskaODA");
        put(ODESA, "https://t.me/odeskaODA");
        put(RIVNE, "https://t.me/vitalykoval8");
        put(TERNOPIL, "https://t.me/ternopilskaODA");
        put(KHARKIV, "https://t.me/synegubov");
        put(KHERSON, "https://t.me/khersonskaODA");
        put(KHMELNYTSKY, "https://t.me/khmelnytskaODA");
        put(CHERKASY, "https://t.me/cherkaskaODA");
        put(CHERNIHIV, "https://t.me/chernigivskaODA");
        put(CHERNIVTSI, "https://t.me/chernivetskaODA");
        put(ZHYTOMYR, "https://t.me/zhytomyrskaODA");
        put(POLTAVA, "https://t.me/DMYTROLUNIN");
        put(LUHANSK, "https://t.me/luhanskaVTSA");
        put(DONETSK, "https://t.me/pavlokyrylenko_donoda");
        put(SUMY, "https://t.me/Zhyvytskyy");
    }};

    /* Variables */
    private ItemViewModel mViewModel;
    private ArrayList<TelegramNews> mTelegramNews;
    private ProgressBar mProgressBarNewsFragment;
    private ScrollView mScrollView;

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
        if (Util.isNetworkAvailable((ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            mViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
            mProgressBarNewsFragment = mView.findViewById(R.id.progressBarNewsFragment);
            mScrollView = mView.findViewById(R.id.scrollViewNewsFragment);
            assignNews();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
            builder.setMessage("You do not have access to the Internet, turn it on and restart the application")
                    .setTitle("Error");
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
        return mView;
    }

    public void assignNews() {
        if (mViewModel.getNewsNavigationTypeValue() == ALL_NEWS) {
            Toast.makeText(getContext(), "ALL", Toast.LENGTH_SHORT).show();
        } else {

            new TelegramNewsContent(new StringBuilder(Objects.requireNonNull(telegram_news.get(mViewModel.getNewsNavigationTypeValue()))), getContext(), mProgressBarNewsFragment, mScrollView).execute();
        }
    }
}