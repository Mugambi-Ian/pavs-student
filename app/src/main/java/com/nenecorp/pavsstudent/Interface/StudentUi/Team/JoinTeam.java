package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectStatus;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class JoinTeam extends AppCompatActivity {
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
        final TextInputEditText editText = findViewById(R.id.AJT_edProjectId);
        final TextInputLayout inputLayout = findViewById(R.id.AJT_tlProjectID);
        findViewById(R.id.AJT_btnJoinTeam).setOnClickListener(v -> Animator.OnClick(v, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() == 0) {
                    inputLayout.setError("Fill this value;");
                } else if (!pavsDB.joinProject(editText.getText().toString().toUpperCase())) {
                    inputLayout.setError("This id is invalid");
                } else if (pavsDB.getProject(editText.getText().toString().toUpperCase()).getGroupMembers().size() <= pavsDB.getProject(editText.getText().toString().toUpperCase()).getTeamSize()) {
                    pavsDB.createJoinRequest(editText.getText().toString());
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(JoinTeam.this, ProjectStatus.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        }));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        initialize();
    }
}
