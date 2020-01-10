package com.nenecorp.pavsstudent.Interface.StudentUi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StartUp.SplashScreen;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectManagement;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectDetails;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectStatus;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectSubmission;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.MyTeam;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.NewTeam;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

public class Home extends AppCompatActivity {
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

        if (pavsDB.getAppUser() != null) {
            ((TextView) findViewById(R.id.AH_txtUserName)).setText(pavsDB.getAppUser().getUserName());
            ((TextView) findViewById(R.id.AH_txtAdmNumber)).setText(pavsDB.getAppUser().getAdmissionNumber());
            findViewById(R.id.AH_btnLogOut).setOnClickListener(v -> Animator.OnClick(v, v1 -> logOut()));
        }
        if (pavsDB.teamProject()) {
            initTeamProject();
        } else {
            initSoloProject();
        }
        pavsDB.addDataListener(new PavsDB.DataListener() {
            @Override
            public void newRequests() {

            }

            @Override
            public void teamPresence() {

            }

            @Override
            public void projectStatus() {
                if (pavsDB.teamProject()) {
                    initTeamProject();
                } else {
                    initSoloProject();
                }
            }

            @Override
            public void newMessage() {

            }
        });
        findViewById(R.id.AH_btnMyInfo).setOnClickListener(v -> Animator.OnClick(v, v12 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(Home.this, StudentDetails.class));
            overridePendingTransition(0, 0);
        }));
        findViewById(R.id.AH_btnSubmitProject).setOnClickListener(v -> Animator.OnClick(v, v13 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(Home.this, ProjectSubmission.class));
            overridePendingTransition(0, 0);
        }));
        findViewById(R.id.AH_btnManageProject).setOnClickListener(v -> Animator.OnClick(v, v14 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(Home.this, ProjectManagement.class));
            overridePendingTransition(0, 0);
        }));

    }

    @Override
    public void finish() {
        super.finish();
        Cache.setHome(null);
        new PavsDBController(database -> {
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        new PavsDBController(database -> {
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Cache.setHome(this);
        initialize();
    }

    private void initSoloProject() {
        findViewById(R.id.AH_btnRegistration).setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            if (pavsDB.getStudentProject() == null) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(Home.this, ProjectDetails.class));
                overridePendingTransition(0, 0);
            } else {
                overridePendingTransition(0, 0);
                startActivity(new Intent(Home.this, ProjectStatus.class));
                overridePendingTransition(0, 0);
            }
        }));
    }

    private void initTeamProject() {
        boolean p = pavsDB.getStudentProject() != null;
        p = p && pavsDB.getStudentProject().getProjectStatus() != null;
        p = p && (pavsDB.getStudentProject().getProjectStatus().equals(Dictionary.REQUESTING)
                || pavsDB.getStudentProject().getProjectStatus().equals(Dictionary.DENIED));
        boolean a = pavsDB.getStudentProject() != null;
        a = a && pavsDB.getStudentProject().getProjectStatus() != null;
        a = a && pavsDB.getStudentProject().getProjectStatus().equals(Dictionary.APPROVED);
        boolean t = pavsDB.getAppUser() != null;
        t = t && pavsDB.getAppUser().getTeamRequest() != null;
        t = t && (pavsDB.getAppUser().getTeamRequest().equals(Dictionary.REQUESTING_TEAM)
                || pavsDB.getAppUser().getTeamRequest().equals(Dictionary.TEAM_DENIED));
        boolean c = pavsDB.getStudentProject() != null;
        c = c && pavsDB.getStudentProject().getProjectStatus() != null;
        c = c && pavsDB.getStudentProject().getProjectStatus().equals(Dictionary.COMPLETED);
        if (t || p || c) {
            findViewById(R.id.AH_btnRegistration).setVisibility(View.VISIBLE);
            findViewById(R.id.AH_btnMyTeam).setVisibility(View.GONE);
        }
        if (a && !t && !c) {
            findViewById(R.id.AH_btnRegistration).setVisibility(View.GONE);
            findViewById(R.id.AH_btnMyTeam).setVisibility(View.VISIBLE);
        }
        boolean finalP = p;
        boolean finalT = t;
        boolean finalC = c;
        boolean finalA = a;
        findViewById(R.id.AH_btnRegistration).setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            if (pavsDB.getStudentProject() == null) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(Home.this, NewTeam.class));
            }
            if (finalP || finalT || finalC) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(Home.this, ProjectStatus.class));
            }
            if (finalA && !finalT && !finalC) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(Home.this, MyTeam.class));
            }
        }));
        findViewById(R.id.AH_btnMyTeam).setOnClickListener(v -> Animator.OnClick(v, v12 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(Home.this, MyTeam.class));

        }));
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, SplashScreen.class));
        overridePendingTransition(0, 0);
        finish();
    }

}
