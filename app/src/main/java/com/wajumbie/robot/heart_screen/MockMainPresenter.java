package com.wajumbie.robot.heart_screen;

import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.FutureFunction;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.interaction.Human;
import com.aldebaran.qi.sdk.object.interaction.Say;
import com.wajumbie.robot.R;
import com.wajumbie.robot.models.WEATHER_TYPE;
import com.wajumbie.robot.models.WeatherResponse;
import com.wajumbie.robot.network.Api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    private List<String> greetings;
    private int mockLocation = 94103;

    private List<Integer> animationIds;

    private Random random = new Random();

    //How often a mock human will be detected in milliseconds
    private final int HUMAN_DETECTION_DELAY = 10000;


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
        greetings = new ArrayList<>(Arrays.asList(view().getResources().getStringArray(R.array.general_greetings)));
        addWeatherGreetingForZip(mockLocation);
        addMondayGreeting();
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
        Log.d(TAG, "startDetectingHumans");


        //Emit mock humans at an interval
        //Use concatMap to respect order of arrival, or else timing will not work
        //as flatmap doesn't guarantee emission order, essentially nilling out the delay we are adding on

        //This is to simulate humans arriving in the store at an interval, since I don't have a robot :)
        Observable
                .fromIterable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Function<Human, ObservableSource<Human>>() {
                    @Override
                    public ObservableSource<Human> apply(Human human) throws Exception {
                        //delay operates by default on the computation thread
                        //so we must explicitly deliver the result on the main thread
                        //and we can't just observeOn(AndroidSchedulers.mainThread()) up above
                        return Observable.just(human).delay(HUMAN_DETECTION_DELAY, TimeUnit.MILLISECONDS,/*explicit main thread*/AndroidSchedulers.mainThread());
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "Couldn't emit a mock human!!!", throwable);
                    }
                })
                .doOnNext(new Consumer<Human>() {
                    @Override
                    public void accept(Human human) throws Exception {
                        //pepper is excited to see someone
                        view().showHeartbeat();
                        greetHuman(human);
                    }
                })
                .subscribe();
    }

    @Override
    public void greetHuman(Human human) {
        Log.d(TAG, "greetHuman");
        int randomAnimationIndex = random.nextInt(greetings.size());
        Animation animation = Animation.fromResources(view(), animationIds.get(randomAnimationIndex % animationIds.size()));
        Animate animate = new Animate(view());

        animate.run(animation).andThen(new FutureFunction<Void, Void>() {
            @Override
            public Future<Void> execute(Future<Void> future) throws Exception {
                //peppers excitement has passed
                view().hideHeartbeat();
                return null;
            }
        });

        Say say = new Say(view());
        Log.e(TAG, "greetings size is " + greetings.size());
        say.run(greetings.get(randomAnimationIndex));
    }

    /***
     * Get the weather and add a weather relevant greeting
     * @param zip
     */
    private void addWeatherGreetingForZip(int zip) {
        Api.getWeatherForZip(zip)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "Couldn't get weather", throwable);
                    }
                })
                .map(new Function<WeatherResponse, WEATHER_TYPE>() {
                    @Override
                    public WEATHER_TYPE apply(WeatherResponse weatherResponse) throws Exception {
                        if (weatherResponse.getWeather().size() > 0) {
                            String weatherDescription = weatherResponse.getWeather().get(0).getDescription();
                            if (weatherDescription.toLowerCase().contains("rain")) {
                                Log.i(TAG, "It's rain");
                                return WEATHER_TYPE.RAIN;
                            } else if (weatherDescription.toLowerCase().contains("cloud")) {
                                Log.i(TAG, "It's cloudy");
                                return WEATHER_TYPE.CLOUDY;
                            } else if (weatherDescription.toLowerCase().contains("clear")) {
                                Log.i(TAG, "It's clear");
                                return WEATHER_TYPE.SHINE;
                            }
                        }

                        return null;
                    }
                })
                .doOnNext(new Consumer<WEATHER_TYPE>() {
                    @Override
                    public void accept(WEATHER_TYPE weather_type) throws Exception {
                        if (weather_type != null) {
                            switch (weather_type) {
                                case RAIN:
                                    Log.e(TAG, "ADDING GREETING TO LIST WITH SIZE " + greetings.size());
                                    greetings.add(view().getString(R.string.weather_raining));
                                    greetings.remove(view().getString(R.string.weather_nice_day));
                                    greetings.remove(view().getString(R.string.weather_cloudy));
                                    break;
                                case SHINE:
                                    Log.e(TAG, "ADDING GREETING TO LIST WITH SIZE " + greetings.size());
                                    greetings.add(view().getString(R.string.weather_nice_day));
                                    greetings.remove(view().getString(R.string.weather_cloudy));
                                    greetings.remove(view().getString(R.string.weather_raining));
                                    break;
                                case CLOUDY:
                                    Log.e(TAG, "ADDING GREETING TO LIST WITH SIZE " + greetings.size());
                                    greetings.add(view().getString(R.string.weather_cloudy));
                                    greetings.remove(view().getString(R.string.weather_nice_day));
                                    greetings.remove(view().getString(R.string.weather_raining));
                                    break;
                                default:
                                    Log.e(TAG, "Unknown weather type");
                                    break;
                            }
                        }
                    }
                })
                .subscribe();
    }

    /***
     * If it's monday, tell people they're good looking
     */
    private void addMondayGreeting() {
        Date date = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//        int day = cal.get(Calendar.DAY_OF_WEEK);
        int mockDay = Calendar.MONDAY;
        if (mockDay == Calendar.MONDAY) {
            greetings.add(view().getString(R.string.looking_good));
        }
    }
}
