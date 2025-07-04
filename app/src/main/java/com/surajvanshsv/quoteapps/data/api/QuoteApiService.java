package com.surajvanshsv.quoteapps.data.api;

import com.surajvanshsv.quoteapps.model.QuoteResponse;
import com.surajvanshsv.quoteapps.model.QuoteListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface QuoteApiService {

    // 🔹 Get Quote of the Day
    @Headers("Authorization: Token token=36dae93c27213f74a22287d45ce032aa")
    @GET("qotd")
    Call<QuoteResponse> getQuoteOfTheDay();

    // 🔸 Get Quotes by Category/Tag
    @Headers("Authorization: Token token=36dae93c27213f74a22287d45ce032aa")
    @GET("quotes/")
    Call<QuoteListResponse> getQuotesByTag(
            @Query("filter") String tag,
            @Query("type") String type,
            @Query("page") int page
    );

}
