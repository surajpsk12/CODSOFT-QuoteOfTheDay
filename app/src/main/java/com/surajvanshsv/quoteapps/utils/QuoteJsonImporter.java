package com.surajvanshsv.quoteapps.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.surajvanshsv.quoteapps.data.db.QuoteDao;
import com.surajvanshsv.quoteapps.model.Quote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class QuoteJsonImporter {

    private static final String PREF_NAME = "quote_pref";
    private static final String KEY_IMPORTED = "hindi_quotes_imported";

    // 🧠 Nested static class to match the JSON structure
    private static class QuoteListWrapper {
        @SerializedName("quotes")
        List<Quote> quotes;
    }

    public static void importHindiQuotes(Context context, QuoteDao dao) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (prefs.getBoolean(KEY_IMPORTED, false)) {
            Log.d("QuoteImport", "Hindi quotes already imported. Skipping.");
            return;
        }

        try {
            // Load hindi_quotes.json from res/raw
            InputStream is = context.getResources().openRawResource(
                    context.getResources().getIdentifier("hindi_quotes", "raw", context.getPackageName())
            );
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Parse JSON into List<Quote>
            Gson gson = new Gson();
            QuoteListWrapper wrapper = gson.fromJson(builder.toString(), QuoteListWrapper.class);
            List<Quote> quotes = wrapper.quotes;

            if (quotes != null && !quotes.isEmpty()) {
                dao.insertQuotes(quotes);
                prefs.edit().putBoolean(KEY_IMPORTED, true).apply();
                Log.d("QuoteImport", "Inserted " + quotes.size() + " Hindi quotes into Room.");
            } else {
                Log.e("QuoteImport", "Parsed quote list is empty.");
            }
        } catch (Exception e) {
            Log.e("QuoteImport", "Failed to import Hindi quotes: " + e.getMessage(), e);
        }
    }
}
