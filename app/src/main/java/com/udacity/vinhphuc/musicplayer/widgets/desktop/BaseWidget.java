package com.udacity.vinhphuc.musicplayer.widgets.desktop;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.widget.RemoteViews;

import com.udacity.vinhphuc.musicplayer.MusicService;

/**
 * Created by VINH PHUC on 4/8/2018
 */
public abstract class BaseWidget extends AppWidgetProvider {

    protected static final int REQUEST_NEXT = 1;
    protected static final int REQUEST_PREV = 2;
    protected static final int REQUEST_PLAYPAUSE = 3;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        onUpdate(context, appWidgetManager, appWidgetIds, null);
    }

    private void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,Bundle extras){
        ComponentName serviceName = new ComponentName(context, MusicService.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), getLayoutRes());
        try {
            onViewsUpdate(context, remoteViews, serviceName, extras);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.startsWith("com.udacity.vinhphuc.musicplayer.")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), this.getClass().getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds, intent.getExtras());
        } else {
            super.onReceive(context, intent);
        }
    }

    abstract void onViewsUpdate(Context context, RemoteViews remoteViews, ComponentName serviceName, Bundle extras);

    abstract @LayoutRes
    int getLayoutRes();
}
