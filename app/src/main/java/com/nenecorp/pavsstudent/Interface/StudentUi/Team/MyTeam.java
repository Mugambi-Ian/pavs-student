package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;

import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.R;

public class MyTeam extends AppCompatActivity {
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        setupWindowAnimations();
        PavsDatabase.joinMessaging();
    }
}
