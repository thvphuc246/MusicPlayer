package com.udacity.vinhphuc.musicplayer.widgets.desktop;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.udacity.vinhphuc.musicplayer.MusicService;
import com.udacity.vinhphuc.musicplayer.R;
import com.udacity.vinhphuc.musicplayer.utils.AppUtils;
import com.udacity.vinhphuc.musicplayer.utils.NavigationUtils;

/**
 * Created by VINH PHUC on 24/7/2018
 */
public class StandardWidget extends BaseWidget {

    @Override
    int getLayoutRes() {
        return R.layout.widget_standard;
    }

    @Override
    void onViewsUpdate(Context context, RemoteViews remoteViews, ComponentName serviceName, Bundle extras) {
        remoteViews.setOnClickPendingIntent(R.id.image_next, PendingIntent.getService(
                context,
                REQUEST_NEXT,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.NEXT_ACTION)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.image_prev, PendingIntent.getService(
                context,
                REQUEST_PREV,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.PREVIOUS_ACTION)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.image_playpause, PendingIntent.getService(
                context,
                REQUEST_PLAYPAUSE,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.TOGGLEPAUSE_ACTION)
                        .setComponent(serviceName),
                0
        ));

        if (extras != null) {
            String t = extras.getString("track");
            if (t != null) {
                remoteViews.setTextViewText(R.id.textView_title, t);
            }
            t = extras.getString("artist");
            ;
            if (t != null) {
                String album = extras.getString("album");
                ;
                if (!TextUtils.isEmpty(album)) {
                    t += " - " + album;
                }
                remoteViews.setTextViewText(R.id.textView_subtitle, t);
            }
            remoteViews.setImageViewResource(R.id.image_playpause,
                    extras.getBoolean("playing") ? R.drawable.ic_pause : R.drawable.ic_play);
            long albumId = extras.getLong("albumid");
            if (albumId != -1) {
                Bitmap artwork = ImageLoader.getInstance().loadImageSync(AppUtils.getAlbumArtUri(albumId).toString());
                if (artwork != null) {
                    remoteViews.setImageViewBitmap(R.id.imageView_cover, artwork);
                } else {
                    remoteViews.setImageViewResource(R.id.imageView_cover, R.drawable.ic_empty_music2);
                }
            }
        }
        remoteViews.setOnClickPendingIntent(R.id.imageView_cover, PendingIntent.getActivity(
                context,
                0,
                NavigationUtils.getNowPlayingIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));
    }
}
