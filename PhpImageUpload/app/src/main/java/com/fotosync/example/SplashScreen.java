package com.fotosync.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.database.sqlite.SQLiteDatabase;


public class SplashScreen extends Activity {

    private static final int TIME = 500;
    AnimationDrawable savingAnimation;
    RelativeLayout lay;
    ImageView iv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashScreen.this, Register.class);
                startActivity(intent);

                SplashScreen.this.finish();
                overridePendingTransition(R.anim.fade_in1, R.anim.fade_out1);
            }
        }, TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, TIME);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
