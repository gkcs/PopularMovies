package udacity.android.gkcs.popularmovies.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import udacity.android.gkcs.popularmovies.HttpClient;
import udacity.android.gkcs.popularmovies.R;
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
    private Movie selectedMovie;
    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        trailerArrayAdapter = new TrailerArrayAdapter(getActivity(), R.layout.fragment_detail, new ArrayList<Trailer>());
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Log.d(TAG, "onCreateView: Trailer view to be made");
        final ListView trailerListView = (ListView) rootView.findViewById(R.id.trailer_list);
        trailerListView.setAdapter(trailerArrayAdapter);
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                watchTrailerOnYoutube(trailerArrayAdapter.getItem(position).getKey());
            }
        });
        Log.d(TAG, "onCreateView: Trailer view done");

        reviewArrayAdapter = new ReviewArrayAdapter(getActivity(), R.layout.fragment_detail, new ArrayList<Review>());
        Log.d(TAG, "onCreateView: Review view to be made");
        final ListView reviewListView = (ListView) rootView.findViewById(R.id.review_list);
        reviewListView.setAdapter(reviewArrayAdapter);
        Log.d(TAG, "onCreateView: Review view done");

        Log.d(TAG, "onCreateView:  Started filling movie details");
        fillMovieData(rootView, getActivity().getIntent());
        Log.d(TAG, "onCreateView: Done filling movie details");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.share_trailer));
        if (reviewArrayAdapter != null && reviewArrayAdapter.getCount() > 0) {
            mShareActionProvider.setShareIntent(shareYouTubeVideo(trailerArrayAdapter.getItem(0).getKey()));
        }
    }

    private void fillMovieData(final View rootView, final Intent intent) {
        if (intent != null) {
            selectedMovie = intent.getParcelableExtra("movie");
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(selectedMovie.getTitle());
            final String path = String.format(getResources().getString(R.string.image_url), selectedMovie.getImage());
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

    private void updateTrailerList(final Trailer[] trailers) {
        if (trailers != null) {
            trailerArrayAdapter.clear();
            for (final Trailer trailer : trailers) {
                trailerArrayAdapter.add(trailer);
            }
        }
        mShareActionProvider.setShareIntent(shareYouTubeVideo(trailerArrayAdapter.getItem(0).getKey()));
    }

    private void updateReviewList(final Review[] reviews) {
        if (reviews != null) {
            reviewArrayAdapter.clear();
            for (final Review review : reviews) {
                reviewArrayAdapter.add(review);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieDetails();
    }

    private void getMovieDetails() {
        new MovieTrailerTask().execute(selectedMovie.getId());
        new MovieReviewTask().execute(selectedMovie.getId());
    }

    public void watchTrailerOnYoutube(final String key) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key)));
        } catch (ActivityNotFoundException ex) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(getResources().getString(R.string.you_tube_prefix), key))));
        }
    }

    private Intent shareYouTubeVideo(final String key) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.you_tube_prefix), key));
        return shareIntent;
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
            Log.d(TAG, "DONE FILLING Trailer ADAPTER " + Arrays.toString(result.getResults()));
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
            Log.d(TAG, "DONE FILLING Review ADAPTER" + Arrays.toString(result.getResults()));
        }
    }
}