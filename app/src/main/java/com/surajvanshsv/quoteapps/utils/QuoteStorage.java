package com.surajvanshsv.quoteapps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.surajvanshsv.quoteapps.model.Quote;

public class QuoteStorage {
    private static final String PREFS_NAME = "quote_prefs";
    private static final String KEY_BODY = "quote_body";
    private static final String KEY_AUTHOR = "quote_author";

    public static void saveQuote(Context context, Quote quote) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_BODY, quote.getBody())
                .putString(KEY_AUTHOR, quote.getAuthor())
                .apply();
    }

    public static Quote getSavedQuote(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String body = prefs.getString(KEY_BODY, "No quote found");
        String author = prefs.getString(KEY_AUTHOR, "Unknown");
        return new Quote(body, author);
    }
}
