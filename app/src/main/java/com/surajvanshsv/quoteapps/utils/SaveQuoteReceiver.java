package com.surajvanshsv.quoteapps.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.surajvanshsv.quoteapps.data.db.QuoteDatabase;
import com.surajvanshsv.quoteapps.model.Quote;

import java.util.concurrent.Executors;

public class SaveQuoteReceiver extends BroadcastReceiver {

    public static final String EXTRA_QUOTE_BODY = "extra_quote_body";
    public static final String EXTRA_QUOTE_AUTHOR = "extra_quote_author";

    @Override
    public void onReceive(Context context, Intent intent) {
        String body = intent.getStringExtra(EXTRA_QUOTE_BODY);
        String author = intent.getStringExtra(EXTRA_QUOTE_AUTHOR);

        if (body != null && author != null) {
            // ✅ Use the correct 4-argument constructor for Quote
            Quote quote = new Quote(body.trim(), author.trim(), "english", "general");

            // Insert quote in background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                QuoteDatabase db = QuoteDatabase.getInstance(context);
                db.quoteDao().insert(quote);
            });
        }
    }
}
