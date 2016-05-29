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
    private static final String TAG = ReviewArrayAdapter.class.getSimpleName();

    public ReviewArrayAdapter(Activity context, int resource, List<Review> reviews) {
        super(context, resource, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Review review = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_review, parent, false);
        }
        Log.i(TAG, "getView: Setting up review view " + review.toString());
        ((TextView) convertView.findViewById(R.id.review_author)).setText(review.getAuthor());
        ((TextView) convertView.findViewById(R.id.review_content)).setText(review.getContent());
        return convertView;
    }
}
