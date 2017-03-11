package com.riddhikakadia.brunchy2.util;

import com.riddhikakadia.brunchy2.R;
import com.riddhikakadia.brunchy2.data.RecipesContract;

/**
 * Created by RKs on 12/21/2016.
 */

public class Constants {

    final public static String RECIPE_SETTINGS = "RECIPE_SETTINGS";
    final public static String VEG_SEARCH = "VEG_SEARCH";
    final public static String RECIPE_TO_SEARCH = "RECIPE_TO_SEARCH";
    final public static String WEBVIEW_LINK = "WEBVIEW_LINK";
    final public static String RECIPE_ID = "RECIPE_ID";
    final public static String LOAD_FROM_DB = "LOAD_FROM_DB";
    final public static String RECIPE_LIST_POSITION = "RECIPE_LIST_POSITION";

    public static final String ACTION_DATA_UPDATED =
            "com.riddhikakadia.brunchy.ACTION_DATA_UPDATED";

    final public static String BASE_URL = "https://api.edamam.com/";

    final public static String PREFS_VEG_SEARCH = "VegSearch";

    final public static String[] homeRecipeLabels = {
            "Breakfast",
            "Soup",
            "Barbecue",
            "Juice",
            "Sandwich",
            "Bread",
            "Rice",
            "Salad",
            "Pasta",
            "Pizza",
            "Stew",
            "Burger",
            "Smoothie",
            "Cookies",
            "Pie",
            "Cake"
    };

    final public static Integer[] homeRecipeImages = new Integer[]{
            R.drawable.breakfast,
            R.drawable.soup,
            R.drawable.barbecue,
            R.drawable.juice,
            R.drawable.sandwich,
            R.drawable.bread,
            R.drawable.rice,
            R.drawable.salad,
            R.drawable.pasta,
            R.drawable.pizza,
            R.drawable.stew,
            R.drawable.burger,
            R.drawable.smoothie,
            R.drawable.cookies,
            R.drawable.pie,
            R.drawable.cake
    };

    public static final String[] FAVOURITE_RECIPE_COLUMNS = {
            RecipesContract.FavoriteRecipes._ID,
            RecipesContract.FavoriteRecipes.COLUMN_USER_EMAIL,
            RecipesContract.FavoriteRecipes.COLUMN_RECIPE_ID,
            RecipesContract.FavoriteRecipes.COLUMN_RECIPE_NAME,
            RecipesContract.FavoriteRecipes.COLUMN_RECIPE_PHOTO_LINK,
            RecipesContract.FavoriteRecipes.COLUMN_TOTAL_INGREDIENTS,
            RecipesContract.FavoriteRecipes.COLUMN_CALORIES_PER_SERVING,
            RecipesContract.FavoriteRecipes.COLUMN_TOTAL_SERVING,
            RecipesContract.FavoriteRecipes.COLUMN_INGREDIENTS,
            RecipesContract.FavoriteRecipes.COLUMN_HEALTH_LABELS
    };

    void RecipeLabels() {

    }
}
