package com.surajvanshsv.quoteapps.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_quotes")
public class FavoriteQuote {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String body;
    private String author;

    @NonNull
    private String language = "english"; // default fallback

    private String emotion;

    public FavoriteQuote(String body, String author, String language, String emotion) {
        this.body = body;
        this.author = author;
        this.language = (language != null && !language.isEmpty()) ? language : "english";
        this.emotion = emotion;
    }

    public FavoriteQuote() {}

    // ✅ Convert this FavoriteQuote into a regular Quote object
    public Quote toQuote() {
        Quote quote = new Quote(body, author, language, emotion);
        quote.setId(id); // preserve ID
        return quote;
    }

    // ✅ Static method to convert from Quote to FavoriteQuote
    public static FavoriteQuote fromQuote(Quote quote) {
        FavoriteQuote fav = new FavoriteQuote(
                quote.getBody(),
                quote.getAuthor(),
                quote.getLanguage(),
                quote.getEmotion()
        );
        return fav;
    }

    // 🔻 Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) {
        this.language = (language == null || language.isEmpty()) ? "english" : language;
    }

    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
}
