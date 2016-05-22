package udacity.android.gkcs.popularmovies.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import udacity.android.gkcs.popularmovies.R;
import udacity.android.gkcs.popularmovies.model.Review;

public class ReviewArrayAdapter extends ArrayAdapter<Review> {
    private static final String TAG = MovieArrayAdapter.class.getSimpleName();

    public ReviewArrayAdapter(Activity context, int resource, List<Review> reviews) {
        super(context, resource, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review review = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_movie, parent, false);
        }
        Log.i(TAG, "getView: Setting up review view");
        ((TextView) convertView.findViewById(R.id.grid_movie_title)).setText(review.getAuthor());
        return convertView;
    }
}
