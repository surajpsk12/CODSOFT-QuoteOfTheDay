package com.surajvanshsv.quoteapps.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.surajvanshsv.quoteapps.data.repository.FavoriteQuoteRepository;
import com.surajvanshsv.quoteapps.model.FavoriteQuote;

import java.util.List;

public class FavoriteQuoteViewModel extends AndroidViewModel {

    private final FavoriteQuoteRepository repository;

    public FavoriteQuoteViewModel(@NonNull Application application) {
        super(application);
        repository = new FavoriteQuoteRepository(application);
    }

    public void insert(FavoriteQuote quote) {
        repository.insert(quote);
    }

    public void delete(FavoriteQuote quote) {
        repository.delete(quote);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<FavoriteQuote>> getAllFavorites() {
        return repository.getAllFavorites();
    }

    public LiveData<List<FavoriteQuote>> getFavoritesByLanguage(String language) {
        return repository.getFavoritesByLanguage(language);
    }
}
