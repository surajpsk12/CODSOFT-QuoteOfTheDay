package com.surajvanshsv.quoteapps.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.model.FavoriteQuote; // ✅ Add import

@Database(entities = {Quote.class, FavoriteQuote.class}, version = 4, exportSchema = false) // ✅ Add FavoriteQuote
public abstract class QuoteDatabase extends RoomDatabase {

    private static QuoteDatabase instance;

    public abstract QuoteDao quoteDao();
    public abstract com.surajvanshsv.quoteapps.data.db.FavoriteQuoteDao favoriteQuoteDao(); // ✅ Add DAO

    public static synchronized QuoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            QuoteDatabase.class, "quote_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
