package com.surajvanshsv.quoteapps.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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

        // 🔴 Show API fetch errors if any
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Share Quote
        binding.btnShare.setOnClickListener(v -> {
            Quote quote = viewModel.getQuote().getValue();
            if (quote != null) {
                String text = "\"" + quote.getBody() + "\" — " + quote.getAuthor();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(shareIntent, "Share quote using"));
            } else {
                Toast.makeText(this, "Quote not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Save to Favorites (Room)
        binding.btnFavorite.setOnClickListener(v -> {
            Quote quote = viewModel.getQuote().getValue();
            if (quote != null) {
                viewModel.insertQuote(new Quote(quote.getBody(), quote.getAuthor()));
                Toast.makeText(this, "Saved to favorites ❤️", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No quote to save", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Open Favorites Screen
        binding.btnFavoriteQuotes.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoriteQuotesActivity.class));
        });

        // ✅ Fetch new quote on start
        viewModel.fetchQuote();

        // ✅ System UI Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
