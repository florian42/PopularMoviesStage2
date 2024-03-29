package de.flo_aumeier.popularmoviesstage2.model;

import de.flo_aumeier.popularmoviesstage2.BuildConfig;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface which specifies the api endpoint uris for themoviedb.
 */
public interface TmdbApiEndpointInterface {
    String BASE_URL = "https://api.themoviedb.org/3/movie/";
    String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    @GET("popular?api_key=" + API_KEY + "&language=en-US&page=1")
    Call<Page> getPopularMoviesPage1();

    @GET("top_rated?api_key=" + API_KEY + "&language=en-US&page=1")
    Call<Page> getBestRatedMoviesPage1();

    @GET("{movieId}/videos?api_key=" + API_KEY + "&language=en-US")
    Call<ResultsTrailer> getTrailersForMovie(@Path("movieId") String movieId);

    @GET("{movieId}/reviews?api_key=" + API_KEY)
    Call<ReviewResults> getReviewsForMovie(@Path("movieId") String movieId);

}

