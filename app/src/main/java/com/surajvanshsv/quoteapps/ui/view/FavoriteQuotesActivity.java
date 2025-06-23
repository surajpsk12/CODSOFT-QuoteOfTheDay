package com.surajvanshsv.quoteapps.ui.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.surajvanshsv.quoteapps.databinding.ActivityFavoriteQuotesBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.adapter.QuoteAdapter;
import com.surajvanshsv.quoteapps.ui.viewmodel.FavoriteViewModel;

public class FavoriteQuotesActivity extends AppCompatActivity {

    private ActivityFavoriteQuotesBinding binding;
    private FavoriteViewModel viewModel;
    private QuoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteQuotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.topAppBar;
        toolbar.setNavigationOnClickListener(v -> finish());

        // ViewModel & Adapter Setup
        adapter = new QuoteAdapter();
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        binding.recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFavorites.setAdapter(adapter);

        viewModel.getAllQuotes().observe(this, adapter::setQuotes);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Quote deletedQuote = adapter.getQuoteAt(position);

                viewModel.deleteQuote(deletedQuote);

                Snackbar.make(binding.getRoot(), "Quote deleted 🗑️", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            viewModel.insertQuote(deletedQuote);
                            Snackbar.make(binding.getRoot(), "Quote restored ✅", Snackbar.LENGTH_SHORT).show();
                        }).show();
            }
        }).attachToRecyclerView(binding.recyclerFavorites);
    }
}
