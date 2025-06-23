package com.surajvanshsv.quoteapps.ui.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;
import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.databinding.ActivityMainBinding;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.viewmodel.QuoteViewModel;
import com.surajvanshsv.quoteapps.utils.QuoteWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;
    private ActivityMainBinding binding;
    private QuoteViewModel viewModel;

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

        binding.btnFavoriteQuotes.setOnClickListener(v ->
                startActivity(new Intent(this, FavoriteQuotesActivity.class)));

        viewModel.fetchQuote();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        scheduleQuoteNotification("quoteMorning", 9, 0, constraints);

        scheduleQuoteNotification("quoteEvening", 16, 30, constraints);
        binding.btnCategories.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategorySelectionActivity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void scheduleQuoteNotification(String workName, int hour, int minute, Constraints constraints) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        if (now.after(target)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = target.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(QuoteWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                workName,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }
}
