package com.surajvanshsv.quoteapps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.surajvanshsv.quoteapps.model.Quote;

public class QuoteStorageHelper {

    private static final String PREF_NAME = "quotes_prefs";
    private static final String KEY_BODY = "last_body";
    private static final String KEY_AUTHOR = "last_author";

    /**
     * Save the last fetched quote to SharedPreferences
     */
    public static void saveLastQuote(Context context, Quote quote) {
        if (quote == null || quote.getBody() == null || quote.getAuthor() == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_BODY, quote.getBody().trim())
                .putString(KEY_AUTHOR, quote.getAuthor().trim())
                .apply();
    }

    /**
     * Load the last saved quote from SharedPreferences
     */
    public static Quote getLastQuote(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String body = prefs.getString(KEY_BODY, null);
        String author = prefs.getString(KEY_AUTHOR, null);

        if (body != null && !body.trim().isEmpty()
                && author != null && !author.trim().isEmpty()) {
            return new Quote(body.trim(), author.trim());
        } else {
            return null;
        }
    }

    /**
     * Clear the saved quote from SharedPreferences
     */
    public static void clearLastQuote(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_BODY).remove(KEY_AUTHOR).apply();
    }
}
