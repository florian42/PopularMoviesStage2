package de.flo_aumeier.popularmoviesstage2.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.flo_aumeier.popularmoviesstage2.model.db.FavouriteMovieContract;
import de.flo_aumeier.popularmoviesstage2.model.db.FavouriteMovieDbHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by faumeier on 23.03.2017.
 */
@RunWith(AndroidJUnit4.class)
public class FavouriteMoviesContentProviderTest {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() throws Exception {
        FavouriteMovieDbHelper dbHelper = new FavouriteMovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, null, null);
    }

    @Test
    public void testInsert() throws Exception {
        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE, "Test Movie");
        testMovieValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_IS_FAVOURITE, true);
        testMovieValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID, 23456);

        ContentResolver contentResolver = mContext.getContentResolver();

        Uri uri = contentResolver.insert(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, testMovieValues);

        Uri expectedUri = ContentUris.withAppendedId(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, 2); //FIXME: Why is it 2?

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);
    }

    @Test
    public void testDelete() throws Exception {
        FavouriteMovieDbHelper dbHelper = new FavouriteMovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE, "Test Movie");
        testMovieValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_IS_FAVOURITE, true);
        testMovieValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID, 23456);

        long movieRowId = database.insert(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, null, testMovieValues);
        database.close();

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        Uri uriToDelete = FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI.buildUpon().appendPath("1").build();
        ContentResolver contentResolver = mContext.getContentResolver();
        int moviesDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, moviesDeleted != 0);
    }
}