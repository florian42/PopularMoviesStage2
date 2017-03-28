package de.flo_aumeier.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ProgressBar;
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
//TODO: In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite.
public class MovieActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener, ReviewAdapter.ListItemClickListener {
    private static final String TAG = MovieActivity.class.getSimpleName();
    //https://img.youtube.com/vi/<insert-youtube-video-id-here>/default.jpg

    private static final float THRESHOLD_PERCENTAGE = 0.2F;

    private ContentResolver mMoviesContentProvider;

    private RecyclerView mRecyclerViewReviews;
    private List<Review> mReviews;
    private RecyclerView mRecyclerViewTrailer;
    private List<Trailer> mTrailers;
    private List<String> mTrailerUrls;
    private Movie mMovie;
    private MovieActivity mActivity;
    private FloatingActionButton mFavouriteButton;


    private ImageView mMoviePoster;

    private TextView mPlot;
    private TextView mReleaseDate;
    private TextView mRating;
    private ProgressBar mLoadingIndicator;
    /**
     * Displays an error if one is encountered
     */
    private Snackbar mErrorSnackBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Get the application context
        Context context = getApplicationContext();
        mActivity = this;
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_trailer);
        mMoviesContentProvider = getContentResolver();
        mMovie = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_MOVIE);
        if (null == mMovie) {
            throw new NullPointerException("Parcelable Extra is null");
        }

        getXMLReferences();
        setupActionBar();
        final String baseURL = "https://image.tmdb.org/t/p/w185/";

        mPlot.setText(mMovie.getOverview());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRating.setText(String.valueOf(mMovie.getVoteAverage()));
        final String completeURLToPoster = baseURL + mMovie.getPosterPath();
        Picasso.with(context)
                .load(completeURLToPoster)
                .into(mMoviePoster);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReviews.setHasFixedSize(true);
        mRecyclerViewReviews.setLayoutManager(verticalLayoutManager);
        fetchReviews();
        mRecyclerViewTrailer = (RecyclerView) findViewById(R.id.rv_toolbar_movie_thumbnails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewTrailer.setLayoutManager(layoutManager);
        mRecyclerViewTrailer.setHasFixedSize(true);
        fetchTrailerThumbnailURLs();

        //db
        setFavouriteButtonBackground();
    }

    private void showLoadingIndicator(boolean show) {
        if (show) {
            mRecyclerViewTrailer.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mRecyclerViewTrailer.setVisibility(View.VISIBLE);
        }
    }

    private void setFavouriteButtonBackground() {
        if (isFavouriteMovie()) {
            mFavouriteButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favorite_white_24dp, null));
        } else {
            mFavouriteButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favorite_border_white_24dp, null));
        }
    }

    private boolean isFavouriteMovie() {

        final Cursor cursor = mMoviesContentProvider.query(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, null, null, null, null);
        int isFavouriteMovie = 0;
        try {
            assert cursor != null;
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
        final String selection = FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?";
        mMoviesContentProvider.delete(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, selection, null);
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
        showLoadingIndicator(true);
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
                showLoadingIndicator(false);
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
}
