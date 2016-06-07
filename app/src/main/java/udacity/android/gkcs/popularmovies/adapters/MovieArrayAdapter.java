package udacity.android.gkcs.popularmovies.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import udacity.android.gkcs.popularmovies.R;
import udacity.android.gkcs.popularmovies.model.Movie;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    public MovieArrayAdapter(Activity context, int resource, List<Movie> movies) {
        super(context, resource, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_movie, parent, false);
        }
        String path = "http://image.tmdb.org/t/p/w185" + movie.getImage();
        Log.i(LOG_TAG, "getView: path --> " + path);
        ((TextView) convertView.findViewById(R.id.grid_movie_title)).setText(movie.getTitle());
        Picasso.with(getContext()).load(path).into((ImageView) convertView.findViewById(R.id.grid_movie_image));
        return convertView;
    }
}