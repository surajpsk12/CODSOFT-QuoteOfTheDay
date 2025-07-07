package com.surajvanshsv.quoteapps.ui.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.data.db.QuoteDatabase;
import com.surajvanshsv.quoteapps.databinding.ActivityMainBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.model.FavoriteQuote;
import com.surajvanshsv.quoteapps.ui.viewmodel.QuoteViewModel;
import com.surajvanshsv.quoteapps.ui.viewmodel.FavoriteQuoteViewModel;
import com.surajvanshsv.quoteapps.utils.QuoteImageHelper;
import com.surajvanshsv.quoteapps.utils.QuoteJsonImporter;
import com.surajvanshsv.quoteapps.utils.QuoteStorageHelper;
import com.surajvanshsv.quoteapps.utils.QuoteWorkScheduler;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private QuoteViewModel viewModel;
    private FavoriteQuoteViewModel favoriteQuoteViewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String currentLanguage = "english";
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(QuoteViewModel.class);
        favoriteQuoteViewModel = new ViewModelProvider(this).get(FavoriteQuoteViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // ⚠️ Run Hindi quote import in background thread
        executor.execute(() -> QuoteJsonImporter.importHindiQuotes(this, QuoteDatabase.getInstance(this).quoteDao()));

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), "Error: " + error, Snackbar.LENGTH_LONG).show();
            }
        });

        binding.btnEnglish.setOnClickListener(v -> {
            currentLanguage = "english";
            viewModel.fetchQuote();
            highlightLanguageButton();
        });

        binding.btnHindi.setOnClickListener(v -> {
            currentLanguage = "hindi";
            fetchRandomHindiQuote();
            highlightLanguageButton();
        });

        binding.btnFetch.setOnClickListener(v -> {
            if ("english".equals(currentLanguage)) {
                viewModel.fetchQuote();
            } else {
                fetchRandomHindiQuote();
            }
        });

        binding.btnShare.setOnClickListener(v -> {
            Quote quote = viewModel.getQuote().getValue();
            if (quote != null) {
                Bitmap shareBitmap = QuoteImageHelper.createQuoteShareImage(this, quote);
                QuoteImageHelper.shareImage(this, shareBitmap);
            } else {
                Snackbar.make(binding.getRoot(), "Quote not loaded yet", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnFavorite.setOnClickListener(v -> {
            Quote quote = viewModel.getQuote().getValue();
            if (quote != null) {
                FavoriteQuote favoriteQuote = FavoriteQuote.fromQuote(quote);
                favoriteQuoteViewModel.insert(favoriteQuote);
                Snackbar.make(binding.getRoot(), "Saved to favorites ❤️", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(binding.getRoot(), "No quote to save", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnFavoriteQuotes.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FavoriteQuotesActivity.class))
        );

        binding.btnCategories.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CategoryQuotesActivity.class))
        );

        binding.btnDownload.setOnClickListener(v -> {
            Quote quote = viewModel.getQuote().getValue();
            if (quote != null) {
                Snackbar.make(binding.getRoot(), "Saving image, please wait...", Snackbar.LENGTH_SHORT).show();
                executor.execute(() -> {
                    Bitmap bitmap = QuoteImageHelper.createQuoteShareImage(this, quote);
                    String fileName = "quote_" + System.currentTimeMillis() + ".png";
                    boolean saved = QuoteImageHelper.saveImageToGallery(this, bitmap, fileName);
                    runOnUiThread(() -> {
                        if (saved) {
                            Snackbar.make(binding.getRoot(), "Image saved to gallery ✅", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(binding.getRoot(), "Failed to save image", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                Snackbar.make(binding.getRoot(), "No quote loaded", Snackbar.LENGTH_SHORT).show();
            }
        });

        // ✅ Restore last quote if available
        if (viewModel.getQuote().getValue() == null) {
            Quote lastQuote = QuoteStorageHelper.getLastQuote(this);
            if (lastQuote != null) {
                currentLanguage = "hindi".equalsIgnoreCase(lastQuote.getLanguage()) ? "hindi" : "english";
                viewModel.setQuote(lastQuote);
                highlightLanguageButton();
            } else {
                viewModel.fetchQuote();
            }
        }

        QuoteWorkScheduler.scheduleDailyQuotes(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchRandomHindiQuote() {
        executor.execute(() -> {
            List<Quote> hindiQuotes = QuoteDatabase.getInstance(this).quoteDao().getQuotesByLanguageBlocking("hindi");
            if (hindiQuotes != null && !hindiQuotes.isEmpty()) {
                Quote randomQuote = hindiQuotes.get(random.nextInt(hindiQuotes.size()));
                runOnUiThread(() -> {
                    viewModel.setQuote(randomQuote);
                    QuoteStorageHelper.saveLastQuote(this, randomQuote);
                });
            } else {
                runOnUiThread(() ->
                        Snackbar.make(binding.getRoot(), "No Hindi quotes available", Snackbar.LENGTH_LONG).show()
                );
            }
        });
    }

    // ✅ Highlight selected language using alpha only
    private void highlightLanguageButton() {
        float selectedAlpha = 1.0f;
        float unselectedAlpha = 0.5f;

        binding.btnEnglish.setAlpha("english".equals(currentLanguage) ? selectedAlpha : unselectedAlpha);
        binding.btnHindi.setAlpha("hindi".equals(currentLanguage) ? selectedAlpha : unselectedAlpha);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
