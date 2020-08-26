package com.udacity.vinhphuc.musicplayer.presentation.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.udacity.vinhphuc.musicplayer.MusicPlayer;
import com.udacity.vinhphuc.musicplayer.MusicService;
import com.udacity.vinhphuc.musicplayer.R;
import com.udacity.vinhphuc.musicplayer.data.listeners.MusicStateListener;
import com.udacity.vinhphuc.musicplayer.data.loaders.QueueLoader;
import com.udacity.vinhphuc.musicplayer.presentation.activities.BaseActivity;
import com.udacity.vinhphuc.musicplayer.presentation.adapters.BaseQueueAdapter;
import com.udacity.vinhphuc.musicplayer.presentation.adapters.SlidingQueueAdapter;
import com.udacity.vinhphuc.musicplayer.timely.TimelyView;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.Helpers;
import com.udacity.vinhphuc.musicplayer.utils.NavigationUtils;
import com.udacity.vinhphuc.musicplayer.utils.PreferencesUtility;
import com.udacity.vinhphuc.musicplayer.utils.SlideTrackSwitcher;
import com.udacity.vinhphuc.musicplayer.widgets.CircularSeekBar;
import com.udacity.vinhphuc.musicplayer.widgets.DividerItemDecoration;
import com.udacity.vinhphuc.musicplayer.widgets.PlayPauseButton;
import com.udacity.vinhphuc.musicplayer.widgets.PlayPauseDrawable;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.security.InvalidParameterException;

/**
 * Created by VINH PHUC on 1/8/2018
 */
public class BaseNowplayingFragment extends Fragment implements MusicStateListener {
    private MaterialIconView previous, next;
    private PlayPauseButton mPlayPause;
    private PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    private FloatingActionButton playPauseFloating;
    private View playPauseWrapper;

    private String ateKey;
    private int overflowcounter = 0;
    private TextView songtitle, songalbum, songartist, songduration, elapsedtime;
    private SeekBar mProgress;
    boolean fragmentPaused = false;

    private BaseQueueAdapter mAdapter;
    private SlidingQueueAdapter slidingQueueAdapter;

    /*private CircularSeekBar mCircularProgress;
    private TimelyView timelyView11, timelyView12, timelyView13, timelyView14, timelyView15;
    private TextView hourColon;
    public RecyclerView recyclerView;*/

    private int[] timeArr = new int[]{0, 0, 0, 0, 0};
    private Handler mElapsedTimeHandler;
    private boolean duetoplaypause = false;

    public ImageView albumart, shuffle, repeat;
    public int accentColor;

    //seekbar
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            if (mProgress != null) {
                mProgress.setProgress((int) position);
                if (elapsedtime != null && getActivity() != null)
                    elapsedtime.setText(AppUtils.makeShortTimeString(getActivity(), position / 1000));
            }
            overflowcounter--;
            int delay = 250; //not sure why this delay was so high before
            if (overflowcounter < 0 && !fragmentPaused) {
                overflowcounter++;
                mProgress.postDelayed(mUpdateProgress, delay); //delay
            }
        }
    };

    public Runnable mUpdateElapsedTime = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                String time = AppUtils.makeShortTimeString(getActivity(), MusicPlayer.position() / 1000);
                mElapsedTimeHandler.postDelayed(this, 600);
            }

        }
    };

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                    /*if (recyclerView != null && recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();*/
                }
            }, 200);

        }
    };

    private final View.OnClickListener mFLoatingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if(MusicPlayer.getCurrentTrack() == null) {
                Toast.makeText(getContext(), getString(R.string.now_playing_no_track_selected), Toast.LENGTH_SHORT).show();
            } else {
                playPauseDrawable.transformToPlay(true);
                playPauseDrawable.transformToPause(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                        /*if (recyclerView != null && recyclerView.getAdapter() != null)
                            recyclerView.getAdapter().notifyDataSetChanged();*/
                    }
                }, 250);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ateKey = Helpers.getATEKey(getActivity());
        accentColor = Config.accentColor(getActivity(), ateKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.now_playing, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_go_to_album:
                NavigationUtils.goToAlbum(getContext(), MusicPlayer.getCurrentAlbumId());
                break;
            case R.id.menu_go_to_artist:
                NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                break;
            case R.id.action_lyrics:
                NavigationUtils.goToLyrics(getContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPaused = false;
        if (mProgress != null)
            mProgress.postDelayed(mUpdateProgress, 10);

        /*if (mCircularProgress != null)
            mCircularProgress.postDelayed(mUpdateCircularProgress, 10);*/
    }

    public void setSongDetails(View view) {

        albumart = (ImageView) view.findViewById(R.id.album_art);
        shuffle = (ImageView) view.findViewById(R.id.shuffle);
        repeat = (ImageView) view.findViewById(R.id.repeat);
        next = (MaterialIconView) view.findViewById(R.id.next);
        previous = (MaterialIconView) view.findViewById(R.id.previous);
        mPlayPause = (PlayPauseButton) view.findViewById(R.id.playpause);
        playPauseFloating = (FloatingActionButton) view.findViewById(R.id.playpausefloating);
        playPauseWrapper = view.findViewById(R.id.playpausewrapper);

        songtitle = (TextView) view.findViewById(R.id.song_title);
        songalbum = (TextView) view.findViewById(R.id.song_album);
        songartist = (TextView) view.findViewById(R.id.song_artist);
        songduration = (TextView) view.findViewById(R.id.song_duration);
        elapsedtime = (TextView) view.findViewById(R.id.song_elapsed_time);

        mProgress = (SeekBar) view.findViewById(R.id.song_progress);

        songtitle.setSelected(true);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
        if (mPlayPause != null && getActivity() != null) {
            mPlayPause.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }

        if (playPauseFloating != null) {
            playPauseDrawable.setColorFilter(AppUtils.getBlackWhiteColor(accentColor), PorterDuff.Mode.MULTIPLY);
            playPauseFloating.setImageDrawable(playPauseDrawable);
            if (MusicPlayer.isPlaying())
                playPauseDrawable.transformToPause(false);
            else playPauseDrawable.transformToPlay(false);
        }

        setSongDetails();

    }

    private void setSongDetails() {
        updateSongDetails();

        /*if (recyclerView != null)
            setQueueSongs();*/
        setQueueSongs();

        setSeekBarListener();

        if (next != null) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.next();
                            notifyPlayingDrawableChange();
                        }
                    }, 200);

                }
            });
        }
        if (previous != null) {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.previous(getActivity(), false);
                            notifyPlayingDrawableChange();
                        }
                    }, 200);

                }
            });
        }

        if (playPauseWrapper != null)
            playPauseWrapper.setOnClickListener(mButtonListener);

        if (playPauseFloating != null)
            playPauseFloating.setOnClickListener(mFLoatingButtonListener);

        updateShuffleState();
        updateRepeatState();

    }

    public void updateShuffleState() {
        if (shuffle != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);

            if (getActivity() != null) {
                if (MusicPlayer.getShuffleMode() == 0) {
                    builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                } else builder.setColor(Config.accentColor(getActivity(), ateKey));
            }

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleShuffle();
                    updateShuffleState();
                    updateRepeatState();
                }
            });
        }
    }

    public void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setSizeDp(30);

            if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_NONE) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_CURRENT) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                builder.setColor(Config.accentColor(getActivity(), ateKey));
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_ALL) {
                builder.setColor(Config.accentColor(getActivity(), ateKey));
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            }


            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleRepeat();
                    updateRepeatState();
                    updateShuffleState();
                }
            });
        }
    }

    private void setSeekBarListener() {
        if (mProgress != null)
            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        MusicPlayer.seek((long) i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
    }

    public void updateSongDetails() {
        //do not reload image if it was a play/pause change
        if (!duetoplaypause) {
            if (albumart != null) {
                ImageLoader.getInstance().displayImage(AppUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .showImageOnFail(R.drawable.ic_empty_music2)
                                .build(), new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                doAlbumArtStuff(loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
                                doAlbumArtStuff(failedBitmap);
                            }

                        });
            }
            if (songtitle != null && MusicPlayer.getTrackName() != null) {
                songtitle.setText(MusicPlayer.getTrackName());
                if(MusicPlayer.getTrackName().length() <= 23){
                    songtitle.setTextSize(25);
                }
                else if(MusicPlayer.getTrackName().length() >= 30){
                    songtitle.setTextSize(18);
                }
                else{
                    songtitle.setTextSize(18 + (MusicPlayer.getTrackName().length() - 24));
                }
                Log.v("BaseNowPlayingFrag", "Title Text Size: " + songtitle.getTextSize());
            }
            if (songartist != null) {
                songartist.setText(MusicPlayer.getArtistName());
                songartist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationUtils.goToArtist(getContext(), MusicPlayer.getCurrentArtistId());
                    }
                });
            }
            if (songalbum != null)
                songalbum.setText(MusicPlayer.getAlbumName());

        }
        duetoplaypause = false;

        if (mPlayPause != null)
            updatePlayPauseButton();

        if (playPauseFloating != null)
            updatePlayPauseFloatingButton();

        if (songduration != null && getActivity() != null)
            songduration.setText(AppUtils.makeShortTimeString(getActivity(), MusicPlayer.duration() / 1000));

        if (mProgress != null) {
            mProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateProgress != null) {
                mProgress.removeCallbacks(mUpdateProgress);
            }
            mProgress.postDelayed(mUpdateProgress, 10);
        }
    }

    public void setQueueSongs() {
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //load queue songs in asynctask
        if (getActivity() != null)
            new loadQueueSongs().execute("");

    }

    public void updatePlayPauseButton() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
        }
    }

    public void updatePlayPauseFloatingButton() {
        if (MusicPlayer.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
    }

    public void notifyPlayingDrawableChange() {
        int position = MusicPlayer.getQueuePosition();
        BaseQueueAdapter.currentlyPlayingPosition = position;
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        updateSongDetails();

        /*if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();*/
    }

    public void setMusicStateListener() {
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    }

    public void doAlbumArtStuff(Bitmap loadedImage) {

    }

    public void changeDigit(TimelyView tv, int end) {
        ObjectAnimator obja = tv.animate(end);
        obja.setDuration(400);
        obja.start();
    }

    public void changeDigit(TimelyView tv, int start, int end) {
        try {
            ObjectAnimator obja = tv.animate(start, end);
            obja.setDuration(400);
            obja.start();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }

    protected void initGestures(View v) {
        if (PreferencesUtility.getInstance(v.getContext()).isGesturesEnabled()) {
            new SlideTrackSwitcher() {
                @Override
                public void onSwipeBottom() {
                    getActivity().finish();
                }
            }.attach(v);
        }
    }

    private class loadQueueSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null) {
                mAdapter = new BaseQueueAdapter((AppCompatActivity) getActivity(), QueueLoader.getQueueSongs(getActivity()));
                return "Executed";
            } else return null;
        }

        @Override
        protected void onPostExecute(String result) {
            /*if (result != null) {
                recyclerView.setAdapter(mAdapter);
                if (getActivity() != null)
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                recyclerView.scrollToPosition(MusicPlayer.getQueuePosition() - 1);
            }*/

        }

        @Override
        protected void onPreExecute() {
        }
    }
}
