package com.udacity.vinhphuc.musicplayer.permissions;

/**
 * Created by VINH PHUC on 30/7/2018
 */
public interface PermissionCallback {
    void permissionGranted();

    void permissionRefused();
}
