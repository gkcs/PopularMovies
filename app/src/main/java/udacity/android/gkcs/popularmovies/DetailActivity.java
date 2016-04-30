package udacity.android.gkcs.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        private static final String TAG = DetailFragment.class.getSimpleName();
        private Movie selectedMovie = new Movie("", "", "", "", "", 0d, 0d);
        private String mMovieSorting;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            final Intent intent = getActivity().getIntent();
            Log.d(TAG, "onCreateView: STARTED THE VIEWS");
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieSorting = intent.getStringExtra(Intent.EXTRA_TEXT);
                TextView viewById = (TextView) rootView.findViewById(R.id.movie_title);
                viewById.setText(selectedMovie.getTitle());
                ImageView imageview = (ImageView) rootView.findViewById(R.id.movie_image);
                Picasso.with(getContext()).load(selectedMovie.getImage()).into(imageview);
                TextView viewById1 = (TextView) rootView.findViewById(R.id.movie_text);
                viewById1.setText(selectedMovie.getOverview());
                TextView viewById2 = (TextView) rootView.findViewById(R.id.movie_rating);
                viewById2.setText(selectedMovie.getVote_average() + "");
                TextView viewById3 = (TextView) rootView.findViewById(R.id.movie_release_date);
                viewById3.setText(selectedMovie.getRelease_date());
            }
            Log.d(TAG, "onCreateView: DONE WITH VIEWS");
            return rootView;
        }


        public class MovieByIdTask extends AsyncTask<String, Void, Movie> {

            @Override
            protected Movie doInBackground(String... params) {
                return HttpClient.getHttpClient().getMovieById(params[0]);
            }

            @Override
            protected void onPostExecute(Movie result) {
                Log.i(TAG, "onPostExecute: Movie to be assigned:" + result);
                selectedMovie = result;
                Log.i(TAG, "onPostExecute: Movie assigned");
            }
        }
    }
}

