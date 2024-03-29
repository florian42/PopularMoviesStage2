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
 * Provides access to the contents of the underlying database where favourite movies are stored.
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
        final SQLiteDatabase db = mFavouriteDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case MOVIES:
                retCursor = db.query(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
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
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavouriteDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedMovieEntrys = 0;
        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                deletedMovieEntrys = db.delete(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, "movieId=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        if (deletedMovieEntrys != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedMovieEntrys;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int moviesUpdated;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesUpdated = mFavouriteDbHelper.getWritableDatabase().update(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (moviesUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return moviesUpdated;
    }
}
