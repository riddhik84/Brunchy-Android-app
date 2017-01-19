package com.riddhikakadia.brunchy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.data.RecipesContract;
import com.riddhikakadia.brunchy.ui.FavoriteRecipesActivity;
import com.riddhikakadia.brunchy.ui.RecipeDetailActivity;
import com.riddhikakadia.brunchy.ui.RecipesListActivity;

import static com.riddhikakadia.brunchy.util.Constants.ACTION_DATA_UPDATED;

/**
 * Created by RKs on 1/11/2017.
 */

public class FavoriteRecipesWidget extends AppWidgetProvider {

    private final String LOG_TAG = FavoriteRecipesWidget.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_DATA_UPDATED)) {
            //Log.d(LOG_TAG, "*** widget onReceive ACTION_DATA_UPDATED");

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIDs = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIDs, R.id.favorite_recipes_list_widget);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Log.d(LOG_TAG, "*** widget onUpdate()");
        //context.startService(new Intent(context, WidgetService.class));

        for (int appWidgetId : appWidgetIds) {
            //Log.d(LOG_TAG, "*** widget appWidgetId " + appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_recipes_widget);

            // Create an Intent to launch detailActivity
            //implement for detail screen
            Intent intent = new Intent(context, FavoriteRecipesActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.favorite_recipes_header_widget, pendingIntent);

            // Set up the collection
            views.setRemoteAdapter(R.id.favorite_recipes_list_widget,
                    new Intent(context, WidgetService.class));

            boolean useDetailActivity = context.getResources()
                    .getBoolean(R.bool.use_detail_activity);
            Intent clickIntentTemplate = useDetailActivity
                    //change to detailactivity
                    ? new Intent(context, RecipeDetailActivity.class)
                    : new Intent(context, FavoriteRecipesActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.favorite_recipes_list_widget, clickPendingIntentTemplate);
            views.setEmptyView(R.id.favorite_recipes_list_widget, R.id.no_data_textview_widget);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
