package com.surajvanshsv.quoteapps.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.model.Quote;
import com.surajvanshsv.quoteapps.ui.view.MainActivity;
import com.surajvanshsv.quoteapps.utils.QuoteStorage;

public class QuoteWidgetProvider extends AppWidgetProvider {

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        Quote quote = QuoteStorage.getSavedQuote(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quote_widget);
        views.setTextViewText(R.id.widget_quote_text, "\"" + quote.getBody() + "\"");
        views.setTextViewText(R.id.widget_quote_author, "— " + quote.getAuthor());

        // Launch MainActivity when widget is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    // Optional: Allow external refresh trigger (from Worker)
    public static void refreshAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, QuoteWidgetProvider.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);
        for (int widgetId : widgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }
}
