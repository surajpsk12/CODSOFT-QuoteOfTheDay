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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuoteViewModel extends AndroidViewModel {

    private final MutableLiveData<Quote> quoteLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final QuoteRepository repository;

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteRepository(application);
    }

    public LiveData<Quote> getQuote() {
        return quoteLiveData;
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

    // ✅ Insert into Room Database
    public void insertQuote(Quote quote) {
        repository.insert(quote);
    }
}
