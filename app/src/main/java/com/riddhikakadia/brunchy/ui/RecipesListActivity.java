package com.riddhikakadia.brunchy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.riddhikakadia.brunchy.API.RecipeAPI;
import com.riddhikakadia.brunchy.model.BaseModel;
import com.riddhikakadia.brunchy.model.Hit;
import com.riddhikakadia.brunchy.model.Recipe;
import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.util.RecipesInfo;
import com.riddhikakadia.brunchy.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.riddhikakadia.brunchy.util.RecipesInfo.RECIPE_SETTINGS;

public class RecipesListActivity extends AppCompatActivity {

    final String LOG_TAG = RecipesListActivity.class.getSimpleName();
    final String RECIPE_TO_SEARCH = "RECIPE_TO_SEARCH";

    SharedPreferences sharedPreferences;

    RecyclerView mRecyclerView;
    RecyclerAdapter mAdapter;
    Switch vegSwitch;

    final String BASE_URL = "https://api.edamam.com/";
    Retrofit retrofit;

    List<Hit> hits;
    List<String> recipeNames;
    List<String> recipeImageURLs;
    List<String> recipeURIs;

    ProgressBar mProgressBar;

    String recipeToSearch = "";
    boolean vegRecipeSearch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(RECIPE_SETTINGS, Context.MODE_PRIVATE);

        vegSwitch = (Switch) findViewById(R.id.veg_switch);
        if (sharedPreferences != null) {
            if (sharedPreferences.getBoolean("VegSearch", true)) {
                vegSwitch.setChecked(true);
            } else {
                vegSwitch.setChecked(false);
            }
        }

        vegSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (vegSwitch.isChecked()) {
                    vegRecipeSearch = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("VegSearch", true);
                    editor.commit();
                } else if (vegSwitch.isChecked() == false) {
                    vegRecipeSearch = false;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("VegSearch", false);
                    editor.commit();
                }
                searchRecipes();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        //Apply GridLayout
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

//        recipeNames = new ArrayList<>();
//        recipeImageURLs = new ArrayList<>();
//        recipeURIs = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getExtras().get(RECIPE_TO_SEARCH) != null) {
                recipeToSearch = intent.getExtras().get(RECIPE_TO_SEARCH).toString();
                Log.d(LOG_TAG, "Recipe to search from search box: " + recipeToSearch);
                getSupportActionBar().setTitle(recipeToSearch);
            }
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchRecipes();
    }

    public void searchRecipes() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(String... params) {
                RecipeAPI service = retrofit.create(RecipeAPI.class);
                Call<BaseModel> recipeSearch = null;
                if (vegRecipeSearch == true) {
                    recipeSearch = service.getSearchVegRecipe(params[0]);
                } else {
                    recipeSearch = service.getSearchRecipe(params[0]);
                }
                recipeSearch.enqueue(new Callback<BaseModel>() {
                    @Override
                    public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                        try {
                            recipeNames = new ArrayList<>();
                            recipeImageURLs = new ArrayList<>();
                            recipeURIs = new ArrayList<>();

                            hits = response.body().getHits();
                            for (int i = 0; i < hits.size(); i++) {

                                Recipe recipe = hits.get(i).getRecipe();

                                String label = recipe.getLabel();
                                Log.d(LOG_TAG, "recipe label: " + label);
                                recipeNames.add(label);

                                String recipeImageURL = recipe.getImage();
                                Log.d(LOG_TAG, "recipe image url: " + recipeImageURL);
                                recipeImageURLs.add(recipeImageURL);

                                String recipeURI = recipe.getUri();
                                Log.d(LOG_TAG, "recipe uri: " + recipeURI);
                                recipeURIs.add(recipeURI);
                            }

                            mProgressBar.setVisibility(View.INVISIBLE);
                            Log.d(LOG_TAG, "recipeNames size: " + recipeNames.size() + " recipeURLs size: " + recipeImageURLs.size());
                            mAdapter = new RecyclerAdapter(recipeNames, recipeImageURLs, recipeURIs);
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
