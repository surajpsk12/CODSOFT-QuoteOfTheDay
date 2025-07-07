package com.surajvanshsv.quoteapps.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.surajvanshsv.quoteapps.model.Quote;

import java.util.List;

@Dao
public interface QuoteDao {

    // ✅ Insert a single quote
    @Insert
    void insert(Quote quote);

    // ✅ Insert a list of quotes (for JSON import)
    @Insert
    void insertQuotes(List<Quote> quoteList);

    @Delete
    void delete(Quote quote);

    @Query("DELETE FROM quotes")
    void deleteAll();

    // ✅ Get all quotes
    @Query("SELECT * FROM quotes ORDER BY id DESC")
    LiveData<List<Quote>> getAllQuotes();

    // ✅ Get quotes filtered by language
    @Query("SELECT * FROM quotes WHERE language = :lang ORDER BY id DESC")
    LiveData<List<Quote>> getQuotesByLanguage(String lang);

    // ✅ Blocking version (for random quote fetching)
    @Query("SELECT * FROM quotes WHERE language = :lang")
    List<Quote> getQuotesByLanguageBlocking(String lang);
}
