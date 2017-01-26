package com.wajumbie.robot.heart_screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skyfishjy.library.RippleBackground;
import com.wajumbie.robot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    MockMainPresenter presenter;

    @BindView(R.id.slow_heart)
    RippleBackground slowHeart;
    @BindView(R.id.fast_heart)
    RippleBackground fastHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideSystemUI();
        presenter = new MockMainPresenter(this);
        slowHeart.startRippleAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    /***
     * Show fullscreen/immersive mode
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


}
