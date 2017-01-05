package com.riddhikakadia.brunchy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by RKs on 1/4/2017.
 */

public class RecipesProvider extends ContentProvider {

    private final String LOG_TAG = RecipesProvider.class.getSimpleName();

    private static final UriMatcher mUriMatcher = getUriMatcher();
    private RecipesDBHelper recipesDBHelper;

    private static final int SEARCHES = 100;
    private static final int SEARCHES_WITH_ID = 101;

    @Override
    public boolean onCreate() {
        recipesDBHelper = new RecipesDBHelper(getContext());
        return true;
    }

    private static UriMatcher getUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RecipesContract.RecipeSearch.TABLE_NAME, SEARCHES);
        matcher.addURI(authority, RecipesContract.RecipeSearch.TABLE_NAME + "/*", SEARCHES_WITH_ID);

        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case SEARCHES:
                return RecipesContract.CONTENT_DIR_TYPE;
            case SEARCHES_WITH_ID:
                return RecipesContract.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        //return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case SEARCHES:
                cursor = recipesDBHelper.getReadableDatabase().query(
                        RecipesContract.RecipeSearch.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SEARCHES_WITH_ID:
                cursor = recipesDBHelper.getReadableDatabase().query(
                        RecipesContract.RecipeSearch.TABLE_NAME,
                        projection,
                        RecipesContract.RecipeSearch._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if(contentValues == null){
            return null;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
