package com.udacity.vinhphuc.musicplayer.presentation.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.framework.CastSession;
import com.udacity.vinhphuc.musicplayer.MusicPlayer;
import com.udacity.vinhphuc.musicplayer.data.model.Song;
import com.udacity.vinhphuc.musicplayer.presentation.activities.BaseActivity;
import com.udacity.vinhphuc.musicplayer.presentation.cast.AppCastHelper;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.NavigationUtils;

import java.util.List;

/**
 * Created by VINH PHUC on 30/7/2018
 */
public class BaseSongAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View view) {
            super(view);
        }
    }

    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final AppUtils.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {

        if (context instanceof BaseActivity) {
            CastSession castSession = ((BaseActivity) context).getCastSession();
            if (castSession != null) {
                navigateNowPlaying = false;
                AppCastHelper.startCasting(castSession, currentSong);
            } else {
                MusicPlayer.playAll(context, list, position, -1, AppUtils.IdType.NA, false);
            }
        } else {
            MusicPlayer.playAll(context, list, position, -1, AppUtils.IdType.NA, false);
        }

        if (navigateNowPlaying) {
            NavigationUtils.navigateToNowplaying(context, true);
        }


    }

    public void removeSongAt(int i){}

    public void updateDataSet(List<Song> arraylist) {}
}
