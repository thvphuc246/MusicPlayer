package com.udacity.vinhphuc.musicplayer.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.vinhphuc.musicplayer.R;

/**
 * Created by VINH PHUC on 1/8/2018
 */
public class NowPlayingFragment extends BaseNowplayingFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_now_playing, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        initGestures(rootView.findViewById(R.id.album_art));

        return rootView;
    }
}
