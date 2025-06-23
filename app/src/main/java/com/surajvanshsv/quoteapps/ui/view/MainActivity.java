package com.surajvanshsv.quoteapps.ui.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.databinding.ActivityMainBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.viewmodel.QuoteViewModel;

public class MainActivity extends AppCompatActivity {

    private QuoteViewModel viewModel;
    private ActivityMainBinding binding;

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
                String text = "\"" + quote.getBody() + "\" — " + quote.getAuthor();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(shareIntent, "Share quote using"));
            } else {
                Snackbar.make(binding.getRoot(), "Quote not loaded yet", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnFetch.setOnClickListener(v -> {
            viewModel.fetchQuote();
        });


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
            startActivity(new Intent(this, FavoriteQuotesActivity.class));
        });

        viewModel.fetchQuote();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
