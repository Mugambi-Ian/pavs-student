package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.DataModel.Pavs.Student;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StudentUi.Home;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

public class ProjectSelection extends AppCompatActivity {
    private PavsDB pavsDB;

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
        View btnTeamProject = findViewById(R.id.APT_btnGroupProject);
        View btnSoloProject = findViewById(R.id.APT_btnSoloProject);
        btnTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Student x = pavsDB.getAppUser();
                        x.setProjectType(Dictionary.TEAM_PROJECT);
                        pavsDB.saveUser(x);
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
                        Student x = pavsDB.getAppUser();
                        x.setProjectType(Dictionary.SOLO_PROJECT);
                        pavsDB.saveUser(x);
                        finish();
                    }
                });
            }
        });
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
        setContentView(R.layout.activity_project_selection);
        initialize();
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
}
