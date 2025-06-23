package com.surajvanshsv.quoteapps.ui.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.surajvanshsv.quoteapps.databinding.ActivityCategorySelectionBinding;
import com.surajvanshsv.quoteapps.ui.adapter.CategoryAdapter;

import java.util.Arrays;
import java.util.List;

public class CategorySelectionActivity extends AppCompatActivity {

    private ActivityCategorySelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Correctly inflate binding
        binding = ActivityCategorySelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Sample categories to show
        List<String> categories = Arrays.asList(
                "inspirational", "life", "love", "happiness",
                "wisdom", "success", "friendship", "motivation"
        );

        // ✅ Set up adapter with click listener to open category quotes
        CategoryAdapter adapter = new CategoryAdapter(categories, tag -> {
            Intent intent = new Intent(this, CategoryQuotesActivity.class);
            intent.putExtra("TAG", tag);
            startActivity(intent);
        });

        // ✅ Grid layout with 2 columns
        binding.recyclerCategoryList.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerCategoryList.setAdapter(adapter);

        // ✅ Back button in top app bar
        MaterialToolbar toolbar = binding.topAppBar;
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
