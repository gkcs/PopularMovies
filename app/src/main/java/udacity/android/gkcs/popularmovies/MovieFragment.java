package udacity.android.gkcs.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import java.util.Arrays;
import java.util.Comparator;

public class MovieFragment extends Fragment {

    private ArrayAdapter<Movie> movieAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieData();
    }

    private void getMovieData() {
        new MovieTask().execute(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.sort_key),
                        getString(R.string.sort_value)));
    }

    public class MovieTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            Movie[] movies = HttpClient.getHttpClient().getMovies();
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
            if (result != null) {
                movieAdapter.clear();
                for (final Movie movie : result) {
                    movieAdapter.add(movie);
                }
            }
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
        return image;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTitle() {
        return title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public String getId() {
        return id;
    }

    public Movie(String id, final String image,
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