package udacity.android.gkcs.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MovieFragment extends Fragment {

    private static final String TAG = MovieFragment.class.getSimpleName();

    private ArrayAdapter<Movie> movieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviechoice, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            getMovieData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter =
                new ArrayAdapter<>(
                        getActivity(),
                        R.layout.fragment_detail,
                        R.id.movies_grid,
                        new ArrayList<Movie>());
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d(TAG, "onCreateView: GRID VIEW TO BE MADE");
        final GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        Log.d(TAG, "onCreateView: GRID VIEW DONE");
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieAdapter.getItem(position).getId()));
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieData();
    }

    private void getMovieData() {
        MovieTask movieTask = new MovieTask();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String string = defaultSharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_value));
        movieTask.execute(string);
    }

    public class MovieTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            final Movie[] movies = HttpClient.getHttpClient().getMovies();
            if ("POPULARITY".equals(params[0])) {
                Arrays.sort(movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie lhs, Movie rhs) {
                        return getCeiledDiff(lhs.getPopularity() - rhs.getPopularity());
                    }
                });
            } else {
                Arrays.sort(movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie lhs, Movie rhs) {
                        return getCeiledDiff(lhs.getVote_average() - rhs.getVote_average());
                    }
                });
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            Log.d(TAG, "STARTED FILLING ADAPTER");
            if (result != null) {
                movieAdapter.clear();
                for (final Movie movie : result) {
                    movieAdapter.add(movie);
                }
            }
            Log.d(TAG, "DONE FILLING ADAPTER");
        }

        private int getCeiledDiff(double diff) {
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}

class Movie {
    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date + '\'' +
                ", title='" + title + '\'' +
                ", popularity=" + popularity +
                ", vote_average=" + vote_average +
                '}';
    }

    private final String id;
    private final String image;
    private final String overview;
    private final String release_date;
    private final String title;
    private final Double popularity;
    private final Double vote_average;


    public String getImage() {
        return getNotNullObject(image);
    }

    public String getOverview() {
        return getNotNullObject(overview);
    }

    public String getRelease_date() {
        return getNotNullObject(release_date);
    }

    public String getTitle() {
        return getNotNullObject(title);
    }

    public Double getPopularity() {
        return getNotNullObject(popularity);
    }

    public Double getVote_average() {
        return getNotNullObject(vote_average);
    }

    public String getId() {
        return getNotNullObject(id);
    }

    @NonNull
    private Double getNotNullObject(final Double popularity) {
        return popularity == null ? 0d : popularity;
    }

    @NonNull
    private String getNotNullObject(final String string) {
        return string == null ? "ABC" : string;
    }

    public Movie(final String id,
                 final String image,
                 final String overview,
                 final String release_date,
                 final String title,
                 final Double popularity,
                 final Double vote_average) {
        this.id = id;
        this.image = image;
        this.overview = overview;
        this.release_date = release_date;
        this.title = title;
        this.popularity = popularity;
        this.vote_average = vote_average;
    }
}

class MovieResult {
    private final Movie[] results;

    MovieResult(final Movie[] results) {
        this.results = results;
    }

    public Movie[] getResults() {
        return results;
    }
}