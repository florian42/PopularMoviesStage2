/*
* Copyright (C) 2017 Aumeier Florian
*/

package de.flo_aumeier.popularmoviesstage2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Provides methods for querying the themoviedb API.
 */
public class NetworkUtils {
    private static String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Checks if the user has currently a connection to the internet.
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
