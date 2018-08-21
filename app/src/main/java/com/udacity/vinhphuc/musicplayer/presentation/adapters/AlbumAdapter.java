package com.udacity.vinhphuc.musicplayer.presentation.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.udacity.vinhphuc.musicplayer.R;
import com.udacity.vinhphuc.musicplayer.data.model.Album;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.Helpers;
import com.udacity.vinhphuc.musicplayer.utils.NavigationUtils;
import com.udacity.vinhphuc.musicplayer.utils.PreferencesUtility;

import java.util.List;

/**
 * Created by VINH PHUC on 4/8/2018
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemHolder> {
    private List<Album> arraylist;
    private Activity mContext;
    private boolean isGrid;

    public AlbumAdapter(Activity context, List<Album> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isAlbumsInGrid();

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_list, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        Album localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);

        ImageLoader.getInstance().displayImage(AppUtils.getAlbumArtUri(localItem.id).toString(), itemHolder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (isGrid) {
                            new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    if (swatch != null) {
                                        int color = swatch.getRgb();
                                        itemHolder.footer.setBackgroundColor(color);
                                        int textColor = AppUtils.getBlackWhiteColor(swatch.getTitleTextColor());
                                        itemHolder.title.setTextColor(textColor);
                                        itemHolder.artist.setTextColor(textColor);
                                    } else {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            int color = mutedSwatch.getRgb();
                                            itemHolder.footer.setBackgroundColor(color);
                                            int textColor = AppUtils.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                            itemHolder.title.setTextColor(textColor);
                                            itemHolder.artist.setTextColor(textColor);
                                        }
                                    }


                                }
                            });
                        }

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (isGrid) {
                            itemHolder.footer.setBackgroundColor(0);
                            if (mContext != null) {
                                int textColorPrimary = Config.textColorPrimary(mContext, Helpers.getATEKey(mContext));
                                itemHolder.title.setTextColor(textColorPrimary);
                                itemHolder.artist.setTextColor(textColorPrimary);
                            }
                        }
                    }
                });

        if (AppUtils.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void updateDataSet(List<Album> arraylist) {
        this.arraylist = arraylist;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, artist;
        protected ImageView albumArt;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_title);
            this.artist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtils.navigateToAlbum(mContext, arraylist.get(getAdapterPosition()).id,
                    new Pair<View, String>(albumArt, "transition_album_art" + getAdapterPosition()));
        }

    }
}
