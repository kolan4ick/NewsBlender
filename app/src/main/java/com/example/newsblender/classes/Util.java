package com.example.newsblender.classes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

public class Util {
    public static ArrayList<TelegramNews> extractionNewsFromTelegramLink(StringBuilder link) throws IOException {

        /* make link with "/s", for example: https://t.me/s/TCH_channel */
        link.insert(link.lastIndexOf("t.me") + 4, "/s");

        Document doc = Jsoup.connect(link.toString()).get();
        Elements elements = doc.select("div .tgme_widget_message_wrap");

        /* creating variable for storing our news */
        ArrayList<TelegramNews> telegramNews = new ArrayList<>();

        /* get link to message */
        /* get name of channel */
        /* get Text of news */
        /* get Date and Time of creating news */
        elements.forEach(element -> {
            element.select(".tgme_widget_message_reply").remove();
            String linkToNews = Objects.requireNonNull(element.select(".tgme_widget_message").first()).attr("data-post");
            String ownerName = element.select(".tgme_widget_message_owner_name").text();
            String body = element.select(".tgme_widget_message_text").text();
            String inputValue = element.select(".time").attr("datetime");
            Instant timestamp = Instant.parse(inputValue);
            LocalDateTime dateTime = timestamp.atZone(ZoneId.of("Europe/Kiev")).toLocalDateTime();
            telegramNews.add(new TelegramNews(ownerName, dateTime, linkToNews, body));
        });
        return telegramNews;
    }
}
