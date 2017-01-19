package com.riddhikakadia.brunchy.widget;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.data.RecipesContract;
import com.riddhikakadia.brunchy.util.Global;

import java.util.ArrayList;

import static com.riddhikakadia.brunchy.util.Constants.RECIPE_ID;

/**
 * Created by RKs on 1/15/2017.
 */

public class WidgetService extends RemoteViewsService {

    public static final String[] FAVOURITE_RECIPE_COLUMNS = {
            RecipesContract.FavoriteRecipes._ID,
            RecipesContract.FavoriteRecipes.COLUMN_USER_EMAIL,
            RecipesContract.FavoriteRecipes.COLUMN_RECIPE_ID,
            RecipesContract.FavoriteRecipes.COLUMN_RECIPE_NAME,
            RecipesContract.FavoriteRecipes.COLUMN_RECIPE_PHOTO_LINK,
            RecipesContract.FavoriteRecipes.COLUMN_TOTAL_INGREDIENTS,
            RecipesContract.FavoriteRecipes.COLUMN_CALORIES_PER_SERVING,
            RecipesContract.FavoriteRecipes.COLUMN_TOTAL_SERVING,
            RecipesContract.FavoriteRecipes.COLUMN_INGREDIENTS,
            RecipesContract.FavoriteRecipes.COLUMN_HEALTH_LABELS
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new ListProvider(this.getApplicationContext(), intent));
    }

    public class ListProvider implements RemoteViewsFactory {

        final String LOG_TAG = ListProvider.class.getSimpleName();

        private Context context = null;
        Cursor cursor;
        private boolean isLoggedIn;

        public ListProvider(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            //Log.d(LOG_TAG, "*** widget ListProvider onCreate()");
            if (Global.currentUserEmail != null && Global.currentUserEmail != "") {
                isLoggedIn = true;

                cursor = getContentResolver().query(
                        RecipesContract.FavoriteRecipes.buildFavoriteRecipeUri(),
                        FAVOURITE_RECIPE_COLUMNS,
                        RecipesContract.FavoriteRecipes.COLUMN_USER_EMAIL + "=?",
                        new String[]{Global.currentUserEmail},
                        null);
            } else {
                //App is not logged in
            }
        }

        @Override
        public void onDataSetChanged() {
            //Log.d(LOG_TAG, "*** widget ListProvider onDataSetChanged() " + Global.currentUserEmail);

            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.favorite_recipes_widget);
            if (Global.currentUserEmail != null && Global.currentUserEmail != "") {
                Log.d(LOG_TAG, "*** widget ListProvider onDataSetChanged() user not null " + Global.currentUserEmail);
                isLoggedIn = true;

                cursor = getContentResolver().query(
                        RecipesContract.FavoriteRecipes.buildFavoriteRecipeUri(),
                        FAVOURITE_RECIPE_COLUMNS,
                        RecipesContract.FavoriteRecipes.COLUMN_USER_EMAIL + "=?",
                        new String[]{Global.currentUserEmail},
                        null);

                if (cursor != null) {
                    if (cursor.getCount() == 0) {
                        remoteViews.setEmptyView(R.id.favorite_recipes_list_widget, R.id.no_data_textview_widget);
                        remoteViews.setTextViewText(R.id.no_data_textview_widget, getResources().getString(R.string.no_favorite_recipes_message));
                    }
                }
            } else {
                isLoggedIn = false;
                //Log.d(LOG_TAG, "*** widget ListProvider onDataSetChanged() user null or blank " + Global.currentUserEmail);
                remoteViews.setEmptyView(R.id.favorite_recipes_list_widget, R.id.no_data_textview_widget);
                remoteViews.setTextViewText(R.id.no_data_textview_widget, getResources().getString(R.string.app_not_logged_in_message));
            }
        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public int getCount() {
            return (cursor != null) ? cursor.getCount() : 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


        @Override
        public RemoteViews getViewAt(int position) {
            //Log.d(LOG_TAG, "*** widget RemoteViews getViewAt " + position);

            final RemoteViews remoteView = new RemoteViews(
                    context.getPackageName(), R.layout.favorite_recipes_widget_list_item);

            if (isLoggedIn) {
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        if (cursor.moveToPosition(position)) {
                            //Log.d(LOG_TAG, "*** widget RemoteViews cursor.moveToPosition(position) " + position);

                            String recipeName = cursor.getString(3);
                            //Log.d(LOG_TAG, "*** widget recipeName " + recipeName);
                            remoteView.setTextViewText(R.id.favorite_recipe_name_text_widget, recipeName);
                            String recipeID = cursor.getString(2);

                            Bundle extras = new Bundle();
                            extras.putString(RECIPE_ID, recipeID);

                            Intent fillInIntent = new Intent();
                            fillInIntent.putExtras(extras);
                            remoteView.setOnClickFillInIntent(R.id.favorite_recipe_name_text_widget, fillInIntent);
                        }
                    }
                }
            }

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    }
}
