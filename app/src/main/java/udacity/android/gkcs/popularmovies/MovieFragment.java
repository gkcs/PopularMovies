package udacity.android.gkcs.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Comparator;

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
            sortAdapter(PreferenceManager.getDefaultSharedPreferences(getActivity())
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
        new MoviesTask().execute(PreferenceManager.getDefaultSharedPreferences(getActivity())
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
        sortAdapter(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.sort_key),
                        getString(R.string.sort_value)));
    }

    private void updateMovie(TrailerResult trailerResult) {
        if (trailerResult != null) {
            for (int i = 0; i < movieAdapter.getCount(); i++) {
                if (movieAdapter.getItem(i).getId().equals(trailerResult.getId())) {
                    movieAdapter.getItem(i).setTrailers(trailerResult.getResults());
                    movieAdapter.notifyDataSetChanged();
                    break;
                }
            }
        } else {
            Log.i(TAG, "updateMovie: Cannot set null trailerResult in movie list");
        }
    }

    private void updateMovie(ReviewResult reviewResult) {
        if (reviewResult != null) {
            for (int i = 0; i < movieAdapter.getCount(); i++) {
                if (movieAdapter.getItem(i).getId().equals(reviewResult.getId())) {
                    movieAdapter.getItem(i).setReviews(reviewResult.getResults());
                    movieAdapter.notifyDataSetChanged();
                    break;
                }
            }
        } else {
            Log.i(TAG, "updateMovie: Cannot set null reviewResult in movie list");
        }
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

    public class MovieTrailerTask extends AsyncTask<String, Void, TrailerResult> {

        @Override
        protected TrailerResult doInBackground(String... params) {
            Log.i(TAG, "doInBackground: GETTING THE MOVIE TRAILERS");
            return HttpClient.getHttpClient().getMovieDetails(params[0], "videos", TrailerResult.class);
        }

        @Override
        protected void onPostExecute(TrailerResult result) {
            Log.d(TAG, "STARTED FILLING Trailer ADAPTER");
            updateMovie(result);
            Log.d(TAG, "DONE FILLING Trailer ADAPTER");
        }
    }

    public class MovieReviewTask extends AsyncTask<String, Void, ReviewResult> {

        @Override
        protected ReviewResult doInBackground(String... params) {
            Log.i(TAG, "doInBackground: GETTING THE MOVIE REVIEWS");
            return HttpClient.getHttpClient().getMovieDetails(params[0], "reviews", ReviewResult.class);
        }

        @Override
        protected void onPostExecute(ReviewResult result) {
            Log.d(TAG, "STARTED FILLING Review ADAPTER");
            updateMovie(result);
            Log.d(TAG, "DONE FILLING Review ADAPTER");
        }
    }
}

class Movie implements Parcelable {
    private final String id;
    private final String poster_path;
    private final String overview;
    private final String release_date;
    private final String title;
    private final Double popularity;
    private final Double vote_average;
    private Trailer[] trailers;
    private Review[] reviews;

    public String getImage() {
        return poster_path;
    }

    public String getOverview() {
        return (overview);
    }

    public String getRelease_date() {
        return (release_date);
    }

    public String getTitle() {
        return (title);
    }

    public Double getPopularity() {
        return (popularity);
    }

    public Double getVote_average() {
        return (vote_average);
    }

    public String getId() {
        return (id);
    }


    public Movie(final String id,
                 final String poster_path,
                 final String overview,
                 final String release_date,
                 final String title,
                 final Double popularity,
                 final Double vote_average) {
        this.id = id;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.title = title;
        this.popularity = popularity;
        this.vote_average = vote_average;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        title = in.readString();
        popularity = in.readByte() == 0x00 ? null : in.readDouble();
        vote_average = in.readByte() == 0x00 ? null : in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(title);
        if (popularity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(popularity);
        }
        if (vote_average == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(vote_average);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Trailer[] getTrailers() {
        return trailers;
    }

    public void setTrailers(Trailer[] trailerUrl) {
        this.trailers = trailerUrl;
    }

    public Review[] getReviews() {
        return reviews;
    }

    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
    }
}


class TrailerResult {
    private final Trailer[] results;
    private final String id;

    public TrailerResult(final Trailer[] results, String id) {
        this.results = results;
        this.id = id;
    }

    public Trailer[] getResults() {
        return results;
    }

    public String getId() {
        return id;
    }
}

class ReviewResult {
    private final Review[] results;
    private final String id;

    public ReviewResult(final Review[] results, String id) {
        this.results = results;
        this.id = id;
    }

    public Review[] getResults() {
        return results;
    }

    public String getId() {
        return id;
    }
}

class MovieResult {
    private final Movie[] results;

    public MovieResult(final Movie[] results) {
        this.results = results;
    }

    public Movie[] getResults() {
        return results;
    }
}