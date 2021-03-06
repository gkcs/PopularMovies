package udacity.android.gkcs.popularmovies;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import udacity.android.gkcs.popularmovies.model.Movie;
import udacity.android.gkcs.popularmovies.model.MovieResult;

public class HttpClient {
    public static final String MOVIE_ADDRESS = "https://api.themoviedb.org/3/movie/";
    private final String TAG = HttpClient.class.getSimpleName();
    private final Gson gson = new Gson();

    private static final HttpClient httpclient = new HttpClient();

    public static HttpClient getHttpClient() {
        return httpclient;
    }

    public Movie[] getMovies() {
        return getMovies(MOVIE_ADDRESS + "top_rated?");
    }

    private Movie[] getMovies(final String url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJson = null;
        try {
            urlConnection = (HttpURLConnection) new URL(Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                    .build().toString()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            final StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            movieJson = stringBuilder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return gson.fromJson(movieJson, MovieResult.class).getResults();
    }

    public <T> T getMovieDetails(final String movieId, final String detailType, final Class<T> clazz) {
        return getMovieDetails(MOVIE_ADDRESS + movieId + "/" + detailType + "?", clazz);
    }

    private <T> T getMovieDetails(final String url, final Class<T> clazz) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String responseJson = null;
        Log.i(TAG, "getMovieDetails: url: " + url);
        try {
            urlConnection = (HttpURLConnection) new URL(Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                    .build().toString()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            final StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            responseJson = stringBuilder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        Log.i(TAG, "getMovieDetails: json response: " + responseJson);
        return gson.fromJson(responseJson, clazz);
    }
}
