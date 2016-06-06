package udacity.android.gkcs.popularmovies.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import udacity.android.gkcs.popularmovies.DetailActivity;
import udacity.android.gkcs.popularmovies.HttpClient;
import udacity.android.gkcs.popularmovies.R;
import udacity.android.gkcs.popularmovies.adapters.MovieArrayAdapter;
import udacity.android.gkcs.popularmovies.data.FavouritesColumns;
import udacity.android.gkcs.popularmovies.data.MovieColumns;
import udacity.android.gkcs.popularmovies.model.Movie;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MovieFragment extends Fragment {

    private static final String TAG = MovieFragment.class.getSimpleName();
    public static final int FAVOURITE_ID = 0;
    public static final int MOVIE_ID = 0;
    public static final int POSTER_PATH = 1;
    public static final int OVERVIEW = 2;
    public static final int RELEASE_DATE = 3;
    public static final int TITLE = 4;
    public static final int POPULARITY = 5;
    public static final int VOTE_AVERAGE = 6;

    private MovieArrayAdapter movieAdapter;
    private int mPosition;
    private GridView gridView;
    private static final String SELECTED_KEY = "selected_position";

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
            updateMovieView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovieView() {
        if (getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.view_choice_key), getString(R.string.view_all_value))
                .equals("Favourites")) {
            updateListToOnlyShowFavourites();
        } else {
            getMovieData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MovieArrayAdapter(getActivity(), R.layout.fragment_main, new ArrayList<Movie>());
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d(TAG, "onCreateView: GRID VIEW TO BE MADE");
        gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        Log.d(TAG, "onCreateView: GRID VIEW DONE");
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", movieAdapter.getItem(position)));
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieView();
        if (mPosition != ListView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    private void getMovieData() {
        new MoviesTask().execute();
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

    private void updateListToOnlyShowFavourites() {
        final ContentResolver contentResolver = getContext().getContentResolver();
        final Cursor favouritesCursor = contentResolver.query(FavouritesColumns.CONTENT_URI, null, null, null, null);
        final List<Movie> movies = new ArrayList<>();
        if (favouritesCursor != null) {
            while (favouritesCursor.moveToNext()) {
                final Cursor movieCursor = contentResolver.query(MovieColumns.CONTENT_URI,
                        null,
                        MovieColumns._ID + "=?",
                        new String[]{favouritesCursor.getString(FAVOURITE_ID)},
                        null);
                if (movieCursor != null && movieCursor.moveToFirst()) {
                    movies.add(new Movie(movieCursor.getString(MOVIE_ID),
                            movieCursor.getString(POSTER_PATH),
                            movieCursor.getString(OVERVIEW),
                            movieCursor.getString(RELEASE_DATE),
                            movieCursor.getString(TITLE),
                            Double.parseDouble(movieCursor.getString(POPULARITY)),
                            Double.parseDouble(movieCursor.getString(VOTE_AVERAGE))));
                }
            }
        }
        updateMovieList(movies.toArray(new Movie[movies.size()]));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
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
            insertIntoDatabase(result);
            Log.d(TAG, "DONE FILLING ADAPTER");
        }
    }

    private void insertIntoDatabase(final Movie[] movies) {
        final ContentValues[] contentValues = new ContentValues[movies.length];
        for (int i = 0; i < movies.length; i++) {
            contentValues[i] = new ContentValues();
            contentValues[i].put(MovieColumns.OVERVIEW, movies[i].getOverview());
            contentValues[i].put(MovieColumns.POPULARITY, movies[i].getPopularity());
            contentValues[i].put(MovieColumns.POSTER_PATH, movies[i].getImage());
            contentValues[i].put(MovieColumns.RELEASE_DATE, movies[i].getRelease_date());
            contentValues[i].put(MovieColumns.TITLE, movies[i].getTitle());
            contentValues[i].put(MovieColumns.VOTE_AVERAGE, movies[i].getVote_average());
            contentValues[i].put(MovieColumns._ID, movies[i].getId());
            Log.d(TAG, "insertIntoDatabase: movie added to content values: " + movies[i].getId());
        }
        getContext().getContentResolver().bulkInsert(MovieColumns.CONTENT_URI, contentValues);
    }
}

