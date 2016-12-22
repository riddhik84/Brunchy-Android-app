package com.riddhikakadia.brunchy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.riddhikakadia.brunchy.API.RecipeAPI;
import com.riddhikakadia.brunchy.Model.BaseModel;
import com.riddhikakadia.brunchy.Model.Hit;
import com.riddhikakadia.brunchy.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesListActivity extends AppCompatActivity {

    final String LOG_TAG = RecipesListActivity.class.getSimpleName();
    final String RECIPE_ID = "RECIPE_ID";

    RecyclerView mRecyclerView;
    RecyclerAdapter mAdapter;

    final String BASE_URL = "https://api.edamam.com/";
    Retrofit retrofit;

    List<Hit> hits;
    List<String> recipeNames;
    List<String> recipeImageURLs;
    ProgressBar mProgressBar;

    String recipeToSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        //Apply GridLayout
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

        recipeNames = new ArrayList<>();
        recipeImageURLs = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            int id = (Integer) intent.getExtras().get(RECIPE_ID);
            recipeToSearch = RecipeLabels.homeRecipeLabels[id];
            Log.d(LOG_TAG, "Recipe to search from search box: " + recipeToSearch);
            getSupportActionBar().setTitle(recipeToSearch);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        new AsyncTask<String, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(String... params) {
                RecipeAPI service = retrofit.create(RecipeAPI.class);
                Call<BaseModel> example = service.getSearchRecipe(params[0]);
                example.enqueue(new Callback<BaseModel>() {
                    @Override
                    public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                        try {
                            hits = response.body().getHits();
                            for (int i = 0; i < hits.size(); i++) {

                                Recipe recipe = hits.get(i).getRecipe();

                                String label = recipe.getLabel();
                                Log.d(LOG_TAG, "label: " + label);
                                recipeNames.add(label);
                                String recipeURL = recipe.getImage();
                                Log.d(LOG_TAG, "url: " + recipeURL);
                                recipeImageURLs.add(recipeURL);
                            }

                            mProgressBar.setVisibility(View.INVISIBLE);
                            Log.d(LOG_TAG, "recipeNames size: " + recipeNames.size() + " recipeURLs size: " + recipeImageURLs.size());
                            mAdapter = new RecyclerAdapter(recipeNames, recipeImageURLs);
                            mRecyclerView.setAdapter(mAdapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel> call, Throwable t) {
                        Log.e(LOG_TAG, "Error in retrofit");
                    }
                });
                return null;
            }
        }.execute(recipeToSearch);
    }
}
