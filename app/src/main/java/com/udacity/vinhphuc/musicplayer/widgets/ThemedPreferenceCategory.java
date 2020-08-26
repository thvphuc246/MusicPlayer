package com.udacity.vinhphuc.musicplayer.widgets;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.udacity.vinhphuc.musicplayer.utils.Helpers;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by VINH PHUC on 24/7/2018
 */
public class ThemedPreferenceCategory extends PreferenceCategory {
    private Context context;

    @BindView(android.R.id.title)
    TextView titleView;

    public ThemedPreferenceCategory(Context context) {
        super(context);
        this.context = context;
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ButterKnife.bind(this, view);

        titleView.setTextColor(Config.accentColor(context, Helpers.getATEKey(context)));
    }
}
