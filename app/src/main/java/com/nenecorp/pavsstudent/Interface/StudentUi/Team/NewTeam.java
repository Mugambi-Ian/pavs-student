package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectDetails;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class NewTeam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_team);
        findViewById(R.id.APR_btnCreate).setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(NewTeam.this, ProjectDetails.class));
            overridePendingTransition(0, 0);
            finish();
        }));
        findViewById(R.id.APR_btnJoin).setOnClickListener(v -> Animator.OnClick(v, v12 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(NewTeam.this, JoinTeam.class));
            overridePendingTransition(0, 0);
            finish();
        }));
    }
}
