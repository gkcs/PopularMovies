package udacity.android.gkcs.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "udacity.android.gkcs.popularmovies";
    private static final UriMatcher uriMatcher = buildUriMatcher();
    public static final String PATH_MOVIES = "movie";
    public static final String FAVOURITE_MOVIE_PATH = "favorite";
    public static final int MOVIE_CODE = 100;
    public static final int FAVOURITE_MOVIE_CODE = 101;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private MovieDBHelper movieDBHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES, MOVIE_CODE);
        matcher.addURI(CONTENT_AUTHORITY, FAVOURITE_MOVIE_PATH, FAVOURITE_MOVIE_CODE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOVIE_CODE:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
            case FAVOURITE_MOVIE_CODE:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAVOURITE_MOVIE_PATH;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case FAVOURITE_MOVIE_CODE: {
                retCursor = movieDBHelper.getReadableDatabase().query(
                        FavouritesColumns.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_CODE: {
                retCursor = movieDBHelper.getReadableDatabase().query(
                        MovieColumns.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.i("Provider-query", "query: Cursor has " + retCursor.getCount() + " rows");
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case MOVIE_CODE: {
                long _id = db.insert(MovieColumns.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(MovieColumns.CONTENT_URI, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVOURITE_MOVIE_CODE: {
                long _id = db.insert(FavouritesColumns.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(FavouritesColumns.CONTENT_URI, _id);
                if (_id <= 0)
                    Log.d("Provider-insert", "Rows not affected " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (uriMatcher.match(uri)) {
            case MOVIE_CODE:
                rowsDeleted = db.delete(MovieColumns.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOURITE_MOVIE_CODE:
                rowsDeleted = db.delete(FavouritesColumns.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.i("Provider-delete:", "delete: No rows deleted");
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriMatcher.match(uri)) {
            case MOVIE_CODE:
                rowsUpdated = db.update(MovieColumns.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVOURITE_MOVIE_CODE:
                rowsUpdated = db.update(FavouritesColumns.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MOVIE_CODE:
                db.beginTransaction();
                int count = 0;
                try {
                    for (final ContentValues value : values) {
                        if (db.insert(MovieColumns.TABLE_NAME, null, value) != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDBHelper.close();
        super.shutdown();
    }
}
