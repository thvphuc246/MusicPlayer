package com.udacity.vinhphuc.musicplayer.presentation.cast;

import android.net.Uri;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.udacity.vinhphuc.musicplayer.data.model.Song;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.Constants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by VINH PHUC on 30/7/2018
 */
public class AppCastHelper {
    public static void startCasting(CastSession castSession, Song song) {

        String ipAddress = AppUtils.getIPAddress(true);
        URL baseUrl;
        try {
            baseUrl = new URL("http", ipAddress, Constants.CAST_SERVER_PORT, "" );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        String songUrl = baseUrl.toString() + "/song?id=" + song.id;
        String albumArtUrl = baseUrl.toString() + "/albumart?id=" + song.albumId;

        MediaMetadata musicMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

        musicMetadata.putString(MediaMetadata.KEY_TITLE, song.title);
        musicMetadata.putString(MediaMetadata.KEY_ARTIST, song.artistName);
        musicMetadata.putString(MediaMetadata.KEY_ALBUM_TITLE, song.albumName);
        musicMetadata.putInt(MediaMetadata.KEY_TRACK_NUMBER, song.trackNumber);
        musicMetadata.addImage(new WebImage(Uri.parse(albumArtUrl)));

        try {
            MediaInfo mediaInfo = new MediaInfo.Builder(songUrl)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("audio/mpeg")
                    .setMetadata(musicMetadata)
                    .setStreamDuration(song.duration)
                    .build();
            RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            remoteMediaClient.load(mediaInfo, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
