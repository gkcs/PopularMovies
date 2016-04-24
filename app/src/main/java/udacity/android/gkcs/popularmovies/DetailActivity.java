package udacity.android.gkcs.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        setContentView(R.layout.movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Movie selectedMovie;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.movie_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                final String movie_id = intent.getStringExtra(Intent.EXTRA_TEXT);
                /**
                 * original title
                 movie poster image thumbnail
                 A plot synopsis (called overview in the api)
                 user rating (called vote_average in the api)
                 release date
                 */
                new MovieByIdTask().execute();
                ((TextView) rootView.findViewById(R.id.movie_text))
                        .setText(selectedMovie.getTitle());
                Picasso.with(getContext()).load(selectedMovie.getImage())
                        .into(((ImageView) rootView.findViewById(R.id.movie_image)));
                ((TextView) rootView.findViewById(R.id.movie_text))
                        .setText(selectedMovie.getOverview());
                ((TextView) rootView.findViewById(R.id.movie_text))
                        .setText(String.valueOf(selectedMovie.getVote_average()));
                ((TextView) rootView.findViewById(R.id.movie_text))
                        .setText(selectedMovie.getRelease_date());
            }

            return rootView;
        }


        public class MovieByIdTask extends AsyncTask<String, Void, Movie> {

            @Override
            protected Movie doInBackground(String... params) {
                return HttpClient.getHttpClient().getMovieById(params[0]);
            }

            @Override
            protected void onPostExecute(Movie result) {
                selectedMovie = result;
            }
        }
    }
}

