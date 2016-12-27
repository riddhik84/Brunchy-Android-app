package com.riddhikakadia.brunchy.API;

import com.riddhikakadia.brunchy.BuildConfig;
import com.riddhikakadia.brunchy.model.BaseModel;
import com.riddhikakadia.brunchy.model.RecipeDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface RecipeAPI {

    //Retrofit 2.0
    @GET("search?q=\"\"&from=0&to=20&app_id=" + BuildConfig.APP_ID + "&app_key=" + BuildConfig.APP_KEY)
    Call<BaseModel> getAllRecipes();

    @Streaming
    @GET("search?from=0&to=20&app_id=" + BuildConfig.APP_ID + "&app_key=" + BuildConfig.APP_KEY)
    Call<BaseModel> getSearchRecipe(@Query("q") String q);

    @Streaming
    @GET("search?health=vegetarian&from=0&to=20&app_id=" + BuildConfig.APP_ID + "&app_key=" + BuildConfig.APP_KEY)
    Call<BaseModel> getSearchVegRecipe(@Query("q") String q);

    @Streaming
    @GET("search?app_id=" + BuildConfig.APP_ID + "&app_key=" + BuildConfig.APP_KEY)
    Call<RecipeDetail> getRecipeDetail(@Query("r") String r);
}
