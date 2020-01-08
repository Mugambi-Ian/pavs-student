package com.nenecorp.pavsstudent.Utility.Resources;

import android.app.Activity;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Timer;
import java.util.TimerTask;

public class Animator {
    public static void OnClick(final View x, View.OnClickListener onClickListener) {
        final View z = new View(x.getContext());
        z.setOnClickListener(onClickListener);
        x.setEnabled(false);
        YoYo.with(Techniques.FlipInY)
                .duration(300)
                .repeat(0)
                .playOn(x);
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity) x.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                z.callOnClick();
                                x.setEnabled(true);
                            }
                        });
                    }
                }, 350);
    }
}
