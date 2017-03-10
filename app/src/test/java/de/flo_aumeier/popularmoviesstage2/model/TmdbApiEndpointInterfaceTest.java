package de.flo_aumeier.popularmoviesstage2.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.flo_aumeier.popularmoviesstage2.model.mock.MockedHttpResponses;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertTrue;

/**
 * Created by faumeier on 10.03.2017.
 */
public class TmdbApiEndpointInterfaceTest {

    MockWebServer mockWebServer;
    Retrofit mRetrofit;
    TmdbApiEndpointInterface mTmdbApiEndpointInterface;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        Gson gson = new GsonBuilder().setLenient().create();
        mRetrofit = new Retrofit.Builder().baseUrl(TmdbApiEndpointInterface.BASE_URL)
                .addConverterFactory
                        (GsonConverterFactory.create(gson))
                .build();
        mTmdbApiEndpointInterface = mRetrofit.create(TmdbApiEndpointInterface.class);
    }

    @Test
    public void getPopularMoviesPage1() throws Exception {
//TODO: Impelement
    }

    @Test
    public void getBestRatedMoviesPage1() throws Exception {
//TODO: Impelement
    }

    @Test
    public void getTrailersForMovie() throws Exception {
        //TODO: Impelement
        final String movieId = "263115";
        final String jsonResponseBody = MockedHttpResponses.GET_TRAILERS_FOR_MOVIE_JSON_RESPONSE;
        mockWebServer.enqueue(new MockResponse().setBody(jsonResponseBody));
        Call<ResultsTrailer> call = mTmdbApiEndpointInterface.getTrailersForMovie(movieId);
        assertTrue(call.execute() != null);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();

    }
}