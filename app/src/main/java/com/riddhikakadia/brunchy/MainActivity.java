package com.riddhikakadia.brunchy;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener,
        CategoriesFragment.OnFragmentInteractionListener {

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 111;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    View navigationHeader;
    ImageView user_account_photo;
    TextView user_account_name;
    TextView user_account_email;
    View mainContentView;
    FrameLayout fragmentContainer;

    HomeFragment homeFragment;
    CategoriesFragment categoriesFragment;

    String mUsername;
    String mEmail;
    Uri mPhotoUrl;
    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        mUsername = ANONYMOUS;
        mEmail = "";
        mPhotoUrl = null;

        mainContentView = findViewById(R.id.content_main);
        fragmentContainer = (FrameLayout) mainContentView.findViewById(R.id.fragment_container);
        homeFragment = new HomeFragment();
        categoriesFragment = new CategoriesFragment();

        if (fragmentContainer != null) {
            Log.d(LOG_TAG, "RK view found");
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer.getId(), homeFragment).commit();
        } else {
            Log.d(LOG_TAG, "RK view not found");
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                String fbUser = getString(R.string.new_user);
                String fbEmail = "";
                Uri fbUserPhoto = null;
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    //User logged in
                    Log.d(LOG_TAG, "RK Firebase User: " + mFirebaseAuth.getCurrentUser().toString());
                    Log.d(LOG_TAG, "RK firebase User id " + firebaseUser.getUid().toString());

                    Toast.makeText(getApplicationContext(), getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show();
                    //Update drawer header with signed in user
                    if (firebaseUser.getDisplayName() != null) {
                        fbUser = firebaseUser.getDisplayName().toString();
                    }
                    if (firebaseUser.getEmail() != null) {
                        fbEmail = firebaseUser.getEmail().toString();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.d(LOG_TAG, "RK requestCode RC_SIGN_IN");
            if (resultCode == RESULT_OK) {
                Log.d(LOG_TAG, "RK resultCode RESULT_OK sign in");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(LOG_TAG, "RK resultCode RESULT_CANCELED sign in");
                Toast.makeText(this, "Sign In Canceled!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                showSnackbar();
                return;
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
        if (id == R.id.action_settings) {
            return true;
        }

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
        } else if (id == R.id.nav_categories) {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer.getId(), categoriesFragment).commit();
        } else if (id == R.id.nav_snap_n_cook) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_logout) {
            //User sign out
            AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
            logoutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    signout();
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            logoutDialog.setTitle("Are you sure you want to logout?");
            logoutDialog.show();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedInInitialize(Uri photoUrl, String username, String useremail) {
        mPhotoUrl = photoUrl;
        mUsername = username;
        mEmail = useremail;

        Log.d(LOG_TAG, "RK onSignedInInitialize() mUsername " + mUsername + " mEmail " + mEmail + " mURL " + mPhotoUrl);

        //Update navigation drawer header
        if (mPhotoUrl != null) {
            Picasso.with(getApplicationContext())
                    .load(mPhotoUrl)
                    .resize(150, 150)
                    .centerCrop()
                    .into(user_account_photo);
        }
        user_account_name.setText(mUsername);
        user_account_email.setText(mEmail);
    }

    private void onSignedOutCleanup() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void showSnackbar() {
        if (fragmentContainer != null) {
            Snackbar.make(fragmentContainer, "No Internet connection...", Snackbar.LENGTH_LONG).show();
        }
    }

    public void signout() {
        AuthUI.getInstance().signOut(this);
    }
}
