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
    private static final int ALL_NEWS = 1000020;
    private static final int VINNUTSIA = 1000019;
    private static final int VOLYN = 1000017;
    private static final int DNIPROPETROVSK = 1000032;
    private static final int TRANSCARPATHIAN = 1000051;
    private static final int ZAPORIZHZHIA = 1000048;
    private static final int IVANO_FRANKIVSK = 1000003;
    private static final int KYIV = 1000047;
    private static final int KIROVOHRAD = 1000045;
    private static final int LVIV = 1000009;
    private static final int MYKOLAYIV = 1000026;
    private static final int ODESA = 1000015;
    private static final int RIVNE = 1000010;
    private static final int TERNOPIL = 1000021;
    private static final int KHARKIV = 1000040;
    private static final int KHERSON = 1000029;
    private static final int KHMELNYTSKY = 1000036;
    private static final int CHERKASY = 1000000;
    private static final int CHERNIHIV = 1000011;
    private static final int CHERNIVTSI = 1000033;
    private static final int ZHYTOMYR = 1000053;
    private static final int POLTAVA = 1000052;
    private static final int LUHANSK = 1000034;
    private static final int DONETSK = 1000018;
    private static final int SUMY = 1000042;
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