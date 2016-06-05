package udacity.android.gkcs.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavouritesColumns implements BaseColumns {
    public static final String TABLE_NAME = "favourite";
    public static final Uri CONTENT_URI =
            MovieProvider.BASE_CONTENT_URI.buildUpon().appendPath(MovieProvider.FAVOURITE_MOVIE_PATH).build();
}
