package com.nenecorp.pavsstudent.Interface.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StudentUi.Home;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectSelection;
import com.nenecorp.pavsstudent.Interface.StudentUi.StudentDetails;
import com.nenecorp.pavsstudent.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "Home";
    private PavsDB pavsDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new PavsDBController(database -> pavsDB = database);
        YoYo.with(Techniques.FadeIn)
                .duration(300)
                .repeat(0)
                .playOn(findViewById(R.id.ASS_cardLogo));
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> startAnimations());
                    }
                }, 350);
    }

    private void startAnimations() {
        YoYo.with(Techniques.StandUp)
                .duration(400)
                .repeat(0)
                .playOn(findViewById(R.id.ASS_cardLogo));
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            findViewById(R.id.ASS_progressBar).setVisibility(View.VISIBLE);
                            checkLogin();
                        });
                    }
                }, 400);
    }

    private void checkLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            new Timer()
                    .schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                overridePendingTransition(0, 0);
                                startActivity(new Intent(SplashScreen.this, SignUp.class));
                                overridePendingTransition(0, 0);
                                finish();
                            });
                        }
                    }, 500);
        } else {
            new Timer()
                    .schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Handler handler = new Handler();
                                    final int delay = 5;
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            Log.i(TAG, "run: ");
                                            if (PavsDBController.isLoaded()) {
                                                if (pavsDB.getAppUser() == null) {
                                                    overridePendingTransition(0, 0);
                                                    startActivity(new Intent(SplashScreen.this, StudentDetails.class));
                                                    overridePendingTransition(0, 0);
                                                    finish();

                                                } else {
                                                    if (pavsDB.selectedProjectType()) {
                                                        overridePendingTransition(0, 0);
                                                        startActivity(new Intent(SplashScreen.this, Home.class));
                                                        overridePendingTransition(0, 0);
                                                        finish();

                                                    } else {
                                                        overridePendingTransition(0, 0);
                                                        startActivity(new Intent(SplashScreen.this, ProjectSelection.class));
                                                        overridePendingTransition(0, 0);
                                                        finish();

                                                    }
                                                }

                                            } else {
                                                handler.postDelayed(this, delay);
                                            }
                                        }
                                    }, delay);
                                }
                            });
                        }
                    }, 500);
        }

    }
}
