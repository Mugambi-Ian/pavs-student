package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StartUp.SplashScreen;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;

public class ProjectManagement extends AppCompatActivity {
    private PavsDB pavsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_project);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        findViewById(R.id.AMP_btnLeave).setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            pavsDB.leaveProject();
            Cache.getHome().finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(ProjectManagement.this, SplashScreen.class));
            finish();
        }));
    }
}
