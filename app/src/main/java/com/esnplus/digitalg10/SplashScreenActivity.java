package com.esnplus.digitalg10;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences prefs = getSharedPreferences("storage", MODE_PRIVATE);
        String pass = prefs.getString("pass","1234");
        boolean isPassEnabled = prefs.getBoolean("pass_enabled",true);

        new Handler().postDelayed(() -> {
            if (isPassEnabled && !pass.isEmpty()){
                startActivity(new Intent(SplashScreenActivity.this,EnterPassActivity.class));
            }else {
                if (Utils.getAlarmDeviceList(SplashScreenActivity.this).isEmpty())
                    startActivity(new Intent(SplashScreenActivity.this, ManageAlarmDevicesActivity.class));
                else startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
            finish();
        }, 2300);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_move_from_bottom);
        findViewById(R.id.textShow).startAnimation(animation);
        findViewById(R.id.gif).startAnimation(animation2);
    }
}