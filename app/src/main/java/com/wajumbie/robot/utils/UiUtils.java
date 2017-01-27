package com.wajumbie.robot.utils;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Tyler on 1/26/2017.
 */

public class UiUtils {

    /***
     * Show fullscreen/immersive mode
     */
    public static void showImmersiveMode(AppCompatActivity activity) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        activity
                .getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
