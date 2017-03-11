package com.riddhikakadia.brunchy2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.riddhikakadia.brunchy2.R;

/**
 * Created by RKs on 12/25/2016.
 */

public class Utility {

    public static boolean isNetworkConnected(Context context) {
        //Check internet connection
        boolean isConnected = false;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static void showNoInternetToast(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.connect_to_internet), Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
