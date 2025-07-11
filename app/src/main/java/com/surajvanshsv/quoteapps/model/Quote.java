package com.surajvanshsv.quoteapps.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "quotes")
public class Quote {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String body;
    private String author;

    // 👇 Add this field for language (new)
    @NonNull
    private String language = "english"; // default if not set

    private String emotion;

    public Quote(String body, String author, String language, String emotion) {
        this.body = body;
        this.author = author;
        this.language = (language != null) ? language : "english";
        this.emotion = emotion;
    }

    // ✅ Add empty constructor for Room if needed
    public Quote() {}

    // 🔻 Getters and setters
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
