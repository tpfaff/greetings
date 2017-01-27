package com.wajumbie.robot.heart_screen;

import android.util.Log;

import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.geometry.TransformTime;
import com.aldebaran.qi.sdk.object.interaction.Human;
import com.aldebaran.qi.sdk.object.interaction.Interaction;
import com.wajumbie.robot.MyApp;
import com.wajumbie.robot.utils.TransformUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;


/**
 * Created by Tyler on 1/23/2017.
 */

public class MainPresenter extends BasePresenter<Object, MainActivity> implements MainPresenterContract {


    public static final String TAG = MainPresenter.class.getSimpleName();

    Random random = new Random();

    //todo populate for real robot
    List<String> greetings;
    List<String> animationIds;


    public MainPresenter() {
    }

    @Override
    public void startDetectingHumans() {
        final Actuation actuation = Actuation.get(MyApp.getShared().getApplicationContext());
        Interaction.get(MyApp.getShared().getApplicationContext()).setHumansAroundListener(new Interaction
                .HumansAroundListener() {
            @Override
            public void onHumansAroundChanged(List<Human> humansAround) {
                Log.v(TAG, humansAround.size() + " humans around");
                // signals callbacks are called from a qi thread
                displayHumansInfo(actuation, humansAround);
            }
        });
    }

    @Override
    public void greetHuman(Human human) {
        //todo stub for real robot
    }

    private void displayHumansInfo(Actuation actuation, List<Human> humans) {
        try {
            Frame robotFrame = actuation.robotFrame();
            int i = 0;
            for (Human human : humans) {
                greetHuman(human);
                Frame humanFrame = human.getHeadFrame();
                // currently, lastKnowTransform never returns (the future never finishes)
                TransformTime tf = humanFrame.lastKnownTransform(robotFrame).get();
                double distance = TransformUtils.distance(tf.getTransform());
                Log.v(TAG, "human " + i++ + " at " + distance);
            }
        } catch (ExecutionException e) {
            Log.e(TAG, "Cannot display humans infos", e);
        }
    }
}
