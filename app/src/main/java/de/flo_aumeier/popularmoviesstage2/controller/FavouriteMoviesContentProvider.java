package de.flo_aumeier.popularmoviesstage2.controller;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.flo_aumeier.popularmoviesstage2.model.db.FavouriteMovieContract;
import de.flo_aumeier.popularmoviesstage2.model.db.FavouriteMovieDbHelper;

/**
 * Created by faumeier on 20.03.2017.
 */

public class FavouriteMoviesContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavouriteMovieDbHelper mFavouriteDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY, FavouriteMovieContract.PATH_FAVOURITE_MOVIES, MOVIES);
        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY, FavouriteMovieContract.PATH_FAVOURITE_MOVIES + "/#", MOVIES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavouriteDbHelper = new FavouriteMovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavouriteDbHelper.getWritableDatabase();
        Uri returnUri;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                long id = db.insert(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
