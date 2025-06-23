package com.surajvanshsv.quoteapps.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "favorite_quotes")
public class Quote {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String body;
    private String author;

    @Ignore
    private List<String> tags; // Not stored in Room, only used for API quotes

    public Quote() {}

    public Quote(String body, String author) {
        this.body = body;
        this.author = author;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
