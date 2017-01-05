package com.riddhikakadia.brunchy.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.riddhikakadia.brunchy.model.P;

/**
 * Created by RKs on 1/4/2017.
 */

public class RecipesContract {
    final String LOG_TAG = RecipesContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECIPE_SEARCH = "recipe_search";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_SEARCH).build();
    public static final String CONTENT_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_SEARCH;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_SEARCH;

    public static class RecipeSearch implements BaseColumns {

        public static final String TABLE_NAME = "searchlist";

        public static final String _ID = "_id";
        public static final String COLUMN_SEARCH_KEYWORD = "search_keyword";
    }

}
