package com.surajvanshsv.quoteapps.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HindiQuoteResponse {

    @SerializedName("quotes")
    private List<Quote> quotes;

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}
