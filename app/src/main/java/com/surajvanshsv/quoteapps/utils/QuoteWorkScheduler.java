package com.surajvanshsv.quoteapps.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class QuoteWorkScheduler {

    public static void scheduleDailyQuote(Context context) {
        Calendar now = Calendar.getInstance();
        Calendar next12pm = Calendar.getInstance();
        next12pm.set(Calendar.HOUR_OF_DAY, 12);
        next12pm.set(Calendar.MINUTE, 0);
        next12pm.set(Calendar.SECOND, 0);

        if (now.after(next12pm)) {
            next12pm.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = next12pm.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                QuoteWorker.class,
                24, TimeUnit.HOURS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(new Constraints.Builder().setRequiresBatteryNotLow(true).build())
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        "daily_quote_worker",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                );

    }
}
