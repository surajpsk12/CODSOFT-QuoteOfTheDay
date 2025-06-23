package com.surajvanshsv.quoteapps.model;

import java.util.List;

public class QuoteListResponse {
    private int page;
    private int last_page;
    private List<Quote> quotes;

    public int getPage() {
        return page;
    }

    public int getLastPage() {
        return last_page;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLastPage(int last_page) {
        this.last_page = last_page;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}
