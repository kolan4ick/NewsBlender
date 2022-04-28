package com.example.newsblender.classes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.newsblender.MainActivity;
import com.smarteist.autoimageslider.SliderView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;

public class TelegramNewsContent extends AsyncTask<Void, Void, Void> {
    private StringBuilder link;
    private ArrayList<TelegramNews> telegramNews;
    private Context context;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    public TelegramNewsContent(StringBuilder link, Context context, ProgressBar progressBar, ScrollView scrollView) {
        this.link = link;
        this.context = context;
        this.progressBar = progressBar;
        this.scrollView = scrollView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        TimeZone.setDefault(TimeZone.getTimeZone("Ukraine"));
        /* make link with "/s", for example: https://t.me/s/TCH_channel */
        link.insert(link.lastIndexOf("t.me") + 4, "/s");

        Document doc = null;
        try {
            doc = Jsoup.connect(link.toString()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements = doc.select("div .tgme_widget_message_wrap");

        /* creating variable for storing our news */
        ArrayList<TelegramNews> telegramNews = new ArrayList<>();

        elements.forEach(element -> {
            element.select(".tgme_widget_message_reply").remove();

            /* get link to message */
            String linkToNews = Objects.requireNonNull(element.select(".tgme_widget_message").first()).attr("data-post");

            /* get name of channel */
            String ownerName = element.select(".tgme_widget_message_owner_name").text();

            /* get Text of news */
            String body = element.select(".tgme_widget_message_text").text();

            /* get news photos if exists */
            ArrayList<String> photo_links = new ArrayList<>();
            Elements photo_elements = element.select(".tgme_widget_message_photo_wrap");
            photo_elements.forEach(photo_element -> {
                String[] style_attributes = photo_element.attr("style").split("'");
                if (style_attributes.length > 0) {
                    photo_links.add(style_attributes[1]);
                }
            });
            /* get Date and Time of creating news */
            String inputValue = element.select(".time").attr("datetime");
            LocalDateTime localDateTime = LocalDateTime.parse(inputValue, DateTimeFormatter.ISO_DATE_TIME).plusHours(ZoneId.of("Europe/Kiev").getRules().getOffset(Instant.now()).getTotalSeconds() / 3600);
            telegramNews.add(new TelegramNews(ownerName, localDateTime, linkToNews, body, photo_links));
        });
        this.telegramNews = telegramNews;
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progressBar.setVisibility(View.GONE);
        telegramNews.forEach(item -> ((LinearLayout) scrollView.getChildAt(0)).addView(item.getNews(context)));
    }
}
