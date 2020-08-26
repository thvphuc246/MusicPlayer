package com.udacity.vinhphuc.musicplayer.presentation.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.appthemeengine.ATE;
import com.udacity.vinhphuc.musicplayer.MusicPlayer;
import com.udacity.vinhphuc.musicplayer.R;
import com.udacity.vinhphuc.musicplayer.data.loaders.ArtistLoader;
import com.udacity.vinhphuc.musicplayer.data.loaders.ArtistSongLoader;
import com.udacity.vinhphuc.musicplayer.data.model.Artist;
import com.udacity.vinhphuc.musicplayer.data.model.Song;
import com.udacity.vinhphuc.musicplayer.presentation.adapters.ArtistSongAdapter;
import com.udacity.vinhphuc.musicplayer.presentation.dialogs.AddPlaylistDialog;
import com.udacity.vinhphuc.musicplayer.utils.ATEUtils;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.Constants;
import com.udacity.vinhphuc.musicplayer.utils.Helpers;
import com.udacity.vinhphuc.musicplayer.utils.ImageUtils;

import java.util.List;

/**
 * Created by VINH PHUC on 1/8/2018
 */
public class ArtistDetailFragment extends Fragment {
    private long artistID = -1;
    private ImageView artistArt;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private boolean largeImageLoaded = false;
    private int primaryColor = -1;
    private ArtistSongAdapter mAdapter;

    public static ArtistDetailFragment newInstance(long id, boolean useTransition, String transitionName) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_artist_detail, container, false);

        artistArt = (ImageView) rootView.findViewById(R.id.artist_art);

        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);

        if (getArguments().getBoolean("transition")) {
            artistArt.setTransitionName(getArguments().getString("transition_name"));
        }

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setupToolbar();
        setUpArtistDetails();

        getChildFragmentManager().beginTransaction().replace(R.id.container, ArtistMusicFragment.newInstance(artistID)).commit();


        return rootView;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpArtistDetails() {

        final Artist artist = ArtistLoader.getArtist(getActivity(), artistID);
        List<Song> songList = ArtistSongLoader.getSongsForArtist(getActivity(), artistID);
        mAdapter = new ArtistSongAdapter(getActivity(), songList, artistID);

        collapsingToolbarLayout.setTitle(artist.name);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artist_detail, menu);
        if (getActivity() != null)
            ATE.applyMenu(getActivity(), "dark_theme", menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popup_song_addto_queue:
                MusicPlayer.addToQueue(getContext(), mAdapter.getSongIds(), -1, AppUtils.IdType.NA);
                break;
            case R.id.popup_song_addto_playlist:
                AddPlaylistDialog.newInstance(mAdapter.getSongIds()).show(getActivity().getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        if (primaryColor != -1 && getActivity() != null) {
            collapsingToolbarLayout.setContentScrimColor(primaryColor);
            String ateKey = Helpers.getATEKey(getActivity());
            ATEUtils.setStatusBarColor(getActivity(), ateKey, primaryColor);
        }

    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null && !largeImageLoaded) {
                artistArt.setImageDrawable(result);
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
