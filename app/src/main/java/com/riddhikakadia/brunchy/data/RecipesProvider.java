package com.riddhikakadia.brunchy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.riddhikakadia.brunchy.data.RecipesContract.FavoriteRecipes;
import static com.riddhikakadia.brunchy.data.RecipesContract.RecipeSearch;

/**
 * Created by RKs on 1/4/2017.
 */

public class RecipesProvider extends ContentProvider {

    private final String LOG_TAG = RecipesProvider.class.getSimpleName();

    private static final UriMatcher mUriMatcher = getUriMatcher();
    private RecipesDBHelper recipesDBHelper;

    private static final int SEARCHES = 100;
    private static final int SEARCHES_WITH_ID = 101;
    private static final int FAVORITE_RECIPES = 200;
    private static final int FAVORITE_RECIPES_WITH_ID = 201;

    @Override
    public boolean onCreate() {
        recipesDBHelper = new RecipesDBHelper(getContext());
        return true;
    }

    private static UriMatcher getUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RecipeSearch.TABLE_NAME, SEARCHES);
        matcher.addURI(authority, RecipeSearch.TABLE_NAME + "/*", SEARCHES_WITH_ID);

        matcher.addURI(authority, FavoriteRecipes.TABLE_NAME, FAVORITE_RECIPES);
        matcher.addURI(authority, FavoriteRecipes.TABLE_NAME + "/*", FAVORITE_RECIPES_WITH_ID);

        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case SEARCHES:
                return RecipeSearch.CONTENT_DIR_TYPE;
            case SEARCHES_WITH_ID:
                return RecipeSearch.CONTENT_ITEM_TYPE;
            case FAVORITE_RECIPES:
                return FavoriteRecipes.CONTENT_DIR_TYPE;
            case FAVORITE_RECIPES_WITH_ID:
                return FavoriteRecipes.CONTENT_ITEM_TYPE;
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
                        RecipeSearch.TABLE_NAME,
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
                        RecipeSearch.TABLE_NAME,
                        projection,
                        RecipeSearch._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_RECIPES:
                cursor = recipesDBHelper.getReadableDatabase().query(
                        FavoriteRecipes.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_RECIPES_WITH_ID:
                cursor = recipesDBHelper.getReadableDatabase().query(
                        FavoriteRecipes.TABLE_NAME,
                        projection,
                        FavoriteRecipes._ID + " = ? ",
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
        if (contentValues == null) {
            return null;
        }
        final SQLiteDatabase db = recipesDBHelper.getWritableDatabase();
        Uri returnUri;
        long id = 0;

        switch (mUriMatcher.match(uri)) {
            case SEARCHES:
                id = db.insert(RecipeSearch.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = RecipeSearch.buildSearchUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            case SEARCHES_WITH_ID:
                id = db.insert(RecipeSearch.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = RecipeSearch.buildSearchUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            case FAVORITE_RECIPES:
                id = db.insert(FavoriteRecipes.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = FavoriteRecipes.buildFavoriteRecipeUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            case FAVORITE_RECIPES_WITH_ID:
                id = db.insert(FavoriteRecipes.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = FavoriteRecipes.buildFavoriteRecipeUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = recipesDBHelper.getReadableDatabase();
        int match = mUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (match) {
            case SEARCHES:
                rowsDeleted = db.delete(RecipeSearch.TABLE_NAME, selection, selectionArgs);
                break;
            case SEARCHES_WITH_ID:
                rowsDeleted = db.delete(RecipeSearch.TABLE_NAME, RecipeSearch._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVORITE_RECIPES:
                rowsDeleted = db.delete(FavoriteRecipes.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_RECIPES_WITH_ID:
                rowsDeleted = db.delete(FavoriteRecipes.TABLE_NAME, FavoriteRecipes._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Invalid Uri to delete " + match);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = recipesDBHelper.getWritableDatabase();
        int rowsUpdated = 0;
        int match = mUriMatcher.match(uri);

        if (contentValues == null) {
            throw new IllegalArgumentException("null ContentValues");
        }

        switch (match) {
            case SEARCHES:
                rowsUpdated = db.update(
                        RecipeSearch.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case SEARCHES_WITH_ID:
                rowsUpdated = db.update(
                        RecipeSearch.TABLE_NAME,
                        contentValues,
                        RecipeSearch._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVORITE_RECIPES:
                rowsUpdated = db.update(
                        FavoriteRecipes.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case FAVORITE_RECIPES_WITH_ID:
                rowsUpdated = db.update(
                        FavoriteRecipes.TABLE_NAME,
                        contentValues,
                        FavoriteRecipes._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
        }
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
