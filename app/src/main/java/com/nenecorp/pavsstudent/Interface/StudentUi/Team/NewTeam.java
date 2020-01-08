package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectInfo;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class NewTeam extends AppCompatActivity {
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_team);
        setupWindowAnimations();
        findViewById(R.id.APR_btnCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(NewTeam.this, ProjectInfo.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                });
            }
        });
        findViewById(R.id.APR_btnJoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(NewTeam.this, JoinTeam.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                });
            }
        });
    }
}
