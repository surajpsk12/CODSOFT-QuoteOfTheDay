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

    @Insert
    void insert(Quote quote);

    @Delete
    void delete(Quote quote);

    @Query("DELETE FROM favorite_quotes")
    void deleteAll();

    @Query("SELECT * FROM favorite_quotes ORDER BY id DESC")
    LiveData<List<Quote>> getAllQuotes();
}
