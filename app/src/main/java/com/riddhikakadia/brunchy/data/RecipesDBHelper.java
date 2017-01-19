package com.riddhikakadia.brunchy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.riddhikakadia.brunchy.data.RecipesContract.RecipeSearch;
import static com.riddhikakadia.brunchy.data.RecipesContract.FavoriteRecipes;

/**
 * Created by RKs on 1/4/2017.
 */

public class RecipesDBHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = RecipesDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 2;

    public RecipesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE_SEARCHES = "CREATE TABLE " + RecipeSearch.TABLE_NAME
                + " (" + RecipeSearch._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecipeSearch.COLUMN_SEARCH_KEYWORD + " TEXT "
                + " )";
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCHES);

        final String CREATE_TABLE_FAVORITE_RECIPES = "CREATE TABLE " + FavoriteRecipes.TABLE_NAME
                + " (" + FavoriteRecipes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteRecipes.COLUMN_USER_EMAIL + " TEXT, "
                + FavoriteRecipes.COLUMN_RECIPE_ID + " TEXT, "
                + FavoriteRecipes.COLUMN_RECIPE_NAME + " TEXT, "
                + FavoriteRecipes.COLUMN_RECIPE_PHOTO_LINK + " TEXT, "
                + FavoriteRecipes.COLUMN_TOTAL_INGREDIENTS + " INTEGER, "
                + FavoriteRecipes.COLUMN_CALORIES_PER_SERVING + " INTEGER, "
                + FavoriteRecipes.COLUMN_TOTAL_SERVING + " INTEGER, "
                + FavoriteRecipes.COLUMN_INGREDIENTS + " TEXT, "
                + FavoriteRecipes.COLUMN_HEALTH_LABELS + " TEXT, "
                + "UNIQUE (" + FavoriteRecipes.COLUMN_RECIPE_ID + ") ON CONFLICT REPLACE "
                + " )";
        sqLiteDatabase.execSQL(CREATE_TABLE_FAVORITE_RECIPES);

        //Log.d(LOG_TAG, "*** favorite table \n " + CREATE_TABLE_FAVORITE_RECIPES);
        //Log.d(LOG_TAG, "*** search table \n " + CREATE_TABLE_SEARCHES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(LOG_TAG, "Database upgrade from version " + i + " to version " + i1);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeSearch.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteRecipes.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
