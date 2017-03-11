package com.riddhikakadia.brunchy2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RKs on 1/4/2017.
 */

public class RecipesContract {
    final String LOG_TAG = RecipesContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.riddhikakadia.brunchy";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECIPE_SEARCH = "recipe_search";
    public static final String PATH_FAVORITE_RECIPE = "favorite_recipes";

    public static class RecipeSearch implements BaseColumns {
        public static final String TABLE_NAME = "recipe_search";

        public static final String _ID = "_id";
        public static final String COLUMN_SEARCH_KEYWORD = "search_keyword";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_SEARCH).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_SEARCH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_SEARCH;

        public static Uri buildSearchUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildSearchUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class FavoriteRecipes implements BaseColumns {
        public static final String TABLE_NAME = "favorite_recipes";

        public static final String _ID = "_id";
        public static final String COLUMN_USER_EMAIL = "user_email";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_RECIPE_NAME = "recipe_name";
        public static final String COLUMN_RECIPE_PHOTO_LINK = "recipe_photo";
        public static final String COLUMN_TOTAL_INGREDIENTS = "total_ingredients";
        public static final String COLUMN_CALORIES_PER_SERVING = "calories_per_serving";
        public static final String COLUMN_TOTAL_SERVING = "total_serving";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_HEALTH_LABELS = "health_labels";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_RECIPE).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_RECIPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_RECIPE;

        public static Uri buildFavoriteRecipeUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildFavoriteRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
