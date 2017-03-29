package de.flo_aumeier.popularmoviesstage2.model.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract holding information about the underlying db.
 */

public class FavouriteMovieContract {
    public static final String CONTENT_SCHEME = "content://";
    public static final String AUTHORITY = "de.flo_aumeier.popularmoviesstage2";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + AUTHORITY);
    public static final String PATH_FAVOURITE_MOVIES = "movies";


    private FavouriteMovieContract() {
    }

    public static final class FavouriteMovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

    }
}
