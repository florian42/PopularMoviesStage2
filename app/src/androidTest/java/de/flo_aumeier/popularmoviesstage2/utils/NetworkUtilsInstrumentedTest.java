package de.flo_aumeier.popularmoviesstage2.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.net.URL;

import de.flo_aumeier.popularmoviesstage2.utils.NetworkUtils;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NetworkUtilsInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("de.flo_aumeier.popularmoviesstage2", appContext.getPackageName());
    }

    private static final String messageURLdoesntMatch = "URL doesn't match";

    @Test
    public void buildUrlPopularMovies() throws Exception {
        final URL expectedUrl = new URL(
                "https://api.themoviedb"
                        + ".org/3/movie/popular?api_key=66c86590f283f43a95c4fff54da023dc&language"
                        + "=en-US&page=1");
        final URL actualUrl = NetworkUtils.buildUrlPopularMovies();
        assertEquals(messageURLdoesntMatch, expectedUrl, actualUrl);
    }

    @Test
    public void buildUrlBestRatedMovies() throws Exception {
        final URL expectedUrl = new URL(
                "https://api.themoviedb"
                        + ".org/3/movie/top_rated?api_key=66c86590f283f43a95c4fff54da023dc"
                        + "&language=en-US&page=1");
        final URL actualUrl = NetworkUtils.buildUrlBestRatedMovies();
        assertEquals(messageURLdoesntMatch, expectedUrl, actualUrl);
    }

    @Test
    public void buildUrlMovieStills() throws Exception {
        final URL expectedUrl = new URL(
                "https://api.themoviedb"
                        + ".org/3/movie/372058/images?api_key=66c86590f283f43a95c4fff54da023dc");
        final URL actualUrl = NetworkUtils.buildUrlMovieStills("372058");
        assertEquals(messageURLdoesntMatch, expectedUrl, actualUrl);
    }
}
