package de.flo_aumeier.popularmoviesstage2.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Flo on 19.03.2017.
 */

public class FavouriteMovieDbHelper extends SQLiteOpenHelper {

    // COMPLETED (2) Create a static final String called DATABASE_NAME and set it to "waitlist.db"
    // The database name
    private static final String DATABASE_NAME = "favourite_movies.db";

    // COMPLETED (3) Create a static final int called DATABASE_VERSION and set it to 1
    // If you change the database schema, you must increment the database version
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
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_IS_FAVOURITE + " INTEGER NOT NULL" + "); ";
        db.execSQL(SQL_CREATE_FAVOURITEMOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
