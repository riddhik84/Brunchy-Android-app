package com.riddhikakadia.brunchy.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.riddhikakadia.brunchy.API.RecipeDetailAPI;
import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.model.CA;
import com.riddhikakadia.brunchy.model.CHOCDF;
import com.riddhikakadia.brunchy.model.CHOLE;
import com.riddhikakadia.brunchy.model.ENERCKCAL;
import com.riddhikakadia.brunchy.model.FAMS;
import com.riddhikakadia.brunchy.model.FAPU;
import com.riddhikakadia.brunchy.model.FASAT;
import com.riddhikakadia.brunchy.model.FAT;
import com.riddhikakadia.brunchy.model.FATRN;
import com.riddhikakadia.brunchy.model.FE;
import com.riddhikakadia.brunchy.model.FIBTG;
import com.riddhikakadia.brunchy.model.K;
import com.riddhikakadia.brunchy.model.MG;
import com.riddhikakadia.brunchy.model.NA;
import com.riddhikakadia.brunchy.model.PROCNT;
import com.riddhikakadia.brunchy.model.RecipeDetail;
import com.riddhikakadia.brunchy.model.SUGAR;
import com.riddhikakadia.brunchy.model.TotalNutrients;
import com.riddhikakadia.brunchy.model.ZN;
import com.riddhikakadia.brunchy.util.Global;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.riddhikakadia.brunchy.data.RecipesContract.FavoriteRecipes;
import static com.riddhikakadia.brunchy.util.Constants.ACTION_DATA_UPDATED;
import static com.riddhikakadia.brunchy.util.Constants.BASE_URL;
import static com.riddhikakadia.brunchy.util.Constants.RECIPE_ID;
import static com.riddhikakadia.brunchy.util.Constants.WEBVIEW_LINK;

public class RecipeDetailActivity extends AppCompatActivity {

    final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();

    boolean favorited = false;

    String recipe_id = "";
    String recipe_id_raw = "";
    String recipe_to_search = "";

    final private int default_color = 0x000000;

    Retrofit retrofit;

    TextView calories_per_serving;
    TextView total_serving;
    TextView total_ingredients;
    TextView health_labels;
    //TextView nutrition_info;

    ImageView recipe_detail_image;
    TextView ingredient_texts;
    Button recipe_link_button;
    FloatingActionButton favorite_recipe;

    ImageButton reminder_button;
    ImageButton share_button;
    View upButton;

    GridView nutri_info_gridview;
    CardView health_labels_cardview;

    CollapsingToolbarLayout mCollapsingToolbarLayout;

    String recipeLink = "";
    String shareRecipeLink = "";
    String recipeName = "";
    String recipeImage = "";
    String ingredients = "";
    String dietlabels = "";
    String healthlabels = "";

    int totalIngredients = 0;
    int caloriesPerServing = 0;
    int yield = 0;

    private static final String[] FAVOURITE_RECIPE_COLUMNS = {
            FavoriteRecipes._ID,
            FavoriteRecipes.COLUMN_USER_EMAIL,
            FavoriteRecipes.COLUMN_RECIPE_ID,
            FavoriteRecipes.COLUMN_RECIPE_NAME,
            FavoriteRecipes.COLUMN_RECIPE_PHOTO_LINK,
            FavoriteRecipes.COLUMN_TOTAL_INGREDIENTS,
            FavoriteRecipes.COLUMN_CALORIES_PER_SERVING,
            FavoriteRecipes.COLUMN_TOTAL_SERVING,
            FavoriteRecipes.COLUMN_INGREDIENTS,
            FavoriteRecipes.COLUMN_HEALTH_LABELS
    };

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
        health_labels = (TextView) findViewById(R.id.health_labels);
        reminder_button = (ImageButton) findViewById(R.id.action_reminder);
        share_button = (ImageButton) findViewById(R.id.action_share);
        //nutrition_info = (TextView) findViewById(R.id.nutrition_texts);
        nutri_info_gridview = (GridView) findViewById(R.id.nutrition_gridview);
        health_labels_cardview = (CardView) findViewById(R.id.health_labels_cardview);

        upButton = findViewById(R.id.action_up);

        Intent intent = getIntent();
        if (intent.getExtras().get(RECIPE_ID) != null) {
            recipe_id = intent.getExtras().getString(RECIPE_ID).toString();
            recipe_id_raw = recipe_id;

            recipe_id = recipe_id.replace("#", "%23");
            //Log.d(LOG_TAG, "RK*** recipe_id: " + recipe_id);
        } else {
            //Log.e(LOG_TAG, "RK*** Intent null");
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loadRecipe();

        recipe_link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailActivity.this, WebViewActivity.class);
                intent.putExtra(WEBVIEW_LINK, recipeLink);
                startActivity(intent);
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(LOG_TAG, "rkakadia mUpButton clicked");
                onSupportNavigateUp();
            }
        });

        reminder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar beginTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.HOUR, endTime.get(Calendar.HOUR) + 1);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title) + recipeName)
                        .putExtra(CalendarContract.Events.DESCRIPTION, recipeLink.toString())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_url_text));
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareRecipeLink);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_recipe_header)));
            }
        });

        //recipe favorite operation
        markFavoriteRecipe();
    }

    public void loadRecipe() {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(String... params) {
                RecipeDetailAPI service = retrofit.create(RecipeDetailAPI.class);
                Call<List<RecipeDetail>> recipeDetailCall = service.getRecipeDetail(recipe_id);

                recipeDetailCall.enqueue(new Callback<List<RecipeDetail>>() {
                    @Override
                    public void onResponse(Call<List<RecipeDetail>> call, Response<List<RecipeDetail>> response) {
                        String responseBody = response.body().toString();
                        //Log.d(LOG_TAG, "RK*** retrofit response body: " + responseBody);

                        try {
                            RecipeDetail rd = response.body().get(0);

                            recipeName = rd.getLabel();
                            //Log.d(LOG_TAG, "RK*** recipeName: " + recipeName);

                            mCollapsingToolbarLayout.setTitle(recipeName);
                            mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
                            mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedTitleTextAppearance);
                            mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedTitleTextAppearance);

                            recipeImage = rd.getImage();
                            Picasso.with(getApplicationContext())
                                    .load(recipeImage)
                                    .fit()
                                    .noFade()
                                    .placeholder(R.drawable.placeholder_detail_screen)
                                    .error(R.drawable.placeholder_detail_screen)
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
                                                        //int vibrantColor = palette.getVibrantColor(default_color);
                                                        //int darkVibrantColor = palette.getDarkVibrantColor(default_color);

                                                        mCollapsingToolbarLayout.setContentScrimColor(mutedColor);
                                                        //mCollapsingToolbarLayout.setStatusBarScrimColor(darkMutedColor);

                                                        //Change color for status bar
                                                        Window window = getWindow();
                                                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                                        if (Build.VERSION.SDK_INT >= 21) {
                                                            //Setting darker shade on statusbar
                                                            //int statusBarDarkColor = Utility.darken(mutedColor, 1.0);
                                                            if (darkMutedColor == 0) {
                                                                window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
                                                            } else {
                                                                window.setStatusBarColor(darkMutedColor);
                                                            }
                                                        }
                                                        //Log.d(LOG_TAG, "*** darkMutedColor: " + darkMutedColor);
                                                        if (darkMutedColor == 0) {
                                                            int defaultAccentColor = getResources().getColor(R.color.colorAccent);
                                                            total_ingredients.setBackgroundColor(defaultAccentColor);
                                                            calories_per_serving.setBackgroundColor(mutedColor);
                                                            total_serving.setBackgroundColor(defaultAccentColor);

                                                            recipe_link_button.setBackgroundColor(defaultAccentColor);
                                                        } else {
                                                            total_ingredients.setBackgroundColor(darkMutedColor);
                                                            calories_per_serving.setBackgroundColor(mutedColor);
                                                            total_serving.setBackgroundColor(darkMutedColor);

                                                            recipe_link_button.setBackgroundColor(mutedColor);
                                                        }

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

                            recipe_detail_image.setContentDescription(recipeName);

                            double calories = rd.getCalories();
                            yield = (int) Math.round(rd.getYield());
                            total_serving.setText(getString(R.string.total_serving) + " \n" + yield + "");

                            caloriesPerServing = (int) (calories / yield);
                            //Log.d(LOG_TAG, "caloriesPerServing: " + caloriesPerServing);
                            calories_per_serving.setText(getString(R.string.calories_per_serving_text) + "\n"
                                    + caloriesPerServing);

                            List<String> ingredientLines = rd.getIngredientLines();
                            totalIngredients = ingredientLines.size();
                            total_ingredients.setText(getString(R.string.total_ingredients_text) + "\n" + totalIngredients);

                            //ingredients = getString(R.string.ingredients_header) + " \n";
                            for (String s : ingredientLines) {
                                ingredients = ingredients + "+ " + s + "\n";
                            }
                            ingredient_texts.setText(ingredients.trim());

                            List<String> dietLabels = rd.getDietLabels();
                            List<Object> healthLabels = rd.getHealthLabels();

                            if (dietLabels != null && dietLabels.size() > 0) {
                                for (String dietLbl : dietLabels) {
                                    dietlabels = dietlabels + dietLbl + "," + " ";
                                }
                            }
                            if (healthLabels != null && healthLabels.size() > 0) {
                                for (Object healthLbl : healthLabels) {
                                    healthlabels = healthlabels + healthLbl + "," + " ";
                                }
                            }
                            //Log.d(LOG_TAG, "*** dietlabels: " + dietLabels + " healthlabels: " + healthLabels);

                            if (dietLabels.size() > 0 || healthLabels.size() > 0) {
                                health_labels.setText(dietlabels + healthlabels);
                            } else {
                                health_labels_cardview.setVisibility(View.INVISIBLE);
                            }

                            recipeLink = rd.getUrl();
                            shareRecipeLink = rd.getShareAs();

                            //Nutrition info
                            ArrayList<String> nutritionInfoArray = new ArrayList<>();
                            TotalNutrients totalNutrients = rd.getTotalNutrients();

                            if (totalNutrients.getENERCKCAL() != null) {
                                ENERCKCAL enerckcal = totalNutrients.getENERCKCAL();
                                String enerckcal_label = enerckcal.getLabel();
                                nutritionInfoArray.add(enerckcal_label);
                                int enerckcal_quantity = enerckcal.getQuantity().intValue();
                                String enerckcal_unit = enerckcal.getUnit();
                                String enerckcal_info = (enerckcal_quantity / yield) + " " + enerckcal_unit;
                                nutritionInfoArray.add(enerckcal_info);
                                //Log.d(LOG_TAG, "*** enerckcal_info: " + enerckcal_info);
                            }

                            if (totalNutrients.getFAT() != null) {
                                FAT fat = totalNutrients.getFAT();
                                String fat_label = fat.getLabel();
                                nutritionInfoArray.add(fat_label);
                                int fat_quantity = fat.getQuantity().intValue();
                                String fat_unit = fat.getUnit();
                                String fat_info = (fat_quantity / yield) + " " + fat_unit;
                                nutritionInfoArray.add(fat_info);
                                //Log.d(LOG_TAG, "*** fat_info: " + fat_info);
                            }

                            if (totalNutrients.getFASAT() != null) {
                                FASAT fasat = totalNutrients.getFASAT();
                                String fasat_label = fasat.getLabel();
                                nutritionInfoArray.add(fasat_label + " " + getString(R.string.fat_label));
                                int fasat_quantity = fasat.getQuantity().intValue();
                                String fasat_unit = fasat.getUnit();
                                String fasat_info = (fasat_quantity / yield) + " " + fasat_unit;
                                nutritionInfoArray.add(fasat_info);
                                //Log.d(LOG_TAG, "*** fasat_info: " + fasat_info);
                            }

                            if (totalNutrients.getFATRN() != null) {
                                FATRN fatrn = totalNutrients.getFATRN();
                                String fatrn_label = fatrn.getLabel();
                                nutritionInfoArray.add(fatrn_label + " " + getString(R.string.fat_label));
                                int fatrn_quantity = fatrn.getQuantity().intValue();
                                String fatrn_unit = fatrn.getUnit();
                                String fatrn_info = (fatrn_quantity / yield) + " " + fatrn_unit;
                                nutritionInfoArray.add(fatrn_info);
                                //Log.d(LOG_TAG, "*** fatrn_info: " + fatrn_info);
                            }

                            if (totalNutrients.getFAMS() != null) {
                                FAMS fams = totalNutrients.getFAMS();
                                String fams_label = fams.getLabel();
                                nutritionInfoArray.add(fams_label + " " + getString(R.string.fat_label));
                                int fams_quantity = fams.getQuantity().intValue();
                                String fams_unit = fams.getUnit();
                                String fams_info = (fams_quantity / yield) + " " + fams_unit;
                                nutritionInfoArray.add(fams_info);
                                //Log.d(LOG_TAG, "*** fams_info: " + fams_info);
                            }

                            if (totalNutrients.getFAPU() != null) {
                                FAPU fapu = totalNutrients.getFAPU();
                                String fapu_label = fapu.getLabel();
                                nutritionInfoArray.add(fapu_label + " " + getString(R.string.fat_label));
                                int fapu_quantity = fapu.getQuantity().intValue();
                                String fapu_unit = fapu.getUnit();
                                String fapu_info = (fapu_quantity / yield) + " " + fapu_unit;
                                nutritionInfoArray.add(fapu_info);
                                //Log.d(LOG_TAG, "*** fapu_info: " + fapu_info);
                            }

                            if (totalNutrients.getCHOCDF() != null) {
                                CHOCDF chocdf = totalNutrients.getCHOCDF();
                                String chocdf_label = chocdf.getLabel();
                                nutritionInfoArray.add(chocdf_label);
                                int chocdf_quantity = chocdf.getQuantity().intValue();
                                String chocdf_unit = chocdf.getUnit();
                                String chocdf_info = (chocdf_quantity / yield) + " " + chocdf_unit;
                                nutritionInfoArray.add(chocdf_info);
                                //Log.d(LOG_TAG, "*** chocdf_info: " + chocdf_info);
                            }

                            if (totalNutrients.getFIBTG() != null) {
                                FIBTG fibtg = totalNutrients.getFIBTG();
                                String fibtg_label = fibtg.getLabel();
                                nutritionInfoArray.add(fibtg_label);
                                int fibtg_quantity = fibtg.getQuantity().intValue();
                                String fibtg_unit = fibtg.getUnit();
                                String fibtg_info = (fibtg_quantity / yield) + " " + fibtg_unit;
                                nutritionInfoArray.add(fibtg_info);
                                //Log.d(LOG_TAG, "*** fibtg_info: " + fibtg_info);
                            }

                            if (totalNutrients.getSUGAR() != null) {
                                SUGAR sugar = totalNutrients.getSUGAR();
                                String sugar_label = sugar.getLabel();
                                nutritionInfoArray.add(sugar_label);
                                int sugar_quantity = sugar.getQuantity().intValue();
                                String sugar_unit = sugar.getUnit();
                                String sugar_info = (sugar_quantity / yield) + " " + sugar_unit;
                                nutritionInfoArray.add(sugar_info);
                                //Log.d(LOG_TAG, "*** sugar_info: " + sugar_info);
                            }

                            if (totalNutrients.getPROCNT() != null) {
                                PROCNT procnt = totalNutrients.getPROCNT();
                                String procnt_label = procnt.getLabel();
                                nutritionInfoArray.add(procnt_label);
                                int procnt_quantity = procnt.getQuantity().intValue();
                                String procnt_unit = procnt.getUnit();
                                String procnt_info = (procnt_quantity / yield) + " " + procnt_unit;
                                nutritionInfoArray.add(procnt_info);
                                //Log.d(LOG_TAG, "*** procnt_info: " + procnt_info);
                            }

                            if (totalNutrients.getCHOLE() != null) {
                                CHOLE chole = totalNutrients.getCHOLE();
                                String chole_label = chole.getLabel();
                                nutritionInfoArray.add(chole_label);
                                int chole_quantity = chole.getQuantity().intValue();
                                String chole_unit = chole.getUnit();
                                String chole_info = (chole_quantity / yield) + " " + chole_unit;
                                nutritionInfoArray.add(chole_info);
                                //Log.d(LOG_TAG, "*** chole_info: " + chole_info);
                            }

                            if (totalNutrients.getNA() != null) {
                                NA na = totalNutrients.getNA();
                                String na_label = na.getLabel();
                                nutritionInfoArray.add(na_label);
                                int na_quantity = na.getQuantity().intValue();
                                String na_unit = na.getUnit();
                                String na_info = (na_quantity / yield) + " " + na_unit;
                                nutritionInfoArray.add(na_info);
                                //Log.d(LOG_TAG, "*** na_info: " + na_info);
                            }

                            if (totalNutrients.getCA() != null) {
                                CA ca = totalNutrients.getCA();
                                String ca_label = ca.getLabel();
                                nutritionInfoArray.add(ca_label);
                                int ca_quantity = ca.getQuantity().intValue();
                                String ca_unit = ca.getUnit();
                                String ca_info = (ca_quantity / yield) + " " + ca_unit;
                                nutritionInfoArray.add(ca_info);
                                //Log.d(LOG_TAG, "*** ca_info: " + ca_info);
                            }

                            if (totalNutrients.getMG() != null) {
                                MG mg = totalNutrients.getMG();
                                String mg_label = mg.getLabel();
                                nutritionInfoArray.add(mg_label);
                                int mg_quantity = mg.getQuantity().intValue();
                                String mg_unit = mg.getUnit();
                                String mg_info = (mg_quantity / yield) + " " + mg_unit;
                                nutritionInfoArray.add(mg_info);
                                //Log.d(LOG_TAG, "*** mg_info: " + mg_info);
                            }

                            if (totalNutrients.getK() != null) {
                                K k = totalNutrients.getK();
                                String k_label = k.getLabel();
                                nutritionInfoArray.add(k_label);
                                int k_quantity = k.getQuantity().intValue();
                                String k_unit = k.getUnit();
                                String k_info = (k_quantity / yield) + " " + k_unit;
                                nutritionInfoArray.add(k_info);
                                //Log.d(LOG_TAG, "*** k_info: " + k_info);
                            }

                            if (totalNutrients.getFE() != null) {
                                FE fe = totalNutrients.getFE();
                                String fe_label = fe.getLabel();
                                nutritionInfoArray.add(fe_label);
                                int fe_quantity = fe.getQuantity().intValue();
                                String fe_unit = fe.getUnit();
                                String fe_info = (fe_quantity / yield) + " " + fe_unit;
                                nutritionInfoArray.add(fe_info);
                                //Log.d(LOG_TAG, "*** fe_info: " + fe_info);
                            }

                            if (totalNutrients.getZN() != null) {
                                ZN zn = totalNutrients.getZN();
                                String zn_label = zn.getLabel();
                                nutritionInfoArray.add(zn_label);
                                int zn_quantity = zn.getQuantity().intValue();
                                String zn_unit = zn.getUnit();
                                String zn_info = (zn_quantity / yield) + " " + zn_unit;
                                nutritionInfoArray.add(zn_info);
                                //Log.d(LOG_TAG, "*** zn_info: " + zn_info);
                            }

                            //nutrition_info.setText(nutriInfo);
                            ArrayAdapter<String> nutriInfoAdapter = new ArrayAdapter<String>(
                                    getApplicationContext(),
                                    R.layout.nutrition_info_list_item,
                                    R.id.nutri_info_item,
                                    nutritionInfoArray);

                            //nutri_info_gridview.setAdapter(new NutritionInfoAdapter(getApplicationContext(), nutritionInfoArray));
                            nutri_info_gridview.setAdapter(nutriInfoAdapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RecipeDetail>> call, Throwable t) {
                        //Log.e(LOG_TAG, "RK*** Error in retrofit");
                        //Log.d(LOG_TAG, "RK*** Error message:" + t.getMessage());
                    }
                });
                return null;
            }
        }.execute(recipe_id);
    }

    public void markFavoriteRecipe() {
        final Cursor cursorFav = getApplication().getContentResolver().query(FavoriteRecipes.buildFavoriteRecipeUri(),
                FAVOURITE_RECIPE_COLUMNS,
                FavoriteRecipes.COLUMN_RECIPE_ID + "=?", new String[]{recipe_id_raw}, null);
        if (cursorFav != null) {
            if (cursorFav.getCount() > 0) {
                //Log.d(LOG_TAG, "*** Recipe favorite");
                favorite_recipe.setImageResource(R.drawable.favourite_icon_selected);
                favorited = true;
            } else {
                //Log.d(LOG_TAG, "*** Recipe not favorite");
                favorite_recipe.setImageResource(R.drawable.favourite_icon_non_selected);
                favorited = false;
            }
        } else {
            //Log.d(LOG_TAG, "*** cursorfav null");
        }
        favorite_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!favorited) {
                    favorited = true;
                    favorite_recipe.setImageResource(R.drawable.favourite_icon_selected);
                    Cursor cursorFav = getApplication().getContentResolver().query(FavoriteRecipes.buildFavoriteRecipeUri(),
                            FAVOURITE_RECIPE_COLUMNS,
                            FavoriteRecipes.COLUMN_RECIPE_ID + "=?", new String[]{recipe_id_raw}, null);
                    if (cursorFav != null) {
                        if (cursorFav.getCount() > 0) {
                            //Log.d(LOG_TAG, "*** Recipe already added as favorite");
                        } else {
                            if (recipeImage.length() > 0) {
                                ContentValues recipeValues = new ContentValues();

                                recipeValues.put(FavoriteRecipes.COLUMN_USER_EMAIL, Global.currentUserEmail);
                                recipeValues.put(FavoriteRecipes.COLUMN_RECIPE_ID, recipe_id_raw);
                                recipeValues.put(FavoriteRecipes.COLUMN_RECIPE_NAME, recipeName);
                                recipeValues.put(FavoriteRecipes.COLUMN_RECIPE_PHOTO_LINK, recipeImage);
                                recipeValues.put(FavoriteRecipes.COLUMN_TOTAL_INGREDIENTS, totalIngredients);
                                recipeValues.put(FavoriteRecipes.COLUMN_CALORIES_PER_SERVING, caloriesPerServing);
                                recipeValues.put(FavoriteRecipes.COLUMN_TOTAL_SERVING, yield);
                                recipeValues.put(FavoriteRecipes.COLUMN_INGREDIENTS, ingredients);
                                recipeValues.put(FavoriteRecipes.COLUMN_HEALTH_LABELS, dietlabels + healthlabels);

                                //add values to database
                                //Log.d(LOG_TAG, "*** FavoriteRecipes.buildFavoriteRecipeUri() " + FavoriteRecipes.buildFavoriteRecipeUri());
                                Uri insertedColumn = getContentResolver().insert(FavoriteRecipes.buildFavoriteRecipeUri(),
                                        recipeValues);
                                //Log.d(LOG_TAG, "*** insertColumn Uri " + insertedColumn.toString());

                                //remove after testing
                                //TestTable();

                                Toast.makeText(getApplicationContext(),
                                        getApplication().getResources().getString(R.string.added_to_fav_toast),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                favorited = false;
                                favorite_recipe.setImageResource(R.drawable.favourite_icon_non_selected);
                                Toast.makeText(getApplicationContext(),
                                        getApplication().getResources().getString(R.string.select_fav_again_text),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        updateWidget();
                    } else {
                        //Log.d(LOG_TAG, "*** cursorfav null");
                    }
                    //close cursor
                    cursorFav.close();
                } else if (favorited) {
                    favorited = false;
                    favorite_recipe.setImageResource(R.drawable.favourite_icon_non_selected);
                    int i = getContentResolver().delete(FavoriteRecipes.buildFavoriteRecipeUri(),
                            FavoriteRecipes.COLUMN_RECIPE_ID + "=?",
                            new String[]{recipe_id_raw}
                    );
                    if (i > 0) {
                        Toast.makeText(getApplicationContext(),
                                getApplication().getResources().getString(R.string.removed_from_fav_toast),
                                Toast.LENGTH_SHORT)
                                .show();
                        updateWidget();
                    }
                }
            }
        });

        //Log.d(LOG_TAG, "*** close cursor");
        cursorFav.close();
    }

    public void updateWidget() {
        //Log.d(LOG_TAG, "*** updateWidget()");
        Intent intent = new Intent(ACTION_DATA_UPDATED).setPackage(this.getPackageName());
        sendBroadcast(intent);
    }

    //comment out below method
//    public void TestTable() {
//
//        RecipesDBHelper db = new RecipesDBHelper(this);
//        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + FavoriteRecipes.TABLE_NAME, null);
//
//        String value;
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            value = cursor.getString(cursor.getColumnIndex(FavoriteRecipes.COLUMN_RECIPE_NAME));
//            Log.d(LOG_TAG, "*** In TestTable() Value recipe name: " + value);
//            cursor.moveToNext();
//        }
//        // Log.d(LOG_TAG, "Close cursor");
//        cursor.close();
//    }
}
