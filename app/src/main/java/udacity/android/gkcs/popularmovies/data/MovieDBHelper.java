package udacity.android.gkcs.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie";

    public MovieDBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieColumns.TABLE_NAME + " ( " +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                MovieColumns.POSTER_PATH + " VARCHAR(64), " +
                MovieColumns.OVERVIEW + " TEXT, " +
                MovieColumns.RELEASE_DATE + " VARCHAR(32), " +
                MovieColumns.TITLE + " VARCHAR(64), " +
                MovieColumns.POPULARITY + " VARCHAR(16)," +
                MovieColumns.VOTE_AVERAGE + " VARCHAR(16));";
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + FavouritesColumns.TABLE_NAME + " ( "
                + BaseColumns._ID + " INTEGER PRIMARY KEY FOREIGN KEY(" + BaseColumns._ID +
                ") REFERENCES " + MovieColumns.TABLE_NAME + "(" + BaseColumns._ID + ");";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
