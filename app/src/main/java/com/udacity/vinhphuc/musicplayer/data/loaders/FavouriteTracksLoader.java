package com.udacity.vinhphuc.musicplayer.data.loaders;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.udacity.vinhphuc.musicplayer.data.model.Song;
import com.udacity.vinhphuc.musicplayer.data.providers.SongProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VINH PHUC on 9/8/2018
 */
public class FavouriteTracksLoader extends SongLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private static Cursor mCursor;

    private static long mPlaylistID;
    private static Context mContext;

    public static List<Song> getSongsInPlaylist(Context context, long playlistID) {
        ArrayList<Song> mSongList = new ArrayList<>();

        mContext = context;
        mPlaylistID = playlistID;

        mCursor = makePlaylistSongCursor(mPlaylistID);

        if (mCursor != null) {

            if (mCursor.moveToFirst()) {
                final int playOrderCol = mCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAY_ORDER);

                int lastPlayOrder = -1;
                do {
                    int playOrder = mCursor.getInt(playOrderCol);
                    if (playOrder == lastPlayOrder) {
                        break;
                    }
                    lastPlayOrder = playOrder;
                } while (mCursor.moveToNext());
            }
        }

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID));

                final String songName = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));

                final String artist = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));

                final long albumId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID));

                final long artistId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID));

                final String album = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM));

                final long duration = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));

                final int durationInSecs = (int) duration / 1000;

                final int tracknumber = mCursor.getInt(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK));

                final Song song = new Song(id, albumId, artistId, songName, artist, album, durationInSecs, tracknumber);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    private static void cleanupPlaylist(final Context context, final long playlistId,
                                        final Cursor cursor) {
        final int idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        //final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        final Uri uri = SongProvider.CONTENT_URI;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newDelete(uri).build());

        final int YIELD_FREQUENCY = 100;

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                final ContentProviderOperation.Builder builder =
                        ContentProviderOperation.newInsert(uri)
                                .withValue(MediaStore.Audio.Playlists.Members.PLAY_ORDER, cursor.getPosition())
                                .withValue(MediaStore.Audio.Playlists.Members.AUDIO_ID, cursor.getLong(idCol));

                if ((cursor.getPosition() + 1) % YIELD_FREQUENCY == 0) {
                    builder.withYieldAllowed(true);
                }
                ops.add(builder.build());
            } while (cursor.moveToNext());
        }

        try {
            context.getContentResolver().applyBatch(MediaStore.AUTHORITY, ops);
        } catch (RemoteException e) {
        } catch (OperationApplicationException e) {
        }
    }

    public static final Cursor makePlaylistSongCursor(final Long playlistID) {
        final StringBuilder mSelection = new StringBuilder();
        mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        return mContext.getContentResolver().query(
                SongProvider.CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Playlists.Members._ID,
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.ARTIST_ID,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.AudioColumns.TRACK,
                        MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                }, mSelection.toString(), null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(mContext) {

            @Override
            protected void onStartLoading() {
                if (mCursor != null) {
                    deliverResult(mCursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return mContext.getContentResolver().query(SongProvider.CONTENT_URI,
                            null,
                            null,
                            null,
                            SongProvider.FavouriteTracksColumns.ID);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
