package com.example.newsblender.classes;

import java.time.LocalDateTime;
import java.util.Date;

public class TelegramNews {
    public TelegramNews(String ownerName, LocalDateTime date, String linkToNews, String body) {
        this.ownerName = ownerName;
        this.date = date;
        this.linkToNews = linkToNews;
        this.body = body;
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

    private String ownerName;
    private LocalDateTime date;
    private String linkToNews;
    private String body;
}
