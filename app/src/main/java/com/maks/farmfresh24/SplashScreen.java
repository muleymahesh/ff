package com.maks.farmfresh24;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.maks.farmfresh24.utils.AppPreferences;


public class SplashScreen extends AppCompatActivity {

    //
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-        <include layout="@layout/toolbar" />
         * Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent;

                if (new AppPreferences(SplashScreen.this).isLogin()) {
                    mainIntent = new Intent(SplashScreen.this, MainActivity.class);//LoginActivity
                } else {
                    mainIntent = new Intent(SplashScreen.this, MainActivity.class);//LoginActivity

                }

                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
