package udacity.android.gkcs.popularmovies.fragment;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Arrays;

import udacity.android.gkcs.popularmovies.HttpClient;
import udacity.android.gkcs.popularmovies.R;
import udacity.android.gkcs.popularmovies.data.FavouritesColumns;
import udacity.android.gkcs.popularmovies.model.Movie;
import udacity.android.gkcs.popularmovies.model.Review;
import udacity.android.gkcs.popularmovies.model.ReviewResult;
import udacity.android.gkcs.popularmovies.model.Trailer;
import udacity.android.gkcs.popularmovies.model.TrailerResult;

public class DetailFragment extends Fragment {
    private Review[] reviews;
    private Trailer[] trailers;
    private static final String TAG = DetailFragment.class.getSimpleName();
    private Movie selectedMovie;
    private ShareActionProvider mShareActionProvider;
    private View rootView;
    private ViewGroup container;
    private ContentResolver contentResolver;
    private ImageButton button;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        button = (ImageButton) rootView.findViewById(R.id.favourite_button);
        contentResolver = getContext().getContentResolver();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Favourite button clicked" + selectedMovie);
                final ContentValues values = new ContentValues();
                values.put(FavouritesColumns._ID, selectedMovie.getId());
                if (!button.isSelected()) {
                    button.setSelected(true);
                    contentResolver.insert(FavouritesColumns.CONTENT_URI, values);
                    Snackbar.make(view, "Movie added to favourites!", Snackbar.LENGTH_LONG).show();
                } else {
                    button.setSelected(false);
                    Log.d(TAG, "onClick: movie to be deleted: " + selectedMovie.getId());
                    contentResolver.delete(FavouritesColumns.CONTENT_URI, FavouritesColumns._ID + "=?", new String[]{selectedMovie.getId()});
                    Snackbar.make(view, "Movie removed from favourites!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        this.container = container;
        addTrailersToLayout(rootView);
        addReviewsToLayout(rootView);
        Picasso.with(getContext()).load(R.drawable.star).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                button.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed: Bit map failed to load", new RuntimeException());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "onPrepareLoad: Preparing to load image into button");
            }
        });
        Log.d(TAG, "onCreateView:  Started filling movie details");
        fillMovieData(rootView, getActivity().getIntent());
        Log.d(TAG, "onCreateView: Done filling movie details");
        return rootView;
    }

    private void addReviewsToLayout(View rootView) {
        Log.d(TAG, "onCreateView: Review view to be made");
        final LinearLayout reviewList = (LinearLayout) rootView.findViewById(R.id.review_list);
        reviewList.removeAllViews();
        if (reviews != null) {
            for (final Review review : reviews) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.list_item_review, container, false);
                reviewList.addView(view);
                Log.i(TAG, "getView: Setting up review view " + review.toString());
                ((TextView) view.findViewById(R.id.review_author)).setText(review.getAuthor());
                ((TextView) view.findViewById(R.id.review_content)).setText(review.getContent());
            }
            Log.d(TAG, "onCreateView: Review view done");
        } else {
            Log.d(TAG, "addReviewsToLayout: Empty review list");
        }
    }

    private void addTrailersToLayout(View rootView) {
        Log.d(TAG, "onCreateView: Trailer view to be made");
        final LinearLayout trailerList = (LinearLayout) rootView.findViewById(R.id.trailer_list);
        trailerList.removeAllViews();
        if (trailers != null) {
            for (final Trailer trailer : trailers) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.list_item_trailer, container, false);
                trailerList.addView(view);
                Log.i(TAG, "getView: Setting up trailer view " + trailer.toString());
                ((TextView) view.findViewById(R.id.trailer_name)).setText(trailer.getName());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        watchTrailerOnYoutube(trailer.getKey());
                    }
                });
            }
            Log.d(TAG, "onCreateView: Trailer view done");
        } else {
            Log.d(TAG, "addTrailersToLayout: Empty trailer list");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.share_trailer));
        if (mShareActionProvider != null && reviews != null && reviews.length > 0) {
            mShareActionProvider.setShareIntent(shareYouTubeVideo(trailers[0].getKey()));
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
            ((TextView) rootView.findViewById(R.id.trailers_title)).setText(R.string.trailers_title);
            ((TextView) rootView.findViewById(R.id.reviews_title)).setText(R.string.reviews_title);
        }
    }

    private void updateTrailerList(final Trailer[] trailers) {
        if (trailers != null) {
            this.trailers = trailers;
            addTrailersToLayout(rootView);
        }
        if (mShareActionProvider != null && this.trailers != null && this.trailers.length > 0) {
            mShareActionProvider.setShareIntent(shareYouTubeVideo(this.trailers[0].getKey()));
        }
    }

    private void updateReviewList(final Review[] reviews) {
        if (reviews != null) {
            this.reviews = reviews;
            addReviewsToLayout(rootView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieDetails();
        final Cursor query = contentResolver.query(FavouritesColumns.CONTENT_URI,
                new String[]{FavouritesColumns._ID},
                FavouritesColumns._ID + "=?",
                new String[]{selectedMovie.getId()},
                null);
        if (query != null && query.moveToFirst()) {
            button.setSelected(true);
        } else {
            button.setSelected(false);
        }
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