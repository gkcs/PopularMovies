package udacity.android.gkcs.popularmovies.data;

import android.net.Uri;

public class MovieColumns {
    public static final String POSTER_PATH = "POSTER_PATH";
    public static final String OVERVIEW = "OVERVIEW";
    public static final String RELEASE_DATE = "RELEASE_DATE";
    public static final String TITLE = "TITLE";
    public static final String POPULARITY = "POPULARITY";
    public static final String VOTE_AVERAGE = "VOTE_AVERAGE";
    public static final String TABLE_NAME = "MOVIE";
    public static final Uri CONTENT_URI =
            MovieProvider.BASE_CONTENT_URI.buildUpon().appendPath(MovieProvider.PATH_MOVIES).build();
}
