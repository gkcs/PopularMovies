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
import udacity.android.gkcs.popularmovies.model.Trailer;

public class TrailerArrayAdapter extends ArrayAdapter<Trailer> {
    private static final String TAG = TrailerArrayAdapter.class.getSimpleName();

    public TrailerArrayAdapter(Activity context, int resource, List<Trailer> trailers) {
        super(context, resource, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Trailer trailer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        }
        Log.i(TAG, "getView: Setting up trailer view " + trailer.toString());
        ((TextView) convertView.findViewById(R.id.trailer_name)).setText(trailer.getName());
        return convertView;
    }
}
