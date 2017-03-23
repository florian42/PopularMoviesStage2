package de.flo_aumeier.popularmoviesstage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

import de.flo_aumeier.popularmoviesstage2.model.Movie;
import de.flo_aumeier.popularmoviesstage2.model.Page;
import de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface;
import de.flo_aumeier.popularmoviesstage2.model.error.ErrorMessages;
import de.flo_aumeier.popularmoviesstage2.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter
        .ListItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_MOVIE = "EXTRA_MOVIE";
    SharedPreferences mSharedPreferences;
    private MovieAdapter mPopularMoviesAdapter;
    private MovieAdapter mBestRatedMoviesAdapter;
    private List<Movie> mMovies;
    private MainActivity mActivity;
    private View mCoordinatorLayout;
    private RecyclerView mRecyclerView;
    /**
     * Displays an error if one is encountered
     */
    private Snackbar mErrorSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mActivity = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                spanCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mCoordinatorLayout = findViewById(R.id.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (NetworkUtils.isOnline(this)) {
            setSortOrder(mSharedPreferences.getBoolean(getString(R.string.pref_short_sort_order),
                    getResources().getBoolean(R.bool.pref_sort_order_default)));
        } else {
            showNetworkError();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        final Movie clickedMovie = mMovies.get(clickedItemIndex);
        Toast.makeText(this, "Clicked: #" + clickedItemIndex, Toast.LENGTH_SHORT).show();
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(R.string.pref_short_sort_order)) {
            Boolean sortBestRated = sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool
                    .pref_sort_order_default));
            setSortOrder(sortBestRated);
        }
    }

    public void fetchPopularMovies() {
        Log.d(TAG, "Fetching Popular Movies");
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
                    if (null == mPopularMoviesAdapter) {
                        mPopularMoviesAdapter = new MovieAdapter(mActivity, mMovies);
                    }
                    if (mRecyclerView.getAdapter() == null) {
                        mRecyclerView.setAdapter(mPopularMoviesAdapter);
                    } else {
                        mRecyclerView.swapAdapter(mPopularMoviesAdapter, false);
                    }
                } else {
                    Log.d(TAG, response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }


        });
    }

    /**
     * Sets the sortOrder to best Rated if sortOrderBestRated = true
     *
     * @param sortOrderBestRated
     */
    private void setSortOrder(Boolean sortOrderBestRated) {

        if (NetworkUtils.isOnline(this)) {
            Log.d(TAG, "Sorting Best Rated Movies: " + sortOrderBestRated);
            if (sortOrderBestRated) {
                fetchBestRatedMovies();
            } else {
                fetchPopularMovies();
            }
        } else {
            showNetworkError();
        }
    }

    private void showNetworkError() {
        mErrorSnackBar = Snackbar
                .make(mCoordinatorLayout, ErrorMessages.NO_NETWORK_CONNECTION.getMessage(), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSortOrder(mSharedPreferences.getBoolean(getString(R.string.pref_short_sort_order),
                                getResources().getBoolean(R.bool.pref_sort_order_default)));
                    }
                });
        mErrorSnackBar.show();
    }

    private void fetchBestRatedMovies() {
        LinkedList<Movie> fetchedMovies = null;
        Log.d(TAG, "Fetching Best Rated Movies");
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
                    if (null == mBestRatedMoviesAdapter) {
                        mBestRatedMoviesAdapter = new MovieAdapter(mActivity, mMovies);
                    }
                    if (mRecyclerView.getAdapter() == null) {
                        mRecyclerView.setAdapter(mBestRatedMoviesAdapter);
                    } else {
                        mRecyclerView.swapAdapter(mBestRatedMoviesAdapter, false);
                    }
                } else {
                    Log.d(TAG, response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }


        });

    }
}
