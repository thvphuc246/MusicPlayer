package com.udacity.vinhphuc.musicplayer.presentation.activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afollestad.appthemeengine.ATEActivity;
import com.udacity.vinhphuc.musicplayer.utils.Helpers;

/**
 * Created by VINH PHUC on 4/8/2018
 */
public class BaseThemedActivity extends ATEActivity {
    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
