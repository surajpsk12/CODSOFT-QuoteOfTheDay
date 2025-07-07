package com.surajvanshsv.quoteapps.ui.view;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.databinding.ActivityFavoriteQuotesBinding;
import com.surajvanshsv.quoteapps.model.FavoriteQuote;
import com.surajvanshsv.quoteapps.ui.adapter.FavoriteQuoteAdapter;
import com.surajvanshsv.quoteapps.ui.viewmodel.FavoriteQuoteViewModel;
import com.surajvanshsv.quoteapps.utils.QuoteImageHelper;

public class FavoriteQuotesActivity extends AppCompatActivity {

    private ActivityFavoriteQuotesBinding binding;
    private FavoriteQuoteViewModel viewModel;
    private FavoriteQuoteAdapter adapter;
    private String currentLanguage = "english";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteQuotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViewModel();
        setupRecyclerView();
        setupLanguageButtons();
        observeQuotes();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.topAppBar;
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FavoriteQuoteViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new FavoriteQuoteAdapter(new FavoriteQuoteAdapter.OnFavoriteQuoteClickListener() {
            @Override
            public void onShareClick(FavoriteQuote quote) {
                Bitmap bitmap = QuoteImageHelper.createQuoteShareImage(FavoriteQuotesActivity.this, quote.toQuote());
                QuoteImageHelper.shareImage(FavoriteQuotesActivity.this, bitmap);
            }

            @Override
            public void onDeleteClick(FavoriteQuote quote) {
                deleteWithUndo(quote);
            }
        });

        binding.recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerFavorites.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                FavoriteQuote deletedQuote = adapter.getQuoteAt(viewHolder.getAdapterPosition());
                deleteWithUndo(deletedQuote);
            }
        }).attachToRecyclerView(binding.recyclerFavorites);
    }

    private void setupLanguageButtons() {
        binding.btnEnglish.setOnClickListener(v -> {
            currentLanguage = "english";
            observeQuotes();
            updateButtonStyles();
        });

        binding.btnHindi.setOnClickListener(v -> {
            currentLanguage = "hindi";
            observeQuotes();
            updateButtonStyles();
        });

        updateButtonStyles(); // initial highlight
    }

    private void observeQuotes() {
        viewModel.getFavoritesByLanguage(currentLanguage).observe(this, adapter::setQuotes);
    }

    private void updateButtonStyles() {
        float selectedAlpha = 1.0f;
        float unselectedAlpha = 0.5f;

        binding.btnEnglish.setAlpha("english".equals(currentLanguage) ? selectedAlpha : unselectedAlpha);
        binding.btnHindi.setAlpha("hindi".equals(currentLanguage) ? selectedAlpha : unselectedAlpha);
    }

    private void deleteWithUndo(FavoriteQuote quote) {
        viewModel.delete(quote);
        Snackbar.make(binding.getRoot(), "Quote deleted 🗑️", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> viewModel.insert(quote))
                .show();
    }
}
