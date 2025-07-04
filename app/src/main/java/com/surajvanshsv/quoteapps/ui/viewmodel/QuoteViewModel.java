package com.surajvanshsv.quoteapps.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.surajvanshsv.quoteapps.data.api.RetrofitClient;
import com.surajvanshsv.quoteapps.data.repository.QuoteRepository;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.model.QuoteResponse;
import com.surajvanshsv.quoteapps.utils.QuoteStorageHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuoteViewModel extends AndroidViewModel {

    private final MutableLiveData<Quote> quoteLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final QuoteRepository repository;
    private final LiveData<List<Quote>> allQuotes;

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteRepository(application);
        allQuotes = repository.getAllQuotes();
    }

    // ✅ API Quote
    public LiveData<Quote> getQuote() {
        return quoteLiveData;
    }
    public void setQuote(Quote quote) {
        quoteLiveData.setValue(quote);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchQuote() {
        isLoading.setValue(true);

        RetrofitClient.getInstance().getQuoteOfTheDay().enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    quoteLiveData.setValue(response.body().getQuote());
                    QuoteStorageHelper.saveLastQuote(getApplication(), response.body().getQuote());

                } else {
                    error.setValue("Failed to load quote.");
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    // ✅ Favorites
    public void insertQuote(Quote quote) {
        repository.insert(quote);
    }

    public void deleteQuote(Quote quote) {
        repository.delete(quote);
    }

    public void deleteAllQuotes() {
        repository.deleteAll();
    }

    public LiveData<List<Quote>> getAllQuotes() {
        return allQuotes;
    }
}
