package com.surajvanshsv.quoteapps.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class QuoteWorkScheduler {

    public static void scheduleDailyQuotes(Context context) {
        // Schedule for 12:00 pm
        scheduleQuoteAt(context, 12, 0, "daily_quote_worker_12pm");

        // Schedule for 6:05 pm
        scheduleQuoteAt(context, 18, 5, "daily_quote_worker_6_05pm");

        // Schedule for 7:00 pm notification
        scheduleNotificationAt(context, 19, 0, "daily_notification_worker");
    }

    private static void scheduleQuoteAt(Context context, int hour, int minute, String uniqueWorkName) {
        Calendar now = Calendar.getInstance();
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, hour);
        targetTime.set(Calendar.MINUTE, minute);
        targetTime.set(Calendar.SECOND, 0);

        if (now.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = targetTime.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                QuoteWorker.class,
                24, TimeUnit.HOURS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(new Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build())
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        uniqueWorkName,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                );
    }

    // ✅ Added for 7 pm notifications
    private static void scheduleNotificationAt(Context context, int hour, int minute, String uniqueWorkName) {
        Calendar now = Calendar.getInstance();
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, hour);
        targetTime.set(Calendar.MINUTE, minute);
        targetTime.set(Calendar.SECOND, 0);

        if (now.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = targetTime.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class, // you will create this NotificationWorker as shown before
                24, TimeUnit.HOURS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        uniqueWorkName,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                );
    }
}
