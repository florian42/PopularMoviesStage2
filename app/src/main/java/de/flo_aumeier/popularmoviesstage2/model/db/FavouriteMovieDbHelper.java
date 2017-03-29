package de.flo_aumeier.popularmoviesstage2.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for access to the database.
 */

public class FavouriteMovieDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "favourite_movies.db";


    private static final int DATABASE_VERSION = 1;

    public FavouriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // COMPLETED (6) Inside, create an String query called SQL_CREATE_WAITLIST_TABLE that will create the table
        // Create a table to hold waitlist data
        final String SQL_CREATE_FAVOURITEMOVIES_TABLE = "CREATE TABLE " + FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME + " (" +
                FavouriteMovieContract.FavouriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL" + "); ";
        db.execSQL(SQL_CREATE_FAVOURITEMOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
