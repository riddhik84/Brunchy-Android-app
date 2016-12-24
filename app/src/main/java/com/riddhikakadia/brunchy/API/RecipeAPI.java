package com.riddhikakadia.brunchy.API;

import com.riddhikakadia.brunchy.model.BaseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface RecipeAPI {

    //Retrofit 2.0
    @GET("search?q=\"\"&from=0&to=20&app_id=7b503982&app_key=6f36cbee9a3cf70ae6b92bb450c5d377")
    Call<BaseModel> getAllRecipes();

    @Streaming
    @GET("search?from=0&to=20&app_id=7b503982&app_key=6f36cbee9a3cf70ae6b92bb450c5d377")
    Call<BaseModel> getSearchRecipe(@Query("q") String q);
}
