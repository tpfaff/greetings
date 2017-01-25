package com.wajumbie.robot;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.interaction.Human;
import com.aldebaran.qi.sdk.object.interaction.Say;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Tyler on 1/24/2017.
 */

public class MockMainPresenter extends BasePresenter<List<Human>, MainActivity> implements MainPresenterContract {


    public static final String TAG = MockMainPresenter.class.getSimpleName();

    private String[] generalGreetings;
    private String[] weatherGreetings;
    private String[] timeSensitiveGreetings;


    private List<Integer> animationIds;

    Random greetingRandom = new Random();
    //    Random animationRandom = new Random(System.currentTimeMillis());
    //How often a mock human will be detected in milliseconds
    private final int HUMAN_DETECTION_DELAY = 9000;

    public MockMainPresenter(MainActivity mainActivity) {
        bindView(mainActivity);

        List<Human> mockHumans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Human mockHuman = new Human(QiContext.get(view()), null);
            mockHumans.add(mockHuman);
        }
        setModel(mockHumans);

        initGreetings();
        initAnimationFiles();
    }

    private void initGreetings() {
        generalGreetings = view().getResources().getStringArray(R.array.general_greetings);
        weatherGreetings = view().getResources().getStringArray(R.array.weather_greetings);
        timeSensitiveGreetings = view().getResources().getStringArray(R.array.time_sensitive_greetings);
    }

    private void initAnimationFiles() {
        animationIds = new ArrayList<>();
        animationIds.add(R.raw.hello_a001);
        animationIds.add(R.raw.hello_a002);
        animationIds.add(R.raw.hello_a003);
        animationIds.add(R.raw.hello_a004);
        animationIds.add(R.raw.hello_a005);
        animationIds.add(R.raw.hello_a006);
        animationIds.add(R.raw.hello_a007);
        animationIds.add(R.raw.hello_a008);
        animationIds.add(R.raw.hello_a009);
    }

    @Override
    public void startDetectingHumans() {
        Log.e(TAG, "startDetectingHumans");
        Log.e(TAG, "model size is " + model.size());

        //Emit mock humans at an interval
        //Use concatMap to respect order of arrival, or else timing will not work
        //as flatmap doesn't guarantee emission order, essentially nilling out the delay we are adding on

        //This is to simulate humans arriving in the store at an interval, since I don't have a robot :)
        Observable
                .fromIterable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Human, ObservableSource<Human>>() {
                    @Override
                    public ObservableSource<Human> apply(Human human) throws Exception {
                        return Observable.just(human).delay(HUMAN_DETECTION_DELAY, TimeUnit.MILLISECONDS);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "Couldn't emit a mock human!!!", throwable);
                    }
                })
                .subscribe(new Consumer<Human>() {
                    @Override
                    public void accept(Human human) throws Exception {
                        greetHuman(human);
                    }
                });
    }

    @Override
    public void greetHuman(Human human) {
        Log.d(TAG, "greetHuman");


        int randomAnimationIndex = greetingRandom.nextInt(10000);
        randomAnimationIndex %= animationIds.size();
        Animation animation = Animation.fromResources(view(), animationIds.get(randomAnimationIndex));
        Animate animate = new Animate(view());
        animate.run(animation);

        Say say = new Say(view());
        say.run(generalGreetings[randomAnimationIndex]);
    }
}
