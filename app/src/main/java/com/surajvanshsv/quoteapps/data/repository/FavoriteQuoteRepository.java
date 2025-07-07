package com.surajvanshsv.quoteapps.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.surajvanshsv.quoteapps.data.db.FavoriteQuoteDao;
import com.surajvanshsv.quoteapps.data.db.QuoteDatabase;
import com.surajvanshsv.quoteapps.model.FavoriteQuote;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoriteQuoteRepository {

    private final FavoriteQuoteDao favoriteQuoteDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public FavoriteQuoteRepository(Application application) {
        QuoteDatabase database = QuoteDatabase.getInstance(application);
        favoriteQuoteDao = database.favoriteQuoteDao();
    }

    public void insert(FavoriteQuote quote) {
        executor.execute(() -> favoriteQuoteDao.insert(quote));
    }

    public void delete(FavoriteQuote quote) {
        executor.execute(() -> favoriteQuoteDao.delete(quote));
    }

    public void deleteAll() {
        executor.execute(favoriteQuoteDao::deleteAll);
    }

    public LiveData<List<FavoriteQuote>> getAllFavorites() {
        return favoriteQuoteDao.getAllFavorites();
    }

    public LiveData<List<FavoriteQuote>> getFavoritesByLanguage(String language) {
        return favoriteQuoteDao.getFavoritesByLanguage(language);
    }
}
