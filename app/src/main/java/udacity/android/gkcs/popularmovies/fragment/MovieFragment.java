package udacity.android.gkcs.popularmovies.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Comparator;

import udacity.android.gkcs.popularmovies.DetailActivity;
import udacity.android.gkcs.popularmovies.HttpClient;
import udacity.android.gkcs.popularmovies.R;
import udacity.android.gkcs.popularmovies.adapters.MovieArrayAdapter;
import udacity.android.gkcs.popularmovies.model.Movie;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MovieFragment extends Fragment {

    private static final String TAG = MovieFragment.class.getSimpleName();

    private MovieArrayAdapter movieAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: MENU ITEM SELECTED: " + item.toString());
        if (item.getItemId() == R.id.action_settings) {
            sortAdapter(getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.sort_key),
                            getString(R.string.sort_value)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MovieArrayAdapter(getActivity(), R.layout.fragment_main, new ArrayList<Movie>());
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d(TAG, "onCreateView: GRID VIEW TO BE MADE");
        final GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        Log.d(TAG, "onCreateView: GRID VIEW DONE");
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", movieAdapter.getItem(position)));
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
        new MoviesTask().execute(getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.sort_key), getString(R.string.sort_value)));
    }

    @NonNull
    private static Comparator<Movie> getRatingComparator() {
        return new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return getCeiledDiff(lhs.getVote_average() - rhs.getVote_average());
            }
        };
    }

    @NonNull
    private static Comparator<Movie> getPopularityComparator() {
        return new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return getCeiledDiff(lhs.getPopularity() - rhs.getPopularity());
            }
        };
    }

    private static int getCeiledDiff(double diff) {
        if (diff < 0) {
            return 1;
        } else if (diff > 0) {
            return -1;
        } else {
            return 0;
        }
    }

    private void updateMovieList(Movie[] movies) {
        if (movies != null) {
            movieAdapter.clear();
            for (final Movie movie : movies) {
                movieAdapter.add(movie);
            }
        }
        sortAdapter(getDefaultSharedPreferences(getActivity()).getString(getString(R.string.sort_key), getString(R.string.sort_value)));
    }

    public void sortAdapter(final String sortingOrder) {
        Log.d(TAG, "sortAdapter: PREFERENCE HAS CHANGED!!");
        if ("Popularity".equals(sortingOrder)) {
            movieAdapter.sort(getPopularityComparator());
        } else {
            movieAdapter.sort(getRatingComparator());
        }
    }

    public class MoviesTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            Log.i(TAG, "doInBackground: GETTING THE MOVIES");
            return HttpClient.getHttpClient().getMovies();
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            Log.d(TAG, "STARTED FILLING ADAPTER");
            updateMovieList(result);
            Log.d(TAG, "DONE FILLING ADAPTER");
        }
    }
}

