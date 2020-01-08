package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nenecorp.pavsstudent.DataModel.Pavs.Project;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

import java.util.ArrayList;

public class ProjectInfo extends AppCompatActivity {
    private ArrayList<InputField> inputFields;
    private TextInputEditText editTextName, editTextDescription;
    private TextView projectId;

    @Override
    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }

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
        setContentView(R.layout.activity_project_info);
        setupWindowAnimations();
        inputFields = new ArrayList<>();
        projectId = findViewById(R.id.API_txtProjectId);
        projectId.setText(PavsDatabase.getProjectId());
        TextInputLayout layoutName = findViewById(R.id.API_tlProjectName);
        TextInputLayout layoutDescription = findViewById(R.id.API_tlDescription);
        editTextName = findViewById(R.id.API_edProjectName);
        editTextDescription = findViewById(R.id.API_edDescription);
        inputFields.add(new InputField(editTextDescription, layoutDescription));
        inputFields.add(new InputField(editTextName, layoutName));
        findViewById(R.id.API_btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });
        findViewById(R.id.API_btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitProject();
                    }
                });
            }
        });
    }

    private void submitProject() {
        if (fieldsAreValid()) {
            String id = projectId.getText().toString();
            String name = editTextName.getText().toString();
            String description = editTextDescription.getText().toString();
            String status = Dictionary.WAITING;
            String projectType = PavsDatabase.getAppUser().getProjectType();
            Project project = new Project(id).setProjectName(name).setProjectDescription(description).setProjectStatus(status).setProjectType(projectType);
            if (project.getProjectType().equals(Dictionary.TEAM_PROJECT)) {
                project.addGroupMember(PavsDatabase.getAppUser().getStudentsId());
            }
            PavsDatabase.saveProject(project);
            finish();
            startActivity(new Intent(this, ProjectStatus.class));
        }
    }

    private boolean fieldsAreValid() {
        for (InputField field : inputFields) {
            if (field.editText.getText().length() == 0) {
                field.inputLayout.setError("You need to fill this values.");
                return false;
            }
        }
        return true;
    }

    private class InputField {
        TextInputEditText editText;
        TextInputLayout inputLayout;

        InputField(TextInputEditText editText, TextInputLayout inputLayout) {
            this.editText = editText;
            this.inputLayout = inputLayout;
        }
    }
}
