package de.flo_aumeier.popularmoviesstage2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;


import de.flo_aumeier.popularmoviesstage2.model.Movie;
import de.flo_aumeier.popularmoviesstage2.model.Page;
import de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface;
import de.flo_aumeier.popularmoviesstage2.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mButtonCallAPI;
    private EditText mTextResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonCallAPI = (Button) findViewById(R.id.bttn_call_api);
        mTextResult = (EditText) findViewById(R.id.result);
    }

    public void callAPI(View view) {
        Log.d(TAG, "Call API Button clicked!");
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
                    Log.d(TAG, "Response Successful");
                    Page popularMovies = response.body();
                    List<Movie> movies = popularMovies.getResults();
                    for (int i = 0; i < 5; i++) {
                        mTextResult.append(movies.get(i).getTitle());
                        mTextResult.append(" ");
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
