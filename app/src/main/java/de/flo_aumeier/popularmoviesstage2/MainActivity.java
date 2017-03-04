package de.flo_aumeier.popularmoviesstage2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;


import de.flo_aumeier.popularmoviesstage2.model.Page;
import de.flo_aumeier.popularmoviesstage2.model.Movie;
import de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_MOVIE = "EXTRA_MOVIE";

    private MovieAdapter mPopularMoviesAdapter;
    private List<Movie> mMovies;
    private MainActivity mActivity;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                spanCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        fetchPopularMovies();
    }

    public void fetchPopularMovies() {
        Log.d(TAG, "Calling API");
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
                    Log.d(TAG, mMovies.get(1).toString());
                    mPopularMoviesAdapter = new MovieAdapter(mActivity, mMovies);
                    mRecyclerView.setAdapter(mPopularMoviesAdapter);
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

    @Override
    public void onListItemClick(int clickedItemIndex) {
        final Movie clickedMovie = mMovies.get(clickedItemIndex);
        Intent startMovieActivityIntent = new Intent(this, MovieActivity.class);
        startMovieActivityIntent.putExtra(INTENT_EXTRA_MOVIE, clickedMovie);
    }
}
