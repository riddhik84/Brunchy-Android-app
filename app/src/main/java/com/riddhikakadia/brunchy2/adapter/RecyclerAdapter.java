package com.riddhikakadia.brunchy2.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.riddhikakadia.brunchy2.R;
import com.riddhikakadia.brunchy2.ui.RecipeDetailActivity;
import com.riddhikakadia.brunchy2.util.Utility;
import com.riddhikakadia.brunchy2.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final String LOG_TAG = RecyclerAdapter.class.getSimpleName();

    private final int IMAGE_WIDTH = 500;

    int lastPosition = -1;

    //Context context;
    LayoutInflater inflater;
    List<String> recipe_names;
    List<String> recipe_image_URLs;
    List<String> recipe_URIs;

    public RecyclerAdapter(List<String> recipeNames, List<String> recipeImageURLs, List<String> recipeURIs) {
        //Log.d(LOG_TAG, "RecyclerAdapter() ");

        //this.context = context;
        recipe_names = recipeNames;
        recipe_image_URLs = recipeImageURLs;
        recipe_URIs = recipeURIs;
        //inflater = LayoutInflater.from(context);
        //Log.d(LOG_TAG, "*** recipe_names.size() " + recipe_names.size());
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
        //Log.d(LOG_TAG, "Recipe Name: " + recipe_names.get(position) + " Recipe Image URLs : " + recipe_image_URLs.get(position)
        //        + " Recipe ID: " + recipe_URIs.get(position));

        int imageHeight = 0;
        if (position % 3 == 0) {
            imageHeight = 600;
        } else {
            imageHeight = 700;
        }

        holder.recipe_name.setText(recipe_names.get(position));

        String loadImageURL;
        if (recipe_image_URLs.get(position) == null || recipe_image_URLs.get(position).length() < 1) {
            loadImageURL = inflater.getContext().getResources().getDrawable(R.drawable.placeholder_food).toString();
        } else {
            loadImageURL = recipe_image_URLs.get(position);
        }
        Picasso.with(inflater.getContext())
                .load(loadImageURL)
                .noFade()
                .resize(IMAGE_WIDTH, imageHeight)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                //.fit()
                .into(holder.recipe_photo);
        holder.recipe_photo.setContentDescription(recipe_names.get(position));

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
            //Toast.makeText(inflater.getContext(), "This is position " + position, Toast.LENGTH_LONG).show();

            if (Utility.isNetworkConnected(inflater.getContext())) {
                Intent intent = new Intent(inflater.getContext(), RecipeDetailActivity.class);
                intent.putExtra(Constants.RECIPE_ID, recipe_URIs.get(position));
                intent.putExtra(Constants.RECIPE_LIST_POSITION, position);
                //Log.d(LOG_TAG, "RK*** Recipe uri: " + recipe_URIs.get(position));
                inflater.getContext().startActivity(intent);
            } else {
                Utility.showNoInternetToast(inflater.getContext());
            }
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
