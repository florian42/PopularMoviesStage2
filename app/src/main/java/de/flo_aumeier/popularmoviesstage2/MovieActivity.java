package de.flo_aumeier.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.flo_aumeier.popularmoviesstage2.model.Movie;
import de.flo_aumeier.popularmoviesstage2.model.ResultsTrailer;
import de.flo_aumeier.popularmoviesstage2.model.Review;
import de.flo_aumeier.popularmoviesstage2.model.ReviewResults;
import de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface;
import de.flo_aumeier.popularmoviesstage2.model.Trailer;
import de.flo_aumeier.popularmoviesstage2.model.db.FavouriteMovieContract;
import de.flo_aumeier.popularmoviesstage2.model.error.ErrorMessages;
import de.flo_aumeier.popularmoviesstage2.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Society on 04.03.2017.
 */

/*
* Displays detailed information for a specific movie.
*/
public class MovieActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener, ReviewAdapter.ListItemClickListener {
    private static final String TAG = MovieActivity.class.getSimpleName();

    private ContentResolver mMoviesContentProvider;
    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerView mRecyclerViewReviews;
    private List<Review> mReviews;
    private RecyclerView mRecyclerViewTrailer;
    private List<Trailer> mTrailers;
    private List<String> mTrailerUrls;
    private Movie mMovie;
    private AppBarLayout mAppBarLayout;
    private Context mContext;
    private MovieActivity mActivity;
    private CoordinatorLayout mCLayout;
    private FloatingActionButton mFavouriteButton;

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
        mActivity = this;
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mMoviesContentProvider = getContentResolver();
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mMovie = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_MOVIE);
        if (null == mMovie) {
            throw new NullPointerException("Parcelable Extra is null");
        }

        getXMLReferences();
        setupActionBar();
        final String baseURL = "https://image.tmdb.org/t/p/w185/";
        final String completeURLtoBackdrop = baseURL + mMovie.getBackdropPath();

        mPlot.setText(mMovie.getOverview());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRating.setText(String.valueOf(mMovie.getVoteAverage()));
        final String completeURLToPoster = baseURL + mMovie.getPosterPath();
        Picasso.with(mContext)
                .load(completeURLToPoster)
                .into(mMoviePoster);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReviews.setHasFixedSize(true);
        mRecyclerViewReviews.setLayoutManager(verticalLayoutManager);
        mRecyclerViewTrailer = (RecyclerView) findViewById(R.id.rv_toolbar_movie_thumbnails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewTrailer.setLayoutManager(layoutManager);
        mRecyclerViewTrailer.setHasFixedSize(true);
        if (NetworkUtils.isOnline(this)) {
            fetchReviews();
            fetchTrailerThumbnailURLs();
        } else {
            showNetworkError();
        }

        //db
        setFavouriteButtonBackground();
    }

    private void setFavouriteButtonBackground() {
        if (isFavouriteMovie()) {
            mFavouriteButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favorite_white_24dp, null));
        } else {
            mFavouriteButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favorite_border_white_24dp, null));
        }
    }

    public boolean isFavouriteMovie() {

        final Cursor cursor = mMoviesContentProvider.query(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, null, null, null, null);
        int isFavouriteMovie = 0;
        try {
            while (cursor.moveToNext()) {
                int movieIdColumnIndex = cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID);
                int movieId = cursor.getInt(movieIdColumnIndex);
                if (movieId == mMovie.getId()) {
                    int isFavouriteMovieColumnIndex = cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_IS_FAVOURITE);
                    isFavouriteMovie = cursor.getInt(isFavouriteMovieColumnIndex);
                }
            }

        } finally {
            cursor.close();
        }
        boolean isFavourite = false;
        if (isFavouriteMovie == 1) {
            isFavourite = true;
        }
        return isFavourite;
    }

    public void addToFavouriteMovies(View view) {
        if (isFavouriteMovie()) {
            removeFromFavouriteMovies();
        } else {
            addNewFavouriteMovie(mMovie.getTitle(), mMovie.getId());
        }
        setFavouriteButtonBackground();
    }

    private void removeFromFavouriteMovies() {
        // mDb.delete(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + "=" + mMovie.getId(), null);
        final Uri uri = FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI.buildUpon().appendPath(mMovie.getId().toString()).build();
        mMoviesContentProvider.delete(uri, null, null);
    }

    private void addNewFavouriteMovie(String title, int movieId) {

        ContentValues cv = new ContentValues();
        cv.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID, movieId);
        cv.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE, title);
        cv.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_IS_FAVOURITE, true);
        mMoviesContentProvider.insert(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, cv);
    }

    private void fetchReviews() {
        Log.d(TAG, "Fetching reviews");
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(TmdbApiEndpointInterface.BASE_URL)
                .addConverterFactory
                        (GsonConverterFactory.create(gson))
                .build();
        TmdbApiEndpointInterface endpointInterface = retrofit.create(TmdbApiEndpointInterface.class);
        Call<ReviewResults> call = endpointInterface.getReviewsForMovie(String.valueOf(mMovie.getId()));
        call.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                if (response.isSuccessful()) {
                    ReviewResults reviewResults = response.body();
                    mReviews = reviewResults.getResults();
                    ReviewAdapter reviewAdapter = new ReviewAdapter(mActivity, mReviews);
                    mRecyclerViewReviews.setAdapter(reviewAdapter);
                } else {
                    Log.d(TAG, "Response was not Successfull :(, CODE: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void setupActionBar() {
        // Set the support action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_movie_detail);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mMovie.getTitle());
        }
    }

    private void getXMLReferences() {
        TextView voteCount = (TextView) findViewById(R.id.tv_vote_count);
        voteCount.setText(String.valueOf(mMovie.getVoteCount()));
        // Get the widget reference from XML layout
        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
                R.id.collapsing_toolbar_layout_movie_details);
        mMovieBackdrop = (ImageView) findViewById(R.id.iv_movie_poster);
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster_toolbar);
        mPlot = (TextView) findViewById(R.id.tv_plot);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mFavouriteButton = (FloatingActionButton) findViewById(R.id.fav);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(this, "Clicked: #" + clickedItemIndex, Toast.LENGTH_SHORT).show();
        final String baseUrl = "https://www.youtube.com/watch?v=";
        final String completeUrl = baseUrl + mTrailerUrls.get(clickedItemIndex);
        Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl));
        startActivity(playTrailerIntent);

    }

    private void fetchTrailerThumbnailURLs() {
        Log.d(TAG, "Fetching Thumbnails for the trailers");
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(TmdbApiEndpointInterface.BASE_URL)
                .addConverterFactory
                        (GsonConverterFactory.create(gson))
                .build();
        TmdbApiEndpointInterface endpointInterface = retrofit.create(TmdbApiEndpointInterface.class);
        Call<ResultsTrailer> call = endpointInterface.getTrailersForMovie(String.valueOf(mMovie.getId()));
        call.enqueue(new Callback<ResultsTrailer>() {
            @Override
            public void onResponse(Call<ResultsTrailer> call, Response<ResultsTrailer> response) {
                if (response.isSuccessful()) {
                    ResultsTrailer resultsTrailer = response.body();
                    mTrailers = resultsTrailer.getResults();
                    mTrailerUrls = getUrlsOfTrailers();
                    TrailerAdapter adapter = new TrailerAdapter(mTrailerUrls, mActivity);
                    mRecyclerViewTrailer.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Response was not Successfull :(, CODE: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResultsTrailer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private List<String> getUrlsOfTrailers() {
        List<String> listOfTrailersUrls = new ArrayList<>();
        for (Trailer trailer : mTrailers) {
            listOfTrailersUrls.add(trailer.getKey());
        }
        return listOfTrailersUrls;
    }

    @Override
    public void onReviewAdapterListItemClick(int clickedItemIndex) {
        Toast.makeText(this, "Clicked: #" + clickedItemIndex, Toast.LENGTH_SHORT).show();
    }

    private void showNetworkError() {
        mErrorSnackBar = Snackbar
                .make(mCoordinatorLayout, ErrorMessages.NO_NETWORK_CONNECTION.getMessage(), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchReviews();
                        fetchTrailerThumbnailURLs();
                    }
                });
        mErrorSnackBar.show();
    }
}
