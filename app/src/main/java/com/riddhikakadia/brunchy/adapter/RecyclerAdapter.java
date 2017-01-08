package com.riddhikakadia.brunchy.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.ui.RecipeDetailActivity;
import com.riddhikakadia.brunchy.ui.RecipesListActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    final String LOG_TAG = RecyclerAdapter.class.getSimpleName();
    final String RECIPE_ID = "RECIPE_ID";

    int lastPosition = -1;

    //Context context;
    LayoutInflater inflater;
    List<String> recipe_names;
    List<String> recipe_image_URLs;
    List<String> recipe_URIs;

    public RecyclerAdapter(List<String> recipeNames, List<String> recipeImageURLs, List<String> recipeURIs) {
        Log.d(LOG_TAG, "RecyclerAdapter() ");

        //this.context = context;
        recipe_names = recipeNames;
        recipe_image_URLs = recipeImageURLs;
        recipe_URIs = recipeURIs;
        //inflater = LayoutInflater.from(context);
        Log.d(LOG_TAG, "RK recipe_names.size() " + recipe_names.size());
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v = inflater.inflate(R.layout.recipe_list_item, parent, false);
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recipe_list_item, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Log.d(LOG_TAG, "Recipe Name: " + recipe_names.get(position) + " Recipe Image URLs : " + recipe_image_URLs.get(position)
                + " Recipe ID: " + recipe_URIs.get(position));

        int imageHeight = 0;
        if (position % 3 == 0) {
            imageHeight = 600;
        } else {
            imageHeight = 700;
        }

        holder.recipe_name.setText(recipe_names.get(position));
        Picasso.with(inflater.getContext())
                .load(recipe_image_URLs.get(position))
                .noFade()
                .resize(500, imageHeight)
                .placeholder(R.drawable.placeholder_food)
                .centerCrop()
                //.fit()
                .into(holder.recipe_photo);

        holder.recipe_photo.setOnClickListener(clickListener);
        holder.recipe_photo.setTag(holder);

        // Here you apply the animation when the view is bound
        setAnimation(holder.cardview, position);
    }

    @Override
    public int getItemCount() {
        return recipe_names.size();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewHolder vh = (RecyclerViewHolder) v.getTag();
            int position = vh.getPosition();

            Toast.makeText(inflater.getContext(), "This is position " + position, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(inflater.getContext(), RecipeDetailActivity.class);
            intent.putExtra(RECIPE_ID, recipe_URIs.get(position));
            Log.d(LOG_TAG, "RK*** Recipe uri: " + recipe_URIs.get(position));
            inflater.getContext().startActivity(intent);
        }
    };

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(inflater.getContext(), android.R.anim.slide_in_left);
            animation.setDuration(1000);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
