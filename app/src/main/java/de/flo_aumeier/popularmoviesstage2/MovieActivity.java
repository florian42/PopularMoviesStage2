package de.flo_aumeier.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.flo_aumeier.popularmoviesstage2.model.Movie;

/**
 * Created by Society on 04.03.2017.
 */

/*
* Displays detailed information for a specific movie.
*/
public class MovieActivity extends AppCompatActivity {
    private static final String TAG = MovieActivity.class.getSimpleName();

    private Movie mMovie;

    private Context mContext;
    private Activity mActivity;
    private CoordinatorLayout mCLayout;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private ImageView mMovieBackdrop;
    private ImageView mMoviePoster;

    private TextView mPlot;
    private TextView mReleaseDate;
    private TextView mRating;
    /**
     * Displays an error if one is encountered
     */
    private Snackbar mErrorSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = MovieActivity.this;

        mMovie = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_MOVIE);
        if (null == mMovie) {
            throw new NullPointerException("ParcelableExtra is null");
        }

        getXMLReferences();
        setupActionBar();
        final String baseURL = "https://image.tmdb.org/t/p/w185/";
        final String completeURLtoBackdrop = baseURL + mMovie.getBackdropPath();
        Picasso.with(mContext)
                .load(completeURLtoBackdrop)
                .into(mMovieBackdrop);
        mPlot.setText(mMovie.getOverview());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRating.setText(String.valueOf(mMovie.getVoteAverage()));
        final String completeURLToPoster = baseURL + mMovie.getPosterPath();
        Picasso.with(mContext)
                .load(completeURLToPoster)
                .into(mMoviePoster);
    }

    private void setupActionBar() {
        // Set the support action bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getXMLReferences() {
        // Get the widget reference from XML layout
        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
                R.id.collapsing_toolbar_layout_movie_details);
        mMovieBackdrop = (ImageView) findViewById(R.id.iv_movie_poster);
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster_toolbar);
        mPlot = (TextView) findViewById(R.id.tv_plot);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
    }

}
