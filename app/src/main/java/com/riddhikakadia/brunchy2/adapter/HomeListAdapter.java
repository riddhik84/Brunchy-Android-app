package com.riddhikakadia.brunchy2.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.riddhikakadia.brunchy2.R;
import com.squareup.picasso.Picasso;

/**
 * Created by RKs on 12/18/2016.
 */

public class HomeListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] recipeList;
    private Integer[] recipeImages;

    private final int IMAGE_WIDTH = 1200;
    private final int IMAGE_HEIGHT = 800;

    public HomeListAdapter(Activity context, String[] recipeList, Integer[] recipeImages) {
        super(context, R.layout.home_list_item, recipeList);
        this.context = context;
        this.recipeList = recipeList;
        this.recipeImages = recipeImages;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.home_list_item, null, true);

        ImageView recipeCategoryImage = (ImageView) rowView.findViewById(R.id.home_list_item_image);
        TextView recipeCategory = (TextView) rowView.findViewById(R.id.home_list_item_text);

        Picasso.with(context)
                .load(recipeImages[position])
                .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                //.resize(1200, 0)
                //.noFade()
                //.skipMemoryCache()
                //.placeholder(R.drawable.placeholder)
                .placeholder(recipeImages[position])
                .into(recipeCategoryImage);
        recipeCategory.setContentDescription(recipeList[position]);

        recipeCategory.setText(recipeList[position]);
        return rowView;
    }
}
