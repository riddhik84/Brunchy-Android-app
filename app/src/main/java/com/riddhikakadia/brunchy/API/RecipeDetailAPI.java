package com.riddhikakadia.brunchy.API;

import com.riddhikakadia.brunchy.BuildConfig;
import com.riddhikakadia.brunchy.model.RecipeDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface RecipeDetailAPI {

//    @Streaming
//    @GET("search?app_id=&app_key=&r=http://www.edamam.com/ontologies/edamam.owl%23recipe_637913ec61d9da69eb451818c3293df2")
//    Call<List<RecipeDetail>> getRecipeDetailTest();

    @Streaming
    @GET("search?app_id=" + BuildConfig.APP_ID + "&app_key=" + BuildConfig.APP_KEY)
    Call<List<RecipeDetail>> getRecipeDetail(@Query("r") String r);
}
