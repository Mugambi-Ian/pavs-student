package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.MyTeam;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.NewTeam;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

public class ProjectStatus extends AppCompatActivity {
    private TextView txtStatus;

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_status);
        setupWindowAnimations();
        txtStatus = findViewById(R.id.APS_txtStatus);
        switch (PavsDatabase.getStudentProject().getProjectStatus(PavsDatabase.getAppUser().getStudentsId())) {
            case Dictionary.WAITING:
                waitingProgress();
                break;
            case Dictionary.DENIED:
                deniedRequest();
                break;
            case Dictionary.APPROVED:
                approvedRequest();
                break;
            case Dictionary.GROUP_REQUEST:
                groupRequest();
                break;
        }
    }

    private void groupRequest() {
        txtStatus.setText(Dictionary.groupReview);
    }

    private void approvedRequest() {
        txtStatus.setText(Dictionary.projectApproved);
        if (PavsDatabase.getAppUser().getProjectType().equals(Dictionary.TEAM_PROJECT)) {
            Cache.home.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Cache.home.initTeamProject();
                }
            });
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, MyTeam.class));
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void deniedRequest() {
        txtStatus.setText(Dictionary.projectDenied);
        View btnTryAgain = findViewById(R.id.APS_btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PavsDatabase.getStudentProject().getProjectType().equals(Dictionary.TEAM_PROJECT)) {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(ProjectStatus.this, NewTeam.class));
                            overridePendingTransition(0, 0);
                            finish();
                        } else {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(ProjectStatus.this, ProjectInfo.class));
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void waitingProgress() {
        txtStatus.setText(Dictionary.projectWaiting);
    }
}
