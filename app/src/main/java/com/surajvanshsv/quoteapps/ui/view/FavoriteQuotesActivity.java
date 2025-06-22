package com.surajvanshsv.quoteapps.ui.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.surajvanshsv.quoteapps.databinding.ActivityFavoriteQuotesBinding;
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

        adapter = new QuoteAdapter();
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        binding.recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFavorites.setAdapter(adapter);

        viewModel.getAllQuotes().observe(this, adapter::setQuotes);
    }
}
