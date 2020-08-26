package com.udacity.vinhphuc.musicplayer.utils.slidinguppanel;

/**
 * Created by VINH PHUC on 25/7/2018
 */

import android.view.View;

/**
 * Listener for monitoring events about sliding panes.
 */
public interface PanelSlideListener {
    /**
     * Called when a sliding pane's position changes.
     *
     * @param panel       The child view that was moved
     * @param slideOffset The new offset of this sliding pane within its range, from 0-1
     */
    void onPanelSlide(View panel, float slideOffset);

    /**
     * Called when a sliding panel becomes slid completely collapsed.
     *
     * @param panel The child view that was slid to an collapsed position
     */
    void onPanelCollapsed(View panel);

    /**
     * Called when a sliding panel becomes slid completely expanded.
     *
     * @param panel The child view that was slid to a expanded position
     */
    void onPanelExpanded(View panel);

    /**
     * Called when a sliding panel becomes anchored.
     *
     * @param panel The child view that was slid to a anchored position
     */
    void onPanelAnchored(View panel);

    /**
     * Called when a sliding panel becomes completely hidden.
     *
     * @param panel The child view that was slid to a hidden position
     */
    void onPanelHidden(View panel);
}
