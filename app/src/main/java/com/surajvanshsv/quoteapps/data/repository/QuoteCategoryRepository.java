package com.surajvanshsv.quoteapps.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.surajvanshsv.quoteapps.data.api.QuoteApiService;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.model.QuoteListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteCategoryRepository {

    private static final String BASE_URL = "https://favqs.com/api/";
    private final QuoteApiService apiService;
    private final String API_KEY = "Token token=36dae93c27213f74a22287d45ce032aa";

    public QuoteCategoryRepository() {
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApiService.class);
    }

    public LiveData<List<Quote>> getQuotesByTag(String tag) {
        MutableLiveData<List<Quote>> liveData = new MutableLiveData<>();

        apiService.getQuotesByTag( tag, "tag", 1).enqueue(new Callback<QuoteListResponse>() {
            @Override
            public void onResponse(Call<QuoteListResponse> call, Response<QuoteListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getQuotes());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<QuoteListResponse> call, Throwable t) {
                liveData.setValue(null);
            }
        });

        return liveData;
    }
}
