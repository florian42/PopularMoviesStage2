package de.flo_aumeier.popularmoviesstage2.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Flo on 27.02.2017.
 */

public interface TmdbApiEndpointInterface {
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    @GET("popular?api_key=66c86590f283f43a95c4fff54da023dc&language=en-US&page=1")
    Call<Page> getPopularMoviesPage1();

    @GET("top_rated?api_key=66c86590f283f43a95c4fff54da023dc&language=en-US&page=1")
    Call<Page> getBestRatedMoviesPage1();

    @GET("{movieId}/videos?api_key=66c86590f283f43a95c4fff54da023dc&language=en-US")
    Call<ResultsTrailer> getTrailersForMovie(@Path("movieId") String movieId);

}

