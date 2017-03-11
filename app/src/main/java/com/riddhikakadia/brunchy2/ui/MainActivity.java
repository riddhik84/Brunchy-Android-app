package com.riddhikakadia.brunchy2.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.riddhikakadia.brunchy2.BuildConfig;
import com.riddhikakadia.brunchy2.R;
import com.riddhikakadia.brunchy2.fragments.HomeFragment;
import com.riddhikakadia.brunchy2.util.Global;
import com.riddhikakadia.brunchy2.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static com.riddhikakadia.brunchy2.util.Constants.ACTION_DATA_UPDATED;
import static com.riddhikakadia.brunchy2.util.Constants.RECIPE_SETTINGS;
import static com.riddhikakadia.brunchy2.util.Constants.RECIPE_TO_SEARCH;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener {

    public static final int RC_SIGN_IN = 111;

    public static SharedPreferences sharedPreferences;

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final int IMAGE_WIDTH = 170;
    private final int IMAGE_HEIGHT = 170;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseUser firebaseUser;

    View navigationHeader;
    ImageView user_account_photo;
    TextView user_account_name;
    TextView user_account_email;
    SearchView recipe_search_view;
    View mainContentView;
    FrameLayout fragmentContainer;

    HomeFragment homeFragment;

    String mUsername;
    String mEmail;
    Uri mPhotoUrl;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recipe_search_view = (SearchView) findViewById(R.id.recipe_search_view);
        recipe_search_view.setLayoutParams(new Toolbar.LayoutParams(Gravity.END));  //Move search icon to right

        sharedPreferences = getSharedPreferences(RECIPE_SETTINGS, Context.MODE_PRIVATE);

        //Interstitial ad
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID);
        requestNewInterstitial();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setContentDescription(getString(R.string.snap_n_cook_fab_button_content_desc));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();

                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                Intent snap_n_cook = new Intent(getApplicationContext(), SnapNCookActivity.class);
                                startActivity(snap_n_cook);
                            }
                        });
                        requestNewInterstitial();

                    } else {
                        Intent snap_n_cook = new Intent(getApplicationContext(), SnapNCookActivity.class);
                        startActivity(snap_n_cook);
                    }
                } else {
                    Utility.showNoInternetToast(getApplicationContext());
                }
            }
        });

        MobileAds.initialize(getApplicationContext(), BuildConfig.ADMOB_AD_BANER_APP_ID);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationHeader = navigationView.getHeaderView(0);
        user_account_photo = (ImageView) navigationHeader.findViewById(R.id.user_account_photo);
        user_account_name = (TextView) navigationHeader.findViewById(R.id.user_account_name);
        user_account_email = (TextView) navigationHeader.findViewById(R.id.user_account_email);

        mUsername = "";
        mEmail = "";
        mPhotoUrl = null;

        mainContentView = findViewById(R.id.content_main);
        fragmentContainer = (FrameLayout) mainContentView.findViewById(R.id.fragment_container);
        homeFragment = new HomeFragment();

        if (fragmentContainer != null) {
            //Log.d(LOG_TAG, "*** fragmentContainer found");
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer.getId(), homeFragment).commit();
        } else {
            //Log.d(LOG_TAG, "*** fragmentContainer not found");
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                String fbUser = getString(R.string.new_user);
                String fbEmail = "";
                Uri fbUserPhoto = null;

                firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    //User logged in
                    //Log.d(LOG_TAG, "*** Firebase User: " + mFirebaseAuth.getCurrentUser().toString());
                    //Log.d(LOG_TAG, "*** Firebase User id " + firebaseUser.getUid().toString());

                    //Toast.makeText(getApplicationContext(), getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show();

                    Global.getCurrentUserID = firebaseUser.getUid().toString();
                    //Update drawer header with signed in user
                    if (firebaseUser.getDisplayName() != null) {
                        fbUser = firebaseUser.getDisplayName().toString();
                        Global.currentUser = fbUser;
                    }
                    if (firebaseUser.getEmail() != null) {
                        fbEmail = firebaseUser.getEmail().toString();
                        Global.currentUserEmail = fbEmail;
                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        fbUserPhoto = firebaseUser.getPhotoUrl();
                    }
                    onSignedInInitialize(fbUserPhoto, fbUser, fbEmail);

                } else {
                    //User not logged in - go to login screen
                    onSignedOutCleanup();

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //.setLogo(R.drawable.chef128)
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.LoginTheme)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        recipe_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    if (query.length() > 1) {
                        Intent intent = new Intent(MainActivity.this, RecipesListActivity.class);
                        intent.putExtra(RECIPE_TO_SEARCH, query);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utility.showNoInternetToast(getApplicationContext());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //For firebase notification
        if (getIntent().getExtras() != null) {
            String searchString = "";
            if (getIntent().getExtras().get("search") != null) {
                searchString = getIntent().getExtras().get("search").toString();
                //Log.d(LOG_TAG, "*** notification searchString: " + searchString);

                Intent intent = new Intent(MainActivity.this, RecipesListActivity.class);
                intent.putExtra(RECIPE_TO_SEARCH, searchString);
                startActivity(intent);
            }
        }

        //Show admob baner ad
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            //Log.d(LOG_TAG, "*** requestCode RC_SIGN_IN");
            if (resultCode == RESULT_OK) {
                //Log.d(LOG_TAG, "*** resultCode RESULT_OK sign in");
                updateWidget();
            } else if (resultCode == RESULT_CANCELED) {
                //Log.d(LOG_TAG, "*** resultCode RESULT_CANCELED sign in");
                Toast.makeText(this, getResources().getString(R.string.sign_in_canceled_message), Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                //Log.d(LOG_TAG, "*** resultCode RESULT_NO_NETWORK sign in");
                Utility.showNoInternetToast(this);
            } else {
                //Log.d(LOG_TAG, "*** resultCode error sign in");
                Utility.showToast(this, getResources().getString(R.string.login_error_message)); //login error
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (fragmentContainer != null)
                getSupportFragmentManager().beginTransaction().replace(fragmentContainer.getId(), homeFragment).commit();
        } else if (id == R.id.nav_snap_n_cook) {
            if (Utility.isNetworkConnected(this)) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            requestNewInterstitial();
                            Intent snap_n_cook = new Intent(getApplicationContext(), SnapNCookActivity.class);
                            startActivity(snap_n_cook);
                        }
                    });
                } else {
                    Intent snap_n_cook = new Intent(getApplicationContext(), SnapNCookActivity.class);
                    startActivity(snap_n_cook);
                }
            } else {
                Utility.showNoInternetToast(this);
            }

        } else if (id == R.id.nav_favorites) {
            Intent favorite_recipes = new Intent(this, FavoriteRecipesActivity.class);
            startActivity(favorite_recipes);
        } else if (id == R.id.nav_logout) {
            //User sign out
            if (Utility.isNetworkConnected(this)) {
                if (mFirebaseAuth.getCurrentUser() == null) {
                    //Utility.showToast(this, getResources().getString(R.string.no_user_logged_in_message));
                    AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
                    loginDialog.setPositiveButton(getResources().getString(R.string.login_button_text),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //User not logged in - go to login screen
                                    startActivityForResult(
                                            AuthUI.getInstance()
                                                    .createSignInIntentBuilder()
                                                    //.setLogo(R.drawable.chef128)
                                                    .setIsSmartLockEnabled(false)
                                                    .setTheme(R.style.LoginTheme)
                                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                                    .build(),
                                            RC_SIGN_IN);
                                }
                            }).create();
                    loginDialog.setCancelable(false);
                    loginDialog.setTitle(getResources().getString(R.string.no_user_logged_in_message));
                    loginDialog.show();
                } else {
                    AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
                    logoutDialog.setPositiveButton(getResources().getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logout();
                            onSignedOutCleanup();
                            updateWidget();
                        }
                    })
                            .setNegativeButton(getResources().getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Do nothing - auto dismiss
                                }
                            }).create();
                    logoutDialog.setTitle(getResources().getString(R.string.logout_confirmation_msg));
                    logoutDialog.show();
                }
            } else {
                Utility.showNoInternetToast(this);
            }
//        } else if (id == R.id.nav_settings) {
            //settings
        } else if (id == R.id.nav_about_us) {
            Intent aboutUsIntent = new Intent(this, AboutUsActivity.class);
            startActivity(aboutUsIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.isNetworkConnected(this)) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);

            //Get device token
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            //Log.d(LOG_TAG, "*** firebase device token: " + refreshedToken);
        } else {
            Utility.showNoInternetToast(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Utility.isNetworkConnected(this)) {
            if (mAuthStateListener != null) {
                mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            }
        }
    }

    private void onSignedInInitialize(Uri photoUrl, String username, String useremail) {
        mPhotoUrl = photoUrl;
        mUsername = username;
        mEmail = useremail;

        //Log.d(LOG_TAG, "*** firebase onSignedInInitialize() mUsername " + mUsername + " mEmail " + mEmail + " mURL " + mPhotoUrl);

        //Update navigation drawer header
        if (mPhotoUrl != null) {
            Picasso.with(getApplicationContext())
                    .load(mPhotoUrl)
                    .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                    .centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.chef3))
                    .error(getResources().getDrawable(R.drawable.chef3))
                    .into(user_account_photo);
        }
        user_account_name.setText(mUsername);
        user_account_email.setText(mEmail);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void logout() {
        AuthUI.getInstance().signOut(this);
    }

    private void onSignedOutCleanup() {
        Global.currentUserEmail = "";
        Global.currentUser = "";
        Global.getCurrentUserID = "";
    }

    public void updateWidget() {
        //Log.d(LOG_TAG, "*** updateWidget()");
        Intent intent = new Intent(ACTION_DATA_UPDATED).setPackage(this.getPackageName());
        sendBroadcast(intent);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
