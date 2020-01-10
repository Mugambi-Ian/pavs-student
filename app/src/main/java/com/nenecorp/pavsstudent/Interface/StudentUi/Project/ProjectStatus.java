package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.MyTeam;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.NewTeam;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

public class ProjectStatus extends AppCompatActivity {
    private TextView txtStatus;
    private PavsDB pavsDB;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize() {
        if (PavsDBController.isLoaded()) {
            pavsDB = PavsDBController.getDatabase();
            loadContent();
        } else {
            new PavsDBController(database -> {
                pavsDB = database;
                loadContent();
            });
        }
    }

    private void loadContent() {
        txtStatus = findViewById(R.id.APS_txtStatus);
        switch (pavsDB.getStudentProject().getProjectStatus()) {
            case Dictionary.REQUESTING:
                requesting();
                break;
            case Dictionary.DENIED:
                deniedRequest();
                break;
            case Dictionary.APPROVED:
                approvedRequest();
                break;
            case Dictionary.COMPLETED:
                projectCompleted();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_status);
        initialize();
    }

    private void projectCompleted() {
        txtStatus.setText(Dictionary.projectCompleted);
    }

    private void approvedRequest() {
        txtStatus.setText(Dictionary.projectApproved);
        if (pavsDB.getAppUser().getProjectType().equals(Dictionary.TEAM_PROJECT)) {
            String teamRequest = pavsDB.getAppUser().getTeamRequest();
            if (teamRequest != null && !teamRequest.equals("")) {
                switch (teamRequest) {
                    case Dictionary.REQUESTING_TEAM:
                        requestingTeam();
                        break;
                    case Dictionary.TEAM_DENIED:
                        teamDenied();
                    default:
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(this, MyTeam.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                }
            } else {
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, MyTeam.class));
                overridePendingTransition(0, 0);
                finish();
            }

        }
    }

    private void teamDenied() {
        txtStatus.setText(Dictionary.teamDenied);
        View btnTryAgain = findViewById(R.id.APS_btnTryAgain);
        btnTryAgain.setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            if (pavsDB.getStudentProject().getProjectType().equals(Dictionary.TEAM_PROJECT)) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(ProjectStatus.this, NewTeam.class));
                overridePendingTransition(0, 0);
                finish();
            } else {
                overridePendingTransition(0, 0);
                startActivity(new Intent(ProjectStatus.this, ProjectDetails.class));
                overridePendingTransition(0, 0);
                finish();
            }
        }));
    }

    private void requestingTeam() {
        txtStatus.setText(Dictionary.requestingTeam);
    }

    private void deniedRequest() {
        txtStatus.setText(Dictionary.projectDenied);
        View btnTryAgain = findViewById(R.id.APS_btnTryAgain);
        btnTryAgain.setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            if (pavsDB.getStudentProject().getProjectType().equals(Dictionary.TEAM_PROJECT)) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(ProjectStatus.this, NewTeam.class));
                overridePendingTransition(0, 0);
                finish();
            } else {
                overridePendingTransition(0, 0);
                startActivity(new Intent(ProjectStatus.this, ProjectDetails.class));
                overridePendingTransition(0, 0);
                finish();
            }
        }));
    }

    private void requesting() {
        txtStatus.setText(Dictionary.projectWaiting);
    }
}
