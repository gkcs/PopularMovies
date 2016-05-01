package udacity.android.gkcs.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
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
        private static final Gson gson = new Gson();
        private Movie selectedMovie;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Log.d(TAG, "onCreateView: STARTED THE VIEWS");
            fillMovieData(rootView, getActivity().getIntent());
            Log.d(TAG, "onCreateView: DONE WITH VIEWS");
            return rootView;
        }

        private void fillMovieData(View rootView, Intent intent) {
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                selectedMovie = gson.fromJson(intent.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
                final String path = "http://image.tmdb.org/t/p/w185" + selectedMovie.getImage();
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(selectedMovie.getTitle());
                Picasso.with(getContext()).load(path).into((ImageView) rootView.findViewById(R.id.movie_image));
                ((TextView) rootView.findViewById(R.id.movie_text)).setText(selectedMovie.getOverview());
                ((TextView) rootView.findViewById(R.id.movie_rating)).setText(String.valueOf(selectedMovie.getVote_average()));
                ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(selectedMovie.getRelease_date());
            }
        }
    }
}

