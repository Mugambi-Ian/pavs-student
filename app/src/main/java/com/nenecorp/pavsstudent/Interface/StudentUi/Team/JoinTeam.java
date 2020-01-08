package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectStatus;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class JoinTeam extends AppCompatActivity {
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        setupWindowAnimations();
        final TextInputEditText editText = findViewById(R.id.AJT_edProjectId);
        final TextInputLayout inputLayout = findViewById(R.id.AJT_tlProjectID);
        findViewById(R.id.AJT_btnJoinTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().length() == 0) {
                            inputLayout.setError("Fill this value;");
                        } else if (!PavsDatabase.joinProject(editText.getText().toString().toUpperCase())) {
                            inputLayout.setError("This id is invalid");
                        } else if (PavsDatabase.getProject(editText.getText().toString().toUpperCase()).getGroupMembers().size() <= PavsDatabase.getProject(editText.getText().toString().toUpperCase()).getTeamSize()) {
                            PavsDatabase.createJoinRequest(editText.getText().toString());
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(JoinTeam.this, ProjectStatus.class));
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                });

            }
        });
    }
}
