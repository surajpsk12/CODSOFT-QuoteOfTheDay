package com.surajvanshsv.quoteapps.ui.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.surajvanshsv.quoteapps.databinding.ActivityFavoriteQuotesBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.adapter.QuoteAdapter;
import com.surajvanshsv.quoteapps.ui.viewmodel.QuoteViewModel;
import com.surajvanshsv.quoteapps.utils.QuoteImageHelper;

public class FavoriteQuotesActivity extends AppCompatActivity {

    private ActivityFavoriteQuotesBinding binding;
    private QuoteViewModel viewModel;
    private QuoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteQuotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.topAppBar;
        toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(QuoteViewModel.class);

        adapter = new QuoteAdapter(new QuoteAdapter.OnQuoteClickListener() {
            @Override
            public void onShareClick(Quote quote) {
                Bitmap bitmap = QuoteImageHelper.createQuoteShareImage(FavoriteQuotesActivity.this, quote);
                QuoteImageHelper.shareImage(FavoriteQuotesActivity.this, bitmap);
            }

            @Override
            public void onDeleteClick(Quote quote) {
                deleteWithUndo(quote);
            }
        });

        binding.recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerFavorites.setAdapter(adapter);

        viewModel.getAllQuotes().observe(this, adapter::setQuotes);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView,
                                  @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder,
                                  @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Quote deletedQuote = adapter.getQuoteAt(position);
                deleteWithUndo(deletedQuote);
            }
        }).attachToRecyclerView(binding.recyclerFavorites);
    }

    private void deleteWithUndo(Quote quote) {
        viewModel.deleteQuote(quote);

        Snackbar.make(binding.getRoot(), "Quote deleted 🗑️", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> viewModel.insertQuote(quote))
                .show();
    }
}
