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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuoteViewModel extends AndroidViewModel {

    private final MutableLiveData<Quote> quoteLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final QuoteRepository repository;
    private final LiveData<List<Quote>> allQuotes;

    // 🔁 All Hindi quotes stored in Room
    private final LiveData<List<Quote>> allHindiQuotes;

    // 🔄 Track language mode
    private String languageMode = "english"; // Default

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteRepository(application);
        allQuotes = repository.getAllQuotes();
        allHindiQuotes = repository.getHindiQuotes(); // Room DB
    }

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

    public LiveData<List<Quote>> getAllQuotes() {
        return allQuotes;
    }

    // ✅ Set language mode: "english" or "hindi"
    public void setLanguageMode(String lang) {
        languageMode = lang;
    }

    // 🚀 Core method to load a new quote (API or Room based on language)
    public void fetchQuote() {
        if ("hindi".equals(languageMode)) {
            fetchRandomHindiQuote();
        } else {
            fetchEnglishQuoteFromApi();
        }
    }

    // 🔁 Load Hindi quote from Room DB
    private void fetchRandomHindiQuote() {
        isLoading.setValue(true);

        allHindiQuotes.observeForever(hindiList -> {
            isLoading.setValue(false);
            if (hindiList != null && !hindiList.isEmpty()) {
                int randomIndex = new Random().nextInt(hindiList.size());
                quoteLiveData.setValue(hindiList.get(randomIndex));
            } else {
                error.setValue("No Hindi quotes found in database.");
            }
        });
    }

    // 🌐 API call for English quote
    private void fetchEnglishQuoteFromApi() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getQuoteOfTheDay().enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Quote quote = response.body().getQuote();
                    quoteLiveData.setValue(quote);
                    QuoteStorageHelper.saveLastQuote(getApplication(), quote);
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
}
