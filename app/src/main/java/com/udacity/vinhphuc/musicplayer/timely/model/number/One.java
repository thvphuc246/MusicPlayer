package com.udacity.vinhphuc.musicplayer.timely.model.number;

import com.udacity.vinhphuc.musicplayer.timely.model.core.Figure;

/**
 * Created by VINH PHUC on 1/8/2018
 */
public class One extends Figure {
    private static final float[][] POINTS = {
            {0.425414364640884f, 0.113259668508287f}, {0.425414364640884f, 0.113259668508287f}, {0.577348066298343f, 0.113259668508287f},
            {0.577348066298343f, 0.113259668508287f}, {0.577348066298343f, 0.113259668508287f}, {0.577348066298343f, 1f},
            {0.577348066298343f, 1f}, {0.577348066298343f, 1f}, {0.577348066298343f, 1f},
            {0.577348066298343f, 1f}, {0.577348066298343f, 1f}, {0.577348066298343f, 1f},
            {0.577348066298343f, 1f}
    };

    private static One INSTANCE = new One();

    protected One() {
        super(POINTS);
    }

    public static One getInstance() {
        return INSTANCE;
    }
}
