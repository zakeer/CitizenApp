package me.zakeer.startapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timer = new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    System.out.println("Error.....");
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(Splash.this, UserLogin.class);
//                    Intent intent = new Intent(getApplicationContext(), me.zakeer.startapp)
                    startActivity(intent);
                }
            }
        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
