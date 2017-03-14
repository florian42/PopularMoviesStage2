package de.flo_aumeier.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface;
import de.flo_aumeier.popularmoviesstage2.model.Trailer;
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
//TODO: When a trailer is selected, app uses an Intent to launch the trailer.
//TODO: In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite.
//TODO (2): App requests for related videos for a selected movie via the /movie/{id}/videos endpoint
// in a background thread and displays those details when the user selects a movie.
//TODO (3): App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint
// in a background thread and displays those details when the user selects a movie.
public class MovieActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, TrailerAdapter.ListItemClickListener {
    private static final String TAG = MovieActivity.class.getSimpleName();
    //https://img.youtube.com/vi/<insert-youtube-video-id-here>/default.jpg

    private static final float THRESHOLD_PERCENTAGE = 0.2F;

    private RecyclerView mRecyclerView;
    private List<Trailer> mTrailers;
    private List<String> mTrailerUrls;
    private Movie mMovie;
    private AppBarLayout mAppBarLayout;
    private Context mContext;
    private MovieActivity mActivity;
    private CoordinatorLayout mCLayout;

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
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mMovie = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_MOVIE);
        if (null == mMovie) {
            throw new NullPointerException("ParcelableExtra is null");
        }

        getXMLReferences();
        setupActionBar();
        final String baseURL = "https://image.tmdb.org/t/p/w185/";
        final String completeURLtoBackdrop = baseURL + mMovie.getBackdropPath();
//        Picasso.with(mContext)
//                .load(completeURLtoBackdrop)
//                .into(mMovieBackdrop);
        mPlot.setText(mMovie.getOverview());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRating.setText(String.valueOf(mMovie.getVoteAverage()));
        final String completeURLToPoster = baseURL + mMovie.getPosterPath();
        Picasso.with(mContext)
                .load(completeURLToPoster)
                .into(mMoviePoster);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_toolbar_movie_thumbnails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        fetchTrailerThumbnailURLs();
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
        // Get the widget reference from XML layout
        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
                R.id.collapsing_toolbar_layout_movie_details);
        mMovieBackdrop = (ImageView) findViewById(R.id.iv_movie_poster);
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster_toolbar);
        mPlot = (TextView) findViewById(R.id.tv_plot);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //int maxScroll = appBarLayout.getTotalScrollRange();
        //float progressPercentage = (float) (Math.abs(offset)/maxScroll);

        if (verticalOffset == 0) {
            //start an alpha animation on your ImageView here (i.e. fade out)
            mMoviePoster.animate().alpha(1f).setDuration(200l).setListener(null);
        } else {
            mMoviePoster.animate().alpha(0f).setDuration(200l).setListener(null);
            //Add an opposite animation here (i.e. it fades back in again)
        }
/*        if (verticalOffset == 0) {
            //mMoviePoster.setVisibility(View.VISIBLE);
        } else {
            //mMoviePoster.setAnimation(fadeOut);
            //
            //mMoviePoster.setVisibility(View.INVISIBLE);
        }*/
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
                    mRecyclerView.setAdapter(adapter);
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
}
