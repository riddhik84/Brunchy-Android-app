package com.riddhikakadia.brunchy.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.riddhikakadia.brunchy.R;

/**
 * Created by rkakadia on 6/22/2016.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    ImageView recipe_photo;
    TextView recipe_name;
    CardView cardview;

    public RecyclerViewHolder(View itemView) {
        super(itemView);

        cardview = (CardView) itemView.findViewById(R.id.card_view);
        recipe_photo = (ImageView) itemView.findViewById(R.id.recipe_photo);
        recipe_name = (TextView) itemView.findViewById(R.id.recipe_name);
    }
}
