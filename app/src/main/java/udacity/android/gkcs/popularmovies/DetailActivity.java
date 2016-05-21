package udacity.android.gkcs.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
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
        super.onCreateOptionsMenu(menu);
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
            if (intent != null) {
                selectedMovie = intent.getParcelableExtra("movie");
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
    }
}

