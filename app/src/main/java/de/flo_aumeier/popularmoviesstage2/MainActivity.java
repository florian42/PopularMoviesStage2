package de.flo_aumeier.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

import de.flo_aumeier.popularmoviesstage2.model.Movie;
import de.flo_aumeier.popularmoviesstage2.model.Page;
import de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface;
import de.flo_aumeier.popularmoviesstage2.model.db.FavouriteMovieContract;
import de.flo_aumeier.popularmoviesstage2.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Displays the main view in the app.
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter
        .ListItemClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_MOVIE = "EXTRA_MOVIE";
    int mSorting;
    private MovieAdapter mPopularMoviesAdapter;
    private MovieAdapter mBestRatedMoviesAdapter;
    private MovieAdapter mFavouriteMoviesAdapter;
    private List<Movie> mMovies;
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mActivity = this;
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                spanCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (savedInstanceState != null) {
            int sorting = savedInstanceState.getInt("sorting");
            displayLastView(sorting);
        } else {
            fetchPopularMovies();
        }
    }

    private boolean isOnline() {
        if (!NetworkUtils.isOnline(this)) {
            mRecyclerView.setVisibility(View.GONE);
            showError(true);
            return false;
        } else {
            showError(false);
            return true;
        }
    }

    private void showError(boolean showError) {
        TextView textView = (TextView) findViewById(R.id.tv_error);
        if (showError) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }


    }

    private void displayLastView(int sorting) {
        switch (sorting) {
            case 0:
                fetchPopularMovies();
                break;
            case 1:
                fetchBestRatedMovies();
                break;
            case 2:
                fetchFavouriteMovies();
                break;
            default:
                fetchPopularMovies();
        }
    }

    private int getLastSorting() {
        MovieAdapter lastAdapter = (MovieAdapter) mRecyclerView.getAdapter();
        if (lastAdapter.equals(mPopularMoviesAdapter)) {
            return 0;
        } else if (lastAdapter.equals(mBestRatedMoviesAdapter)) {
            return 1;
        } else if (lastAdapter.equals(mFavouriteMoviesAdapter)) {
            return 2;
        }
        return 0;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("sorting", getLastSorting());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        final Movie clickedMovie = mMovies.get(clickedItemIndex);
        Intent startMovieActivityIntent = new Intent(this, MovieActivity.class);
        startMovieActivityIntent.putExtra(INTENT_EXTRA_MOVIE, clickedMovie);
        startActivity(startMovieActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
                /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        switch (itemThatWasClickedId) {
            case R.id.sort_order_best_rated:
                if (mBestRatedMoviesAdapter == null) {
                    fetchBestRatedMovies();
                }
                displayBestRatedMovies();
                break;
            case R.id.sort_order_most_popular:
                if (mPopularMoviesAdapter == null) {
                    fetchPopularMovies();
                }
                displayPopularMovies();
                break;
            case R.id.sort_order_favourite_movies:
                if (mFavouriteMoviesAdapter == null) {
                    fetchFavouriteMovies();
                }
                displayFavouriteMovies();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayFavouriteMovies() {
        showError(false);
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mFavouriteMoviesAdapter);
        } else {
            mRecyclerView.swapAdapter(mFavouriteMoviesAdapter, false);
        }
    }

    private void fetchFavouriteMovies() {
        Log.d(TAG, "Fetching Favourite Movies");
        showLoadingIndicator(true);
        ContentResolver favouriteMoviesContentProvider = getContentResolver();
        Cursor cursor = favouriteMoviesContentProvider.query(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, null, null, null, null);
        mMovies = new LinkedList<>();
        while (cursor.moveToNext()) {
            String movieTitle = cursor.getString(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE));
            int movieId = cursor.getInt(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID));
            String posterPath = cursor.getString(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_POSTER_PATH));
            String overview = cursor.getString(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_OVERVIEW));
            String releaseDate = cursor.getString(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE));
            String backdropPath = cursor.getString(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_BACKDROP_PATH));
            int voteCount = cursor.getInt(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_VOTE_COUNT));
            double voteAverage = cursor.getDouble(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_VOTE_AVERAGE));
            Movie favouriteMovie = new Movie(posterPath, overview, releaseDate, movieId, movieTitle, backdropPath, voteCount, voteAverage);
            mMovies.add(favouriteMovie);
        }
        cursor.close();
        mFavouriteMoviesAdapter = new MovieAdapter(this, mMovies);
        showLoadingIndicator(false);
        displayFavouriteMovies();
    }

    private void displayPopularMovies() {
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mPopularMoviesAdapter);
        } else {
            mRecyclerView.swapAdapter(mPopularMoviesAdapter, false);
        }
    }

    private void displayBestRatedMovies() {
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mBestRatedMoviesAdapter);
        } else {
            mRecyclerView.swapAdapter(mBestRatedMoviesAdapter, false);
        }
    }

    private void fetchPopularMovies() {
        Log.d(TAG, "Fetching Popular Movies");
        if (isOnline()) {
            showError(false);
            showLoadingIndicator(true);
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(TmdbApiEndpointInterface.BASE_URL)
                    .addConverterFactory
                            (GsonConverterFactory.create(gson))
                    .build();
            TmdbApiEndpointInterface endpointInterface = retrofit.create(TmdbApiEndpointInterface.class);
            Call<Page> call = endpointInterface.getPopularMoviesPage1();
            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {
                    if (response.isSuccessful()) {
                        Page popularMovies = response.body();
                        mMovies = popularMovies.getMovies();
                        mPopularMoviesAdapter = new MovieAdapter(mActivity, mMovies);
                        displayPopularMovies();
                    } else {
                        Log.d(TAG, response.errorBody().toString());
                    }
                    showLoadingIndicator(false);

                }


                @Override
                public void onFailure(Call<Page> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }


            });
        } else {
            showError(true);
        }

    }

    private void fetchBestRatedMovies() {
        Log.d(TAG, "Fetching Best Rated Movies");
        if (isOnline()) {
            showError(false);
            showLoadingIndicator(true);
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(TmdbApiEndpointInterface.BASE_URL)
                    .addConverterFactory
                            (GsonConverterFactory.create(gson))
                    .build();
            TmdbApiEndpointInterface endpointInterface = retrofit.create(TmdbApiEndpointInterface.class);
            Call<Page> call = endpointInterface.getBestRatedMoviesPage1();
            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {
                    if (response.isSuccessful()) {
                        Page bestRatedMoviesPage = response.body();
                        mMovies = bestRatedMoviesPage.getMovies();
                        mBestRatedMoviesAdapter = new MovieAdapter(mActivity, mMovies);
                        displayBestRatedMovies();
                    } else {
                        Log.d(TAG, response.errorBody().toString());
                    }
                    showLoadingIndicator(false);
                }

                @Override
                public void onFailure(Call<Page> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }


            });
        } else {
            showError(true);
        }

    }

    private void showLoadingIndicator(boolean show) {
        if (show) {
            mRecyclerView.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
