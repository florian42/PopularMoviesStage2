/*
* Copyright (C) 2017 Aumeier Florian
*/

package de.flo_aumeier.popularmoviesstage2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

import de.flo_aumeier.popularmoviesstage2.model.Movie;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides methods for querying the themoviedb API.
 */
public class NetworkUtils {
    private static String TAG = NetworkUtils.class.getSimpleName();

    private static final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY = "66c86590f283f43a95c4fff54da023dc";

    private static final String PARAM_POPULAR_MOVIES = "popular";
    private static final String PARAM_BEST_RATED_MOVIES = "top_rated";
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_LANGUAGE = "language";
    private static final String LANGUAGE = "en-US";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_IMAGES = "images";

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
