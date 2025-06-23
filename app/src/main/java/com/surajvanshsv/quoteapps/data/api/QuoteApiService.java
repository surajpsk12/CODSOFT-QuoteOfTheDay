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
            @Query("filter") String tag,     // Example: "life", "inspirational"
            @Query("type") String type,      // Usually: "tag"
            String s, @Query("page") int page          // For pagination, start with 1
    );
}
