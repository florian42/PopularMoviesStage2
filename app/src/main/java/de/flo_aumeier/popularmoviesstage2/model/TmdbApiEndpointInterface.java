package de.flo_aumeier.popularmoviesstage2.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface which specifies the api endpoint uris for themoviedb.
 */
public interface TmdbApiEndpointInterface {
    String BASE_URL = "https://api.themoviedb.org/3/movie/";

    @GET("popular?api_key=66c86590f283f43a95c4fff54da023dc&language=en-US&page=1")
    Call<Page> getPopularMoviesPage1();

    @GET("top_rated?api_key=66c86590f283f43a95c4fff54da023dc&language=en-US&page=1")
    Call<Page> getBestRatedMoviesPage1();

    @GET("{movieId}/videos?api_key=66c86590f283f43a95c4fff54da023dc&language=en-US")
    Call<ResultsTrailer> getTrailersForMovie(@Path("movieId") String movieId);

    @GET("{movieId}/reviews?api_key=66c86590f283f43a95c4fff54da023dc")
    Call<ReviewResults> getReviewsForMovie(@Path("movieId") String movieId);

}

