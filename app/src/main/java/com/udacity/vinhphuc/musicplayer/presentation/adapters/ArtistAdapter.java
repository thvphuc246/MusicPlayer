package com.udacity.vinhphuc.musicplayer.presentation.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.vinhphuc.musicplayer.R;
import com.udacity.vinhphuc.musicplayer.data.model.Artist;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.NavigationUtils;
import com.udacity.vinhphuc.musicplayer.utils.PreferencesUtility;
import com.udacity.vinhphuc.musicplayer.widgets.BubbleTextGetter;

import java.util.List;

/**
 * Created by VINH PHUC on 4/8/2018
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> implements BubbleTextGetter {

    private List<Artist> arraylist;
    private Activity mContext;
    private boolean isGrid;

    public ArtistAdapter(Activity context, List<Artist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistInGrid();
    }

    public static int getOpaqueColor(@ColorInt int paramInt) {
        return 0xFF000000 | paramInt;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        final Artist localItem = arraylist.get(i);

        itemHolder.name.setText(localItem.name);
        String albumNmber = AppUtils.makeLabel(mContext, R.plurals.Nalbums, localItem.albumCount);
        String songCount = AppUtils.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount);
        itemHolder.albums.setText(AppUtils.makeCombinedString(mContext, albumNmber, songCount));

        if (AppUtils.isLollipop())
            itemHolder.artistImage.setTransitionName("transition_artist_art" + i);

    }

    @Override
    public long getItemId(int position) {
        return arraylist.get(position).id;
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    @Override
    public String getTextToShowInBubble(final int pos) {
        if (arraylist == null || arraylist.size() == 0)
            return "";
        return Character.toString(arraylist.get(pos).name.charAt(0));
    }

    public void updateDataSet(List<Artist> arrayList) {
        this.arraylist = arrayList;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView name, albums;
        protected ImageView artistImage;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.artist_name);
            this.albums = (TextView) view.findViewById(R.id.album_song_count);
            this.artistImage = (ImageView) view.findViewById(R.id.artistImage);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtils.navigateToArtist(mContext, arraylist.get(getAdapterPosition()).id,
                    new Pair<View, String>(artistImage, "transition_artist_art" + getAdapterPosition()));
        }

    }
}
