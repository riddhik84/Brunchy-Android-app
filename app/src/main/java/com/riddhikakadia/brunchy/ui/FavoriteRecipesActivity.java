package com.riddhikakadia.brunchy.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.adapter.RecyclerAdapter;
import com.riddhikakadia.brunchy.util.Global;

import java.util.ArrayList;
import java.util.List;

import static com.riddhikakadia.brunchy.data.RecipesContract.FavoriteRecipes;
import static com.riddhikakadia.brunchy.util.Constants.FAVOURITE_RECIPE_COLUMNS;

public class FavoriteRecipesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = FavoriteRecipesActivity.class.getSimpleName();
    private static final int FAVORITE_RECIPES_LOADER_ID = 1;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    TextView mNoDataTextView;

    RecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.favorite_recipes_header));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mNoDataTextView = (TextView) findViewById(R.id.no_data_message_text);

        mRecyclerView.setHasFixedSize(false);
        mProgressBar.setVisibility(View.INVISIBLE);

        getLoaderManager().initLoader(FAVORITE_RECIPES_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri favorite_movies_uri = FavoriteRecipes.buildFavoriteRecipeUri();
        return new CursorLoader(this,
                favorite_movies_uri,
                FAVOURITE_RECIPE_COLUMNS,
                FavoriteRecipes.COLUMN_USER_EMAIL + "=?",
                new String[]{Global.currentUserEmail},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            //Log.d(LOG_TAG, "*** favorite recipes cursor count: " + data.getCount());
            int totalCount = data.getCount();
            List<String> recipeNames = new ArrayList<>();
            List<String> recipeImageURLs = new ArrayList<>();
            List<String> recipeURIs = new ArrayList<>();

            data.moveToFirst();
            do {
                //Log.d(LOG_TAG, "*** data column");
                String recipeName = data.getString(data.getColumnIndex(FavoriteRecipes.COLUMN_RECIPE_NAME));
                String recipeImageURL = data.getString(data.getColumnIndex(FavoriteRecipes.COLUMN_RECIPE_PHOTO_LINK));
                String recipeURI = data.getString(data.getColumnIndex(FavoriteRecipes.COLUMN_RECIPE_ID));

                recipeNames.add(recipeName);
                recipeImageURLs.add(recipeImageURL);
                recipeURIs.add(recipeURI);

                //totalCount--;
                //data.moveToNext();
            } while (data.moveToNext());

            mRecyclerAdapter = new RecyclerAdapter(recipeNames, recipeImageURLs, recipeURIs);
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mNoDataTextView.setVisibility(View.VISIBLE);
            mNoDataTextView.setText(getString(R.string.no_favorite_recipes_message));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
