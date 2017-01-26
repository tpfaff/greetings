package com.wajumbie.robot.heart_screen;

import com.aldebaran.qi.sdk.object.interaction.Human;

/**
 * Created by Tyler on 1/24/2017.
 */

public interface MainPresenterContract {
    void startDetectingHumans();

    void greetHuman(Human human);
}
