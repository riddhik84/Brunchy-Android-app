package com.riddhikakadia.brunchy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.riddhikakadia.brunchy.BuildConfig;
import com.riddhikakadia.brunchy.R;
import com.riddhikakadia.brunchy.util.Utility;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.riddhikakadia.brunchy.util.Constants.RECIPE_TO_SEARCH;

public class SnapNCookActivity extends AppCompatActivity {

    private static final String LOG_TAG = SnapNCookActivity.class.getSimpleName();

    private static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 200;

    private final ClarifaiClient clarifaiClient = new ClarifaiClient(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET);

    private static String searchTerm = "";

    private static ArrayList<String> itemsToFind = new ArrayList<>();
    private ArrayList<String> items = new ArrayList<>();

    private ListView listview;
    FloatingActionButton cameraButton;
    FloatingActionButton galleryButton;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_ncook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.snal_n_cook_header));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cameraButton = (FloatingActionButton) findViewById(R.id.fab_camera);
        galleryButton = (FloatingActionButton) findViewById(R.id.fab_gallery);
        listview = (ListView) findViewById(R.id.item_listView);
        mProgressBar = (ProgressBar) findViewById(R.id.snap_n_cook_progressbar);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST_CODE);
                }
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
            }
        });
    }

    private void addItemsList() {
        listview.setTextFilterEnabled(true);
        listview.setAdapter(new ArrayAdapter<>(this, R.layout.ingredient_list_item_checked, items));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                CheckedTextView v = (CheckedTextView) view;
                Object obj = listview.getItemAtPosition(index);
                String item = obj.toString();
                if (v.isChecked()) {
                    if (!itemsToFind.contains(item)) {
                        itemsToFind.add(item);
                    }
                } else {
                    if (itemsToFind.contains(item)) {
                        itemsToFind.remove(item);
                    }
                }
            }
        });
    }

    private void showRecipeBtn() {
        TextView chooseItemsText = (TextView) findViewById(R.id.chooseItemsText);
        chooseItemsText.setVisibility(TextView.VISIBLE);
        Button recipeButton = (Button) findViewById(R.id.recipeButton);
        recipeButton.setVisibility(Button.VISIBLE);
        recipeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(SnapNCookActivity.this, RecipesListActivity.class);
                //Log.d(LOG_TAG, "*** searchterm: " + searchTerm);
                i.putExtra(RECIPE_TO_SEARCH, getSearchTerm());

                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == CAPTURE_IMAGE_REQUEST_CODE || requestCode == GALLERY_IMAGE_REQUEST_CODE) && resultCode == RESULT_OK) {
            // image captured, saved or selected to uri
            try {
                Bitmap imageBitmap = null;

                if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                } else {
                    InputStream inStream = getContentResolver().openInputStream(data.getData());
                    imageBitmap = BitmapFactory.decodeStream(inStream);
                }

                ImageView preview = (ImageView) findViewById(R.id.imageView);
                preview.setImageBitmap(imageBitmap);

                // scale the input image to improve the performance
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 320,
                        320 * imageBitmap.getHeight() / imageBitmap.getWidth(), true);

                if (Utility.isNetworkConnected(this)) {
                    // Run recognition on a background thread.
                    //Log.d(LOG_TAG, "*** snap n cook network connected");

                    new AsyncTask<Bitmap, Void, RecognitionResult>() {
                        @Override
                        protected void onPreExecute() {
                            //Log.d(LOG_TAG, "*** In snap n cook onPreExecute");
                        }

                        @Override
                        protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
                            byte[] byteArray = stream.toByteArray();
                            return clarifaiClient.recognize(new RecognitionRequest(byteArray).setModel("food-items-v0.1")).get(0);
                        }

                        @Override
                        protected void onPostExecute(RecognitionResult result) {
                            clearScreen();
                            for (Tag tag : result.getTags()) {
                                items.add(tag.getName());
                                //+ " " + tag.getProbability() + " "
                            }
                            addItemsList();
                            showRecipeBtn();
                        }
                    }.execute(imageBitmap);
                } else {
                    Utility.showNoInternetToast(this);
                }

            } catch (FileNotFoundException e) {
                //Log.e(LOG_TAG, e.getMessage());
            }
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture or selection.
            //Log.e(LOG_TAG, "*** capture canceled");

        } else {
            // capture failed or did not find file.
            //Log.e(LOG_TAG, "*** capture failed");
        }
    }

    public static String getSearchTerm() {
        for (String str : itemsToFind) {
            if (searchTerm.equals("")) {
                searchTerm += str;
            } else
                searchTerm += " " + str;
        }
        return searchTerm;
    }


    public static void setSearchTerm(String term) {
        searchTerm = term;
    }

    private void clearScreen() {
        itemsToFind = new ArrayList<>();
        items = new ArrayList<>();
        searchTerm = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchTerm = "";
    }

}
