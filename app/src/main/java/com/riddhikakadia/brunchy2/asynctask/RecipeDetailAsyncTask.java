package com.riddhikakadia.brunchy2.asynctask;

import android.os.AsyncTask;

import com.riddhikakadia.brunchy2.API.RecipeDetailAPI;
import com.riddhikakadia.brunchy2.model.RecipeDetail;
import com.riddhikakadia.brunchy2.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RKs on 1/14/2017.
 */

public class RecipeDetailAsyncTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = RecipeDetailAsyncTask.class.getSimpleName();

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(String... params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeDetailAPI service = retrofit.create(RecipeDetailAPI.class);
        Call<List<RecipeDetail>> recipeDetailCall = service.getRecipeDetail(params[0]);

        recipeDetailCall.enqueue(new Callback<List<RecipeDetail>>() {
            @Override
            public void onResponse(Call<List<RecipeDetail>> call, Response<List<RecipeDetail>> response) {
                String responseBody = response.body().toString();
                //Log.d(LOG_TAG, "RK*** retrofit response body: " + responseBody);

                try {
                    RecipeDetail rd = response.body().get(0);

                    String recipeName = rd.getLabel();
                    //Log.d(LOG_TAG, "RK*** recipeName: " + recipeName);

                    String recipeImage = rd.getImage();
                    //Log.d(LOG_TAG, "RK*** recipeImage: " + recipeImage);

                    double calories = rd.getCalories();
                    int yield = rd.getYield().intValue();

                    int caloriesPerServing = (int) (calories / yield);
                    //Log.d(LOG_TAG, "caloriesPerServing: " + caloriesPerServing);

                    List<String> ingredientLines = rd.getIngredientLines();
                    int totalIngredients = ingredientLines.size();

                    String ingredients = "INGREDIENTS: \n";
                    for (String s : ingredientLines) {
                        ingredients = ingredients + "+ " + s + "\n";
                    }

                    List<String> dietLabels = rd.getDietLabels();
                    List<Object> healthLabels = rd.getHealthLabels();

                    String dietlabels = "";
                    String healthlabels = "";

                    if (dietLabels.size() > 0) {
                        for (String dietLbl : dietLabels) {
                            dietlabels = dietlabels + dietLbl + "," + " ";
                        }
                    }
                    if (healthLabels.size() > 0) {
                        for (Object healthLbl : healthLabels) {
                            healthlabels = healthlabels + healthLbl + "," + " ";
                        }
                    }

                    String recipeLink = rd.getUrl();
                    String shareRecipeLink = rd.getShareAs();

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

}
