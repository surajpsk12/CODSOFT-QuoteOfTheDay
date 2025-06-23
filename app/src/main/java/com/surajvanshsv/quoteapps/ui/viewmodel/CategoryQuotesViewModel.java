package com.surajvanshsv.quoteapps.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.surajvanshsv.quoteapps.data.repository.QuoteCategoryRepository;
import com.surajvanshsv.quoteapps.model.Quote;

import java.util.List;

public class CategoryQuotesViewModel extends AndroidViewModel {

    private final QuoteCategoryRepository repository;

    public CategoryQuotesViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteCategoryRepository();
    }

    public LiveData<List<Quote>> getQuotes(String tag) {
        return repository.getQuotesByTag(tag);
    }
}
