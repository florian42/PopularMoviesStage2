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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertTrue;

/**
 * Created by faumeier on 10.03.2017.
 */
public class TmdbApiEndpointInterfaceTest {

    private static final String MESSAGE_RESPONSE_NOT_SUCCESSFULL = "Request was not successfull";
    private static final String MESSAGE_RESPONSE_IS_NULL = "Response is null";
    private MockWebServer mockWebServer;
    private TmdbApiEndpointInterface mTmdbApiEndpointInterface;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(mockWebServer.url("").toString())
                .addConverterFactory
                        (GsonConverterFactory.create(gson))
                .build();
        mTmdbApiEndpointInterface = retrofit.create(TmdbApiEndpointInterface.class);
    }

    @Test
    public void getPopularMoviesPage1() throws Exception {
        final String jsonResponseBody = MockedHttpResponses.GET_POPULAR_MOVIES_JSON_RESPONSE;
        mockWebServer.enqueue(new MockResponse().setBody(jsonResponseBody).setResponseCode(200));
        final Call<Page> call = mTmdbApiEndpointInterface.getPopularMoviesPage1();
        final Response<Page> response = call.execute();
        assertTrue(MESSAGE_RESPONSE_IS_NULL, response != null);
        assertTrue(MESSAGE_RESPONSE_NOT_SUCCESSFULL, response.isSuccessful());
    }

    @Test
    public void getBestRatedMoviesPage1() throws Exception {
        final String jsonResponseBody = MockedHttpResponses.GET_BEST_RATED_MOVIES_JSON_RESPONSE;
        mockWebServer.enqueue(new MockResponse().setBody(jsonResponseBody).setResponseCode(200));
        final Call<Page> call = mTmdbApiEndpointInterface.getBestRatedMoviesPage1();
        final Response<Page> response = call.execute();
        assertTrue(MESSAGE_RESPONSE_IS_NULL, response != null);
        assertTrue(MESSAGE_RESPONSE_NOT_SUCCESSFULL, response.isSuccessful());
    }

    @Test
    public void getTrailersForMovie() throws Exception {
        final String movieId = "263115";
        final String jsonResponseBody = MockedHttpResponses.GET_TRAILERS_FOR_MOVIE_JSON_RESPONSE;
        mockWebServer.enqueue(new MockResponse().setBody(jsonResponseBody).setResponseCode(200));
        Call<ResultsTrailer>
                call = mTmdbApiEndpointInterface.getTrailersForMovie(movieId);
        final Response<ResultsTrailer> response = call.execute();
        assertTrue(MESSAGE_RESPONSE_IS_NULL, response != null);
        assertTrue(MESSAGE_RESPONSE_NOT_SUCCESSFULL, response.isSuccessful());
    }

    @Test
    public void getReviewsForMovie() throws Exception {
        final String movieId = "83542"; //Cloud Atlas
        final String jsonResponseBody = MockedHttpResponses.GET_REVIEWS_FOR_MOVIE_JSON_REFERENCE;
        mockWebServer.enqueue(new MockResponse().setBody(jsonResponseBody).setResponseCode(200));
        final Call<ReviewResults> call = mTmdbApiEndpointInterface.getReviewsForMovie(movieId);
        final Response<ReviewResults> response = call.execute();
        assertTrue(MESSAGE_RESPONSE_IS_NULL, response != null);
        assertTrue(MESSAGE_RESPONSE_NOT_SUCCESSFULL, response.isSuccessful());
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();

    }
}