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
import com.surajvanshsv.quoteapps.databinding.ActivityMainBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.viewmodel.QuoteViewModel;
import com.surajvanshsv.quoteapps.utils.QuoteImageHelper;
import com.surajvanshsv.quoteapps.utils.QuoteStorageHelper;
import com.surajvanshsv.quoteapps.utils.QuoteWorkScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private QuoteViewModel viewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(QuoteViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), "Error: " + error, Snackbar.LENGTH_LONG).show();
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

        binding.btnFetch.setOnClickListener(v -> viewModel.fetchQuote());

        binding.btnFavorite.setOnClickListener(v -> {
            Quote quote = viewModel.getQuote().getValue();
            if (quote != null) {
                viewModel.insertQuote(new Quote(quote.getBody(), quote.getAuthor()));
                Snackbar.make(binding.getRoot(), "Saved to favorites ❤️", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(binding.getRoot(), "No quote to save", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnFavoriteQuotes.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FavoriteQuotesActivity.class));
        });

        binding.btnCategories.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CategoryQuotesActivity.class));
        });

        // 🟢 Download button functionality (instead of reel)
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

        // Load last quote from SharedPreferences if available
        if (viewModel.getQuote().getValue() == null) {
            Quote lastQuote = QuoteStorageHelper.getLastQuote(this);
            if (lastQuote != null) {
                viewModel.setQuote(lastQuote);
            }
        }

        // Schedule quote workers
        QuoteWorkScheduler.scheduleDailyQuotes(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
