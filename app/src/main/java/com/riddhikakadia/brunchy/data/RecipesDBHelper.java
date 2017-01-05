package com.riddhikakadia.brunchy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by RKs on 1/4/2017.
 */

public class RecipesDBHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = RecipesDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;

    public RecipesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE_SEARCHES = "CREATE TABLE " + RecipesContract.RecipeSearch.TABLE_NAME
                + " (" + RecipesContract.RecipeSearch._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecipesContract.RecipeSearch.COLUMN_SEARCH_KEYWORD + " TEXT "
                + " )";
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(LOG_TAG, "Database upgrade from version " + i + " to version " + i1);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeSearch.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
