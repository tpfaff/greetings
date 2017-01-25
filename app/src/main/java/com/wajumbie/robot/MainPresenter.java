package com.wajumbie.robot;

import android.util.Log;

import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.geometry.TransformTime;
import com.aldebaran.qi.sdk.object.interaction.Human;
import com.aldebaran.qi.sdk.object.interaction.Interaction;
import com.wajumbie.robot.utils.VectorUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Tyler on 1/23/2017.
 */

public class MainPresenter extends BasePresenter<Object, MainActivity> implements MainPresenterContract {


    public static final String TAG = MainPresenter.class.getSimpleName();

    public MainPresenter(MainActivity mainActivity) {
        bindView(mainActivity);
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

    }

    private void displayHumansInfo(Actuation actuation, List<Human> humans) {
        try {
            Frame robotFrame = actuation.robotFrame();
            int i = 0;
            for (Human human : humans) {
                Frame humanFrame = human.getHeadFrame();
                // currently, lastKnowTransform never returns (the future never finishes)
                TransformTime tf = humanFrame.lastKnownTransform(robotFrame).get();
                double distance = VectorUtils.distance(tf.getTransform());
                Log.v(TAG, "human " + i++ + " at " + distance);
            }
        } catch (ExecutionException e) {
            Log.e(TAG, "Cannot display humans infos", e);
        }
    }
}
