package com.surajvanshsv.quoteapps.data.api;

import com.surajvanshsv.quoteapps.model.QuoteResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {

    @GET("qotd")
    Call<QuoteResponse> getQuoteOfTheDay();
}
