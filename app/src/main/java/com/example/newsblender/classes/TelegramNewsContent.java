package com.example.newsblender.classes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.newsblender.MainActivity;

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

public class TelegramNewsContent extends AsyncTask<Void, Void, Void> {
    private StringBuilder link;
    private ArrayList<TelegramNews> telegramNews;
    private Context context;
    private ProgressBar progressBar;

    public TelegramNewsContent(StringBuilder link, Context context, ProgressBar progressBar) {
        this.link = link;
        this.context = context;
        this.progressBar = progressBar;
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

            /* get Date and Time of creating news */
            String inputValue = element.select(".time").attr("datetime");
            LocalDateTime localDateTime = LocalDateTime.parse(inputValue, DateTimeFormatter.ISO_DATE_TIME).plusHours(ZoneId.of("Europe/Kiev").getRules().getOffset(Instant.now()).getTotalSeconds() / 3600);
            telegramNews.add(new TelegramNews(ownerName, localDateTime, linkToNews, body));
        });
        this.telegramNews = telegramNews;
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(context, telegramNews.get(telegramNews.size() - 1).getBody(), Toast.LENGTH_SHORT).show();
    }
}
