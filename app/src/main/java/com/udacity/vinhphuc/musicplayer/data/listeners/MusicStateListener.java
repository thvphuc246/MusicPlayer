package com.udacity.vinhphuc.musicplayer.data.listeners;

/**
 * Created by VINH PHUC on 30/7/2018
 */
public interface MusicStateListener {

    /**
     * Called when {@link com.udacity.vinhphuc.musicplayer.MusicService#REFRESH} is invoked
     */
    void restartLoader();

    /**
     * Called when {@link com.udacity.vinhphuc.musicplayer.MusicService#PLAYLIST_CHANGED} is invoked
     */
    void onPlaylistChanged();

    /**
     * Called when {@link com.udacity.vinhphuc.musicplayer.MusicService#META_CHANGED} is invoked
     */
    void onMetaChanged();

}
