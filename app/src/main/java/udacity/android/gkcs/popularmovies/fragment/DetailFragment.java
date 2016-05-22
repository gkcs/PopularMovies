package udacity.android.gkcs.popularmovies.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import udacity.android.gkcs.popularmovies.HttpClient;
import udacity.android.gkcs.popularmovies.R;
import udacity.android.gkcs.popularmovies.adapters.MovieArrayAdapter;
import udacity.android.gkcs.popularmovies.adapters.ReviewArrayAdapter;
import udacity.android.gkcs.popularmovies.adapters.TrailerArrayAdapter;
import udacity.android.gkcs.popularmovies.model.Movie;
import udacity.android.gkcs.popularmovies.model.Review;
import udacity.android.gkcs.popularmovies.model.ReviewResult;
import udacity.android.gkcs.popularmovies.model.Trailer;
import udacity.android.gkcs.popularmovies.model.TrailerResult;

public class DetailFragment extends Fragment {
    private ReviewArrayAdapter reviewArrayAdapter;
    private TrailerArrayAdapter trailerArrayAdapter;
    private static final String TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewArrayAdapter = new ReviewArrayAdapter(getActivity(), R.layout.fragment_main, new ArrayList<Review>());
        trailerArrayAdapter = new TrailerArrayAdapter(getActivity(), R.layout.fragment_main, new ArrayList<Trailer>());
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Log.d(TAG, "onCreateView: STARTED THE VIEWS");
        fillMovieData(rootView, getActivity().getIntent());
        Log.d(TAG, "onCreateView: DONE WITH VIEWS");
        return rootView;
    }

    private void fillMovieData(View rootView, Intent intent) {
        if (intent != null) {
            final Movie selectedMovie = intent.getParcelableExtra("movie");
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(selectedMovie.getTitle());
            final String path = "http://image.tmdb.org/t/p/w185" + selectedMovie.getImage();
            Picasso.with(getContext()).load(path).into((ImageView) rootView.findViewById(R.id.movie_image));
            final Resources resources = getResources();
            ((TextView) rootView.findViewById(R.id.movie_text))
                    .setText(String.format(resources.getString(R.string.content_display), selectedMovie.getOverview()));
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(String.format(resources.getString(R.string.rating_display), selectedMovie.getVote_average()));
            ((TextView) rootView.findViewById(R.id.movie_release_date))
                    .setText(String.format(resources.getString(R.string.release_date_display), selectedMovie.getRelease_date()));
        }
    }

    private void updateTrailerList(Trailer[] trailers) {
        if (trailers != null) {
            trailerArrayAdapter.clear();
            for (final Trailer trailer : trailers) {
                trailerArrayAdapter.add(trailer);
            }
        }
    }

    private void updateReviewList(Review[] reviews) {
        if (reviews != null) {
            reviewArrayAdapter.clear();
            for (final Review review : reviews) {
                reviewArrayAdapter.add(review);
            }
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
            updateTrailerList(result.getResults());
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
            updateReviewList(result.getResults());
            Log.d(TAG, "DONE FILLING Review ADAPTER");
        }
    }
}