package com.riddhikakadia.brunchy;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    final String LOG_TAG = RecyclerAdapter.class.getSimpleName();

    int lastPosition = -1;

    //Context context;
    LayoutInflater inflater;
    List<String> recipe_names;
    List<String> recipe_URLs;

    public RecyclerAdapter(List<String> recipes, List<String> recipeURLs) {
        Log.d(LOG_TAG, "RecyclerAdapter() ");

        //this.context = context;
        recipe_names = recipes;
        recipe_URLs = recipeURLs;
        //inflater = LayoutInflater.from(context);
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
        Log.d(LOG_TAG, "Recipe Name: " + recipe_names.get(position) + " Recipe URL: " + recipe_URLs.get(position));

        holder.recipe_name.setText(recipe_names.get(position));
        Picasso.with(inflater.getContext())
                .load(recipe_URLs.get(position))
                .noFade()
                .resize(500, 600)
                .centerCrop()
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
            //Intent intent = new Intent(context, MovieDetailActivity.class);
            //intent.putExtra("MovieImage", movie_images[position]);
            //context.startActivity(intent);

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
