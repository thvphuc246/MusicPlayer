package com.udacity.vinhphuc.musicplayer.data.loaders;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.util.Log;

import com.udacity.vinhphuc.musicplayer.data.model.Playlist;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VINH PHUC on 29/7/2018
 */
public class PlaylistLoader {
    static ArrayList<Playlist> mPlaylistList;
    private static Cursor mCursor;

    public static List<Playlist> getPlaylists(Context context, boolean defaultIncluded) {

        mPlaylistList = new ArrayList<>();

        if (defaultIncluded)
            makeDefaultPlaylists(context);

        mCursor = makePlaylistCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(0);

                final String name = mCursor.getString(1);

                final int songCount = AppUtils.getSongCountForPlaylist(context, id);

                final Playlist playlist = new Playlist(id, name, songCount);

                mPlaylistList.add(playlist);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mPlaylistList;
    }

    private static void makeDefaultPlaylists(Context context) {
        final Resources resources = context.getResources();

        /* Last added list */
        final Playlist lastAdded = new Playlist(AppUtils.PlaylistType.LastAdded.mId,
                resources.getString(AppUtils.PlaylistType.LastAdded.mTitleId), -1);
        mPlaylistList.add(lastAdded);

        /* Recently Played */
        final Playlist recentlyPlayed = new Playlist(AppUtils.PlaylistType.RecentlyPlayed.mId,
                resources.getString(AppUtils.PlaylistType.RecentlyPlayed.mTitleId), -1);
        mPlaylistList.add(recentlyPlayed);

        /* Top Tracks */
        final Playlist topTracks = new Playlist(AppUtils.PlaylistType.TopTracks.mId,
                resources.getString(AppUtils.PlaylistType.TopTracks.mTitleId), -1);
        mPlaylistList.add(topTracks);

        /* Favourite Tracks */
        /*final Playlist favouriteTracks = new Playlist(AppUtils.PlaylistType.FavouriteTracks.mId,
                resources.getString(AppUtils.PlaylistType.FavouriteTracks.mTitleId), -1);
        mPlaylistList.add(favouriteTracks);*/
    }


    public static final Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(Playlists.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        PlaylistsColumns.NAME
                }, null, null, Playlists.DEFAULT_SORT_ORDER);
    }

    public static void deletePlaylists(Context context, long playlistId) {
        Uri localUri = Playlists.EXTERNAL_CONTENT_URI;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("_id IN (");
        localStringBuilder.append((playlistId));
        localStringBuilder.append(")");
        context.getContentResolver().delete(localUri, localStringBuilder.toString(), null);
    }
}
