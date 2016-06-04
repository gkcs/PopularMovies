package udacity.android.gkcs.popularmovies.data;

import android.net.Uri;

public class FavouritesColumns {
    public static final String TABLE_NAME = "FAVOURITES";
    public static final Uri CONTENT_URI =
            MovieProvider.BASE_CONTENT_URI.buildUpon().appendPath(MovieProvider.FAVOURITE_MOVIE_PATH).build();
}
