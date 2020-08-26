package com.udacity.vinhphuc.musicplayer.data.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by VINH PHUC on 8/8/2018
 */

/**
 * This database stores the favourite songs marked by users.
 */
public class SongProvider extends ContentProvider {
    public static final int SONGS = 100;
    public static final int SONG_WITH_ID = 101;
    public static final String CONTENT_AUTHORITY = "com.udacity.vinhphuc.musicplayer";
    public static final String PATH_SONGS = "songs";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY)
                    .buildUpon()
                    .appendPath(PATH_SONGS).build();

    private static SongProvider sInstance = null;
    private MusicDB mMusicDatabase = null;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(
                CONTENT_AUTHORITY,
                PATH_SONGS,
                SONGS);
        uriMatcher.addURI(CONTENT_AUTHORITY,
                PATH_SONGS + "/#",
                SONG_WITH_ID);
        return uriMatcher;
    }

    public SongProvider() {

    }

    public SongProvider(final Context context) {
        mMusicDatabase = MusicDB.getInstance(context);
    }

    public static final synchronized SongProvider getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new SongProvider(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    public void onCreate(final SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(FavouriteTracksColumns.NAME);
        builder.append("(");
        builder.append(FavouriteTracksColumns.ID);
        builder.append(" INT UNIQUE);");

        db.execSQL(builder.toString());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteTracksColumns.NAME);
        onCreate(db);
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        if (mMusicDatabase != null) {
            db = mMusicDatabase.getReadableDatabase();
            switch (sUriMatcher.match(uri)) {
                case SONGS:
                    cursor = db.query(
                            FavouriteTracksColumns.NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri resultUri;

        switch (sUriMatcher.match(uri)) {
            case SONGS:
                long id = mMusicDatabase.getWritableDatabase().insert(
                        FavouriteTracksColumns.NAME,
                        null,
                        values);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {
        int numRoesDeleted;

        switch (sUriMatcher.match(uri)) {
            case SONG_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numRoesDeleted = mMusicDatabase.getWritableDatabase().delete(
                        FavouriteTracksColumns.NAME,
                        "song_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRoesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRoesDeleted;
    }

    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    public void deleteAll() {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.delete(FavouriteTracksColumns.NAME, null, null);
    }

    public interface FavouriteTracksColumns {
        /* Table name */
        String NAME = "favouritetracks";

        /* Song IDs column */
        String ID = "songid";
    }
}
