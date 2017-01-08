package com.riddhikakadia.brunchy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
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
    final String RECIPE_TO_SEARCH = "RECIPE_TO_SEARCH";
    final String WEBVIEW_LINK = "WEBVIEW_LINK";
    String recipe_id = "";
    String recipe_to_search = "";

    final private int default_color = 0x000000;

    Retrofit retrofit;

    TextView calories_per_serving;
    TextView total_serving;
    TextView total_ingredients;

    ImageView recipe_detail_image;
    TextView ingredient_texts;
    Button recipe_link_button;
    FloatingActionButton favorite_recipe;
    View upButton;

    CollapsingToolbarLayout mCollapsingToolbarLayout;

    String recipeLink = "";
    String shareRecipeLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calories_per_serving = (TextView) findViewById(R.id.calories_per_serving);
        recipe_detail_image = (ImageView) findViewById(R.id.recipe_detail_image);
        ingredient_texts = (TextView) findViewById(R.id.ingredient_texts);
        recipe_link_button = (Button) findViewById(R.id.recipe_link_button);
        favorite_recipe = (FloatingActionButton) findViewById(R.id.favorite_recipe_fab);
        total_serving = (TextView) findViewById(R.id.total_serving);
        total_ingredients = (TextView) findViewById(R.id.total_ingredients);

        upButton = findViewById(R.id.action_up);

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

                            mCollapsingToolbarLayout.setTitle(recipeName);
                            mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
                            mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedTitleTextAppearance);
                            mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedTitleTextAppearance);

                            String recipeImage = rd.getImage();
                            Picasso.with(getApplicationContext())
                                    .load(recipeImage)
                                    .fit()
                                    .noFade()
                                    .into(recipe_detail_image, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Bitmap bitmap = ((BitmapDrawable) recipe_detail_image.getDrawable()).getBitmap();
                                            if (bitmap != null) {
                                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                                    @Override
                                                    public void onGenerated(Palette palette) {
                                                        int mutedColor = palette.getMutedColor(default_color);
                                                        int darkMutedColor = palette.getDarkMutedColor(default_color);
                                                        int vibrantColor = palette.getVibrantColor(default_color);
                                                        int darkVibrantColor = palette.getDarkVibrantColor(default_color);

                                                        mCollapsingToolbarLayout.setContentScrimColor(mutedColor);
                                                        //mCollapsingToolbarLayout.setStatusBarScrimColor(darkMutedColor);

                                                        //Change color for status bar
                                                        Window window = getWindow();
                                                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                                        if (Build.VERSION.SDK_INT >= 21) {
                                                            //Setting darker shade on statusbar
                                                            //int statusBarDarkColor = Utility.darken(mutedColor, 1.0);
                                                            window.setStatusBarColor(darkMutedColor);
                                                        }
                                                        Log.d(LOG_TAG, "RK darkMutedColor: " + darkMutedColor);
                                                        total_ingredients.setBackgroundColor(darkMutedColor);
                                                        calories_per_serving.setBackgroundColor(mutedColor);
                                                        total_serving.setBackgroundColor(darkMutedColor);

                                                        recipe_link_button.setBackgroundColor(mutedColor);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onError() {
                                            int backColor = getResources().getColor(R.color.colorBrown_300);
                                            total_ingredients.setBackgroundColor(backColor);
                                            calories_per_serving.setBackgroundColor(backColor);
                                            total_serving.setBackgroundColor(backColor);

                                            recipe_link_button.setBackgroundColor(backColor);
                                        }
                                    });

                            double calories = rd.getCalories();
                            int yield = rd.getYield().intValue();
                            total_serving.setText("Total serving \n" + yield + "");

                            int caloriesPerServing = (int) (calories / yield);
                            Log.d(LOG_TAG, "caloriesPerServing: " + caloriesPerServing);
                            calories_per_serving.setText("Calories per serving \n" + caloriesPerServing);

                            List<String> dietLabels = rd.getDietLabels();
                            List<Object> healthLabels = rd.getHealthLabels();
                            List<String> ingredientLines = rd.getIngredientLines();
                            int totalIngredients = ingredientLines.size();
                            total_ingredients.setText("Total ingredients \n" + totalIngredients);

                            String ingredients = "Ingredients: \n\n";
                            for (String s : ingredientLines) {
                                ingredients = ingredients + "+ " + s + "\n";
                            }
                            ingredient_texts.setText(ingredients.trim());

                            recipeLink = rd.getUrl();
                            shareRecipeLink = rd.getShareAs();

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

        recipe_link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailActivity.this, WebViewActivity.class);
                intent.putExtra(WEBVIEW_LINK, recipeLink);
                startActivity(intent);
            }
        });

        favorite_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.putExtra()
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "rkakadia mUpButton clicked");
                onSupportNavigateUp();
            }
        });
    }
}
