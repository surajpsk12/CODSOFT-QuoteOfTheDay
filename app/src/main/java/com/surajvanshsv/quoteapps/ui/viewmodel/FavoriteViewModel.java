package com.surajvanshsv.quoteapps.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.surajvanshsv.quoteapps.data.repository.QuoteRepository;
import com.surajvanshsv.quoteapps.model.Quote;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private final QuoteRepository repository;
    private final LiveData<List<Quote>> allQuotes;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteRepository(application);
        allQuotes = repository.getAllQuotes();
    }

    public LiveData<List<Quote>> getAllQuotes() {
        return allQuotes;
    }
}
