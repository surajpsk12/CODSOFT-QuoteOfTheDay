package com.surajvanshsv.quoteapps.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.data.db.QuoteDatabase;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.model.QuoteResponse;
import com.surajvanshsv.quoteapps.ui.view.MainActivity;
import com.surajvanshsv.quoteapps.ui.widget.QuoteWidgetProvider;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import android.appwidget.AppWidgetManager;

public class QuoteWorker extends Worker {

    private static final String CHANNEL_ID = "daily_quote_channel";
    private static final int NOTIFICATION_ID = 101;

    public QuoteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // 1️⃣ Fetch Quote from API
            QuoteApi api = new Retrofit.Builder()
                    .baseUrl("https://favqs.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(QuoteApi.class);

            Response<QuoteResponse> response = api.getQuote().execute();
            if (response.isSuccessful() && response.body() != null) {
                Quote quote = response.body().getQuote();

                // 2️⃣ Save to Room DB
                QuoteDatabase db = QuoteDatabase.getInstance(getApplicationContext());
                db.quoteDao().insert(quote);

                // 3️⃣ Save to SharedPreferences (for widget)
                QuoteStorage.saveQuote(getApplicationContext(), quote);

                // 4️⃣ Trigger widget update
                triggerWidgetUpdate();

                // 5️⃣ Show notification
                showNotification(quote);

                return Result.success();
            } else {
                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void triggerWidgetUpdate() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, QuoteWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, QuoteWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }

    private void showNotification(Quote quote) {
        createNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String content = "\"" + quote.getBody() + "\"\n— " + quote.getAuthor();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Quote of the Day")
                .setContentText(quote.getBody())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Quotes";
            String description = "Shows daily inspirational quotes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    interface QuoteApi {
        @Headers("Authorization: Token token=YOUR_FAVQS_API_KEY") // Replace this
        @GET("qotd")
        Call<QuoteResponse> getQuote();
    }
}
