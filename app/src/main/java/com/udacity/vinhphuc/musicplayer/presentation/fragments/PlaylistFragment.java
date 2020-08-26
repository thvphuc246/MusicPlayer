package com.udacity.vinhphuc.musicplayer.presentation.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.udacity.vinhphuc.musicplayer.R;
import com.udacity.vinhphuc.musicplayer.data.loaders.PlaylistLoader;
import com.udacity.vinhphuc.musicplayer.presentation.adapters.PlaylistAdapter;
import com.udacity.vinhphuc.musicplayer.data.model.Playlist;
import com.udacity.vinhphuc.musicplayer.presentation.dialogs.CreatePlaylistDialog;
import com.udacity.vinhphuc.musicplayer.utils.Constants;
import com.udacity.vinhphuc.musicplayer.utils.PreferencesUtility;
import com.udacity.vinhphuc.musicplayer.widgets.DividerItemDecoration;
import com.udacity.vinhphuc.musicplayer.widgets.MultiViewPager;
import com.udacity.vinhphuc.musicplayer.widgets.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by VINH PHUC on 26/7/2018
 */
public class PlaylistFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.playlistPager)
    MultiViewPager pager;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;
    private int playlistcount;
    private FragmentStatePagerAdapter adapter;
    private PreferencesUtility mPreferences;
    private boolean isGrid;
    private boolean isDefault;
    private boolean showAuto;
    private PlaylistAdapter mAdapter;

    private List<Playlist> playlists = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        isGrid = mPreferences.getPlaylistView() == Constants.PLAYLIST_VIEW_GRID;
        isDefault = mPreferences.getPlaylistView() == Constants.PLAYLIST_VIEW_DEFAULT;
        showAuto = mPreferences.showAutoPlaylist();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.playlists);

        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            initPager();
        } else {
            initRecyclerView();
        }

        return rootView;
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
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (showAuto) {
            menu.findItem(R.id.action_view_auto_playlists).setTitle(R.string.hide_auto_playlists);
        } else menu.findItem(R.id.action_view_auto_playlists).setTitle(R.string.show_auto_playlists);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_playlist:
                CreatePlaylistDialog.newInstance().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                return true;
            case R.id.menu_show_as_list:
                mPreferences.setPlaylistView(Constants.PLAYLIST_VIEW_LIST);
                isGrid = false;
                isDefault = false;
                initRecyclerView();
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setPlaylistView(Constants.PLAYLIST_VIEW_GRID);
                isGrid = true;
                isDefault = false;
                initRecyclerView();
                updateLayoutManager(2);
                return true;
            case R.id.menu_show_as_default:
                mPreferences.setPlaylistView(Constants.PLAYLIST_VIEW_DEFAULT);
                isDefault = true;
                initPager();
                return true;
            case R.id.action_view_auto_playlists:
                if (showAuto) {
                    showAuto = false;
                    mPreferences.setToggleShowAutoPlaylist(false);
                } else {
                    showAuto = true;
                    mPreferences.setToggleShowAutoPlaylist(true);
                }
                reloadPlaylists();
                getActivity().invalidateOptionsMenu();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTION_DELETE_PLAYLIST) {
            if (resultCode == Activity.RESULT_OK) {
                reloadPlaylists();
            }

        }
    }

    private void initPager() {
        pager.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setAdapter(null);
        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return playlistcount;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaylistPagerFragment.newInstance(position);
            }

        };
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
    }

    private void initRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        pager.setVisibility(View.GONE);
        setLayoutManager();
        mAdapter = new PlaylistAdapter(getActivity(), playlists);

        recyclerView.setAdapter(mAdapter);
        //to add spacing between cards
        if (getActivity() != null) {
            setItemDecoration();
        }
    }

    private void setLayoutManager() {
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setItemDecoration() {
        if (isGrid) {
            int spacingInPixels = getActivity()
                    .getResources()
                    .getDimensionPixelSize(R.dimen.spacing_card_album_grid);
            itemDecoration = new SpacesItemDecoration(spacingInPixels);
        } else {
            itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        }
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void updateLayoutManager(int column) {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.setAdapter(new PlaylistAdapter(getActivity(), PlaylistLoader.getPlaylists(getActivity(), showAuto)));
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        setItemDecoration();
    }

    public void reloadPlaylists() {
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            initPager();
        } else {
            initRecyclerView();
        }
    }

    public void updatePlaylists(final long id) {
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            adapter.notifyDataSetChanged();
            if (id != -1) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < playlists.size(); i++) {
                            long playlistid = playlists.get(i).id;
                            if (playlistid == id) {
                                pager.setCurrentItem(i);
                                break;
                            }
                        }
                    }
                }, 200);
            }

        } else {
            mAdapter.updateDataSet(playlists);
        }
    }
}
