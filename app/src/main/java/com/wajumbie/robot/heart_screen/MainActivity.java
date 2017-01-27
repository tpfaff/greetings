package com.wajumbie.robot.heart_screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skyfishjy.library.RippleBackground;
import com.wajumbie.robot.R;
import com.wajumbie.robot.utils.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    MockMainPresenter presenter;
    //todo inject a real presenter when you have a real robot
    // MainPresenter presenter;

    @BindView(R.id.slow_heart)
    RippleBackground slowHeart;
    @BindView(R.id.fast_heart)
    RippleBackground fastHeart;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MockMainPresenter();
        //todo inject a real presenter when you have a real robot
        //presenter = new MainPresenter();
        presenter.bindView(this);
        UiUtils.showImmersiveMode(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        slowHeart.startRippleAnimation();
        presenter.startDetectingHumans();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    public void showHeartbeat() {
        fastHeart.startRippleAnimation();
        fastHeart.setVisibility(View.VISIBLE);
    }

    public void hideHeartbeat() {
        fastHeart.setVisibility(View.GONE);
    }


}
