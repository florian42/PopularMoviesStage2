package de.flo_aumeier.popularmoviesstage2.model.db;

import android.provider.BaseColumns;

/**
 * Created by Flo on 19.03.2017.
 */

public class FavouriteMovieContract {
    private FavouriteMovieContract() {
    }

    public static final class FavouriteMovieEntry implements BaseColumns {
        // COMPLETED (2) Inside create a static final members for the table name and each of the db columns
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_IS_FAVOURITE = "isFavourite";
    }
}
