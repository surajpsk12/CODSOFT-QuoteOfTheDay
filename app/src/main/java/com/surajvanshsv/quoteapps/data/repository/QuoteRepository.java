package com.surajvanshsv.quoteapps.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.surajvanshsv.quoteapps.data.db.QuoteDao;
import com.surajvanshsv.quoteapps.data.db.QuoteDatabase;
import com.surajvanshsv.quoteapps.model.Quote;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QuoteRepository {

    private final QuoteDao quoteDao;
    private final LiveData<List<Quote>> allQuotes;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public QuoteRepository(Application application) {
        QuoteDatabase database = QuoteDatabase.getInstance(application);
        quoteDao = database.quoteDao();
        allQuotes = quoteDao.getAllQuotes();
    }

    public void insert(Quote quote) {
        executor.execute(() -> quoteDao.insert(quote));
    }

    public void delete(Quote quote) {
        executor.execute(() -> quoteDao.delete(quote));
    }

    public void deleteAll() {
        executor.execute(quoteDao::deleteAll);
    }

    public LiveData<List<Quote>> getAllQuotes() {
        return allQuotes;
    }

    // ✅ Live Hindi quotes (e.g., for Favorite screen)
    public LiveData<List<Quote>> getHindiQuotes() {
        return quoteDao.getQuotesByLanguage("hindi");
    }

    // ✅ Blocking method for random Hindi quote (used in MainActivity)
    public List<Quote> getQuotesByLanguageBlocking(String lang) {
        return quoteDao.getQuotesByLanguageBlocking(lang);
    }

    // Optional: expose DAO for custom use
    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
}
