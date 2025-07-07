package com.surajvanshsv.quoteapps.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.surajvanshsv.quoteapps.model.FavoriteQuote;

import java.util.List;

@Dao
public interface FavoriteQuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteQuote quote);

    @Delete
    void delete(FavoriteQuote quote);

    @Query("DELETE FROM favorite_quotes")
    void deleteAll();

    @Query("SELECT * FROM favorite_quotes ORDER BY id DESC")
    LiveData<List<FavoriteQuote>> getAllFavorites();

    @Query("SELECT * FROM favorite_quotes WHERE language = :lang ORDER BY id DESC")
    LiveData<List<FavoriteQuote>> getFavoritesByLanguage(String lang);
}
