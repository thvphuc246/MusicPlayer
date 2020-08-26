package com.udacity.vinhphuc.musicplayer.timely.model.core;

/**
 * Created by VINH PHUC on 1/8/2018
 */
public abstract class Figure {
    public static final int NO_VALUE = -1;

    protected int pointsCount = NO_VALUE;

    //A chained sequence of points P0,P1,P2,P3/0,P1,P2,P3/0,...
    protected float[][] controlPoints = null;

    protected Figure(float[][] controlPoints) {
        this.controlPoints = controlPoints;
        this.pointsCount = (controlPoints.length + 2) / 3;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public float[][] getControlPoints() {
        return controlPoints;
    }
}
