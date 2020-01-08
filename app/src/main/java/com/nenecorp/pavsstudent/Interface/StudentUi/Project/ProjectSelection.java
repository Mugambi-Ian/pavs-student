package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.DataModel.Pavs.Student;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.Interface.StudentUi.Home;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

public class ProjectSelection extends AppCompatActivity {
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void finish() {
        if (Cache.getHome() == null) {
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, Home.class));
        }
        overridePendingTransition(0, 0);
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_type);
        setupWindowAnimations();
        View btnTeamProject = findViewById(R.id.APT_btnGroupProject);
        View btnSoloProject = findViewById(R.id.APT_btnSoloProject);
        btnTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Student x = PavsDatabase.getAppUser();
                        x.setProjectType(Dictionary.TEAM_PROJECT);
                        PavsDatabase.saveUser(x);
                        finish();
                    }
                });
            }
        });
        btnSoloProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Student x = PavsDatabase.getAppUser();
                        x.setProjectType(Dictionary.SOLO_PROJECT);
                        PavsDatabase.saveUser(x);
                        finish();
                    }
                });
            }
        });
    }
}
