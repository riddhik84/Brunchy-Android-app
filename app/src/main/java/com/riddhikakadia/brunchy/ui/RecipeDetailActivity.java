package com.riddhikakadia.brunchy.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.riddhikakadia.brunchy.API.RecipeDetailAPI;
import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.model.RecipeDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeDetailActivity extends AppCompatActivity {

    final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    final String BASE_URL = "https://api.edamam.com/";
    final String RECIPE_ID = "RECIPE_ID";
    String recipe_id = "";

    Retrofit retrofit;

    TextView recipe_name;
    ImageView recipe_image;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        //mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipe_name = (TextView) findViewById(R.id.recipe_detail_name);
        recipe_image = (ImageView) findViewById(R.id.recipe_detail_image);

        Intent intent = getIntent();
        if (intent.getExtras().get(RECIPE_ID) != null) {
            recipe_id = intent.getExtras().getString(RECIPE_ID).toString();
            recipe_id = recipe_id.replace("#", "%23");
            Log.d(LOG_TAG, "RK*** recipe_id: " + recipe_id);
        } else {
            Log.e(LOG_TAG, "RK*** Intent null");
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        new AsyncTask<String, Void, Void>() {

//            @Override
//            protected void onPreExecute() {
//                mProgressBar.setVisibility(View.VISIBLE);
//            }

            @Override
            protected Void doInBackground(String... params) {
                RecipeDetailAPI service = retrofit.create(RecipeDetailAPI.class);
                Call<List<RecipeDetail>> recipeDetailCall = service.getRecipeDetail(recipe_id);

                recipeDetailCall.enqueue(new Callback<List<RecipeDetail>>() {
                    @Override
                    public void onResponse(Call<List<RecipeDetail>> call, Response<List<RecipeDetail>> response) {
                        String responseBody = response.body().toString();
                        Log.d(LOG_TAG, "RK*** retrofit response body: " + responseBody);

                        try {
                            RecipeDetail rd = response.body().get(0);

                            String recipeName = rd.getLabel();
                            Log.d(LOG_TAG, "RK*** recipeName: " + recipeName);
                            //mCollapsingToolbarLayout.setTitle(recipeName);
                            //mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
                            // mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedTitleTextAppearance);
                            // mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedTitleTextAppearance);

                            recipe_name.setText(recipeName);

                            String recipeImage = rd.getImage();
                            Picasso.with(getApplicationContext())
                                    .load(recipeImage)
                                    .resize(1000, 700)
                                    .noFade()
                                    .into(recipe_image);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RecipeDetail>> call, Throwable t) {
                        Log.e(LOG_TAG, "RK*** Error in retrofit");
                        Log.d(LOG_TAG, "RK*** Error message:" + t.getMessage());
                    }
                });
                return null;
            }
        }.execute(recipe_id);

    }
}
