package udacity.android.gkcs.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieColumns implements BaseColumns{
    public static final String POSTER_PATH = "poster_path";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String TITLE = "title";
    public static final String POPULARITY = "popularity";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String TABLE_NAME = "movie";
    public static final Uri CONTENT_URI =
            MovieProvider.BASE_CONTENT_URI.buildUpon().appendPath(MovieProvider.PATH_MOVIES).build();
}
