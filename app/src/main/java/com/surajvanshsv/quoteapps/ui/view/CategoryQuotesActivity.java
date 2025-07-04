package com.surajvanshsv.quoteapps.ui.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.surajvanshsv.quoteapps.databinding.ActivityCategoryQuotesBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.adapter.QuoteAdapter;
import com.surajvanshsv.quoteapps.ui.viewmodel.CategoryQuotesViewModel;

public class CategoryQuotesActivity extends AppCompatActivity {

    private ActivityCategoryQuotesBinding binding;
    private CategoryQuotesViewModel viewModel;
    private QuoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryQuotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String tag = getIntent().getStringExtra("TAG");

        MaterialToolbar toolbar = binding.topAppBar;
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(tag + " Quotes");

        adapter = new QuoteAdapter(null); // ✅ No listener needed for category quotes
        binding.recyclerCategoryQuotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCategoryQuotes.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CategoryQuotesViewModel.class);

        // ✅ Observe the ViewModel’s LiveData
        viewModel.getQuotes().observe(this, quotes -> {
            if (quotes != null && !quotes.isEmpty()) {
                adapter.setQuotes(quotes);
            } else {
                // Optionally show empty state
            }
        });

        // ✅ Trigger the API call ONCE
        viewModel.loadQuotes(tag);
    }
}
