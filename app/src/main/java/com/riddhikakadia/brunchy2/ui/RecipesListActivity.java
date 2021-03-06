package com.riddhikakadia.brunchy2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.riddhikakadia.brunchy2.API.RecipeAPI;
import com.riddhikakadia.brunchy2.R;
import com.riddhikakadia.brunchy2.adapter.RecyclerAdapter;
import com.riddhikakadia.brunchy2.model.BaseModel;
import com.riddhikakadia.brunchy2.model.Hit;
import com.riddhikakadia.brunchy2.model.Recipe;
import com.riddhikakadia.brunchy2.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesListActivity extends AppCompatActivity {

    final String LOG_TAG = RecipesListActivity.class.getSimpleName();

    RecyclerView mRecyclerView;
    RecyclerAdapter mAdapter;
    Switch vegSwitch;

    Retrofit retrofit;

    List<Hit> hits;
    List<String> recipeNames;
    List<String> recipeImageURLs;
    List<String> recipeURIs;

    ProgressBar mProgressBar;
    TextView noDataTextView;

    static String recipeToSearch;
    boolean vegRecipeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vegSwitch = (Switch) findViewById(R.id.veg_switch);
        if (MainActivity.sharedPreferences != null) {
            if (MainActivity.sharedPreferences.getBoolean(Constants.PREFS_VEG_SEARCH, true)) {
                vegRecipeSearch = true;
                vegSwitch.setChecked(true);
            } else if (MainActivity.sharedPreferences.getBoolean(Constants.PREFS_VEG_SEARCH, false)) {
                vegRecipeSearch = false;
                vegSwitch.setChecked(false);
            }
        }

        vegSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (vegSwitch.isChecked()) {
                    vegRecipeSearch = true;
                    SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                    editor.putBoolean(Constants.PREFS_VEG_SEARCH, true);
                    editor.commit();

                } else if (!vegSwitch.isChecked()) {
                    vegRecipeSearch = false;
                    SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                    editor.putBoolean(Constants.PREFS_VEG_SEARCH, false);
                    editor.commit();
                }

                searchRecipes();
            }
        });

        noDataTextView = (TextView) findViewById(R.id.no_data_message_text);
        noDataTextView.setVisibility(View.INVISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);
        //Apply GridLayout
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getExtras().get(Constants.RECIPE_TO_SEARCH) != null) {
                recipeToSearch = intent.getExtras().get(Constants.RECIPE_TO_SEARCH).toString();
                //Log.d(LOG_TAG, "*** Recipe to search from search box: " + recipeToSearch);
                getSupportActionBar().setTitle(recipeToSearch);
            }
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //searchRecipes();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Log.d(LOG_TAG, "*** In onSaveInstanceState " + recipeToSearch);
        outState.putString(Constants.RECIPE_TO_SEARCH, recipeToSearch);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        recipeToSearch = savedInstanceState.getString(Constants.RECIPE_TO_SEARCH);
        //Log.d(LOG_TAG, "*** In onRestoreInstanceState " + recipeToSearch);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(LOG_TAG, "*** In onResume " + recipeToSearch);
        getSupportActionBar().setTitle(recipeToSearch);
        searchRecipes();
    }

    public void searchRecipes() {
        new AsyncTask<String, Void, Integer>() {
            int total_hits;

            @Override
            protected void onPreExecute() {
                //Log.d(LOG_TAG, "*** In recipelist onPreExecute");
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(String... params) {
                //Log.d(LOG_TAG, "*** In recipelist doInBackground");
                RecipeAPI service = retrofit.create(RecipeAPI.class);
                Call<BaseModel> recipeSearch = null;
                if (vegRecipeSearch) {
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
                            //Log.d(LOG_TAG, "*** Hits size: " + hits.size());

                            if (hits.size() == 0) {
                                total_hits = 0;
                                //Log.d(LOG_TAG, "*** In recipelist doInBackground total_hits " + total_hits);

                                mProgressBar.setVisibility(View.INVISIBLE);
                                noDataTextView.setText(getResources().getString(R.string.no_recipes_found));
                                noDataTextView.setVisibility(View.VISIBLE);
                            } else {
                                total_hits = hits.size();

                                for (int i = 0; i < hits.size(); i++) {

                                    Recipe recipe = hits.get(i).getRecipe();

                                    String label = recipe.getLabel();
                                    //Log.d(LOG_TAG, "recipe label: " + label);
                                    recipeNames.add(label);

                                    String recipeImageURL = recipe.getImage();
                                    //Log.d(LOG_TAG, "recipe image url: " + recipeImageURL);
                                    recipeImageURLs.add(recipeImageURL);

                                    String recipeURI = recipe.getUri();
                                    //Log.d(LOG_TAG, "recipe uri: " + recipeURI);
                                    recipeURIs.add(recipeURI);
                                }

                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Log.d(LOG_TAG, "recipeNames size: " + recipeNames.size() + " recipeURLs size: " + recipeImageURLs.size());
                                mAdapter = new RecyclerAdapter(recipeNames, recipeImageURLs, recipeURIs);
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseModel> call, Throwable t) {
                        Log.e(LOG_TAG, "*** Error in retrofit");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        noDataTextView.setText(getResources().getString(R.string.no_recipes_found));
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                });

                return total_hits;
            }

            @Override
            public void onPostExecute(Integer total_hits) {
                //Log.d(LOG_TAG, "*** In recipelist onPostExecute total_hits " + total_hits);
                //update UI
//                mProgressBar.setVisibility(View.INVISIBLE);

//                if (total_hits == 0) {
//                    noDataTextView.setText(getResources().getString(R.string.no_recipes_found));
//                    noDataTextView.setVisibility(View.VISIBLE);
//                } else {
//                    Log.d(LOG_TAG, "recipeNames size: " + recipeNames.size() + " recipeURLs size: " + recipeImageURLs.size());
//
//                    mRecyclerView.setAdapter(mAdapter);
//                    mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
//                }
            }
        }.execute(recipeToSearch);
    }
}
