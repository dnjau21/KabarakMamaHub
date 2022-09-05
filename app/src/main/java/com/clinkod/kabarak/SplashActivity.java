package com.clinkod.kabarak;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.clinkod.kabarak.auth.SignUp;
import com.clinkod.kabarak.exceptions.LocalPropertyNotFound;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.ui.onboarding.OnBoarding;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    Handler handler;
    ImageView logo;
    private static final String FIRST_TIME_USE = "first_time_use";
    private static final String IS_LOGGED_IN  = "first_time_use";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        handler = new Handler();
        handler.postDelayed(() -> {

            if(!PropertyUtils.hasFinishedOnboardingData() || !PropertyUtils.isLoggedIn()){
                Intent intent = new Intent(SplashActivity.this, OnBoarding.class);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }


        }, 1000);


    }

//    private boolean isLoggedIn() {
//        try {
//            return PropertyUtils.getBooleanValue(IS_LOGGED_IN, true);
//        }catch (LocalPropertyNotFound e){
//            PropertyUtils.putProperty(IS_LOGGED_IN, false);
//            return true;
//        }
//    }

    private boolean isFirstTimeUse(){
        try{
            return PropertyUtils.getBooleanValue(FIRST_TIME_USE, true);
        }catch (LocalPropertyNotFound e){

            PropertyUtils.putProperty(FIRST_TIME_USE, false);
            return true;
        }
    }
    private void fadeOutLogo(final ImageView img){
        Animation fadeOut = new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Would you like to exit?", Toast.LENGTH_SHORT).show();
    }
}