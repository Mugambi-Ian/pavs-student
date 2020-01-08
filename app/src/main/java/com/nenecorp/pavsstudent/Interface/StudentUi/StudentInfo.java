package com.nenecorp.pavsstudent.Interface.StudentUi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.DataModel.Pavs.Student;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectSelection;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;

import java.util.ArrayList;

public class StudentInfo extends AppCompatActivity {
    private TextInputEditText editTextAdmNo, editTextName, editTextPhoneNumber;
    private View btnCancel;
    private ArrayList<InputField> inputFields;

    @Override
    public void finish() {
        if (Cache.home == null) {
            overridePendingTransition(0,0);
            startActivity(new Intent(this, ProjectSelection.class));
        }
        overridePendingTransition(0,0);
        super.finish();
    }

    public void hideKeyboard() {
        Activity activity = this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
setupWindowAnimations();
        inputFields = new ArrayList<>();
        btnCancel = findViewById(R.id.AUR_btnCancel);
        View btnSave = findViewById(R.id.AUR_btnSave);
        TextInputLayout layoutAdmNo = findViewById(R.id.AUR_tlAdmNo);
        TextInputLayout layoutName = findViewById(R.id.AUR_tlName);
        TextInputLayout layoutPhoneNumber = findViewById(R.id.AUR_tlPhoneNumber);
        editTextAdmNo = findViewById(R.id.AUR_edAdmNo);
        editTextName = findViewById(R.id.AUR_edName);
        editTextPhoneNumber = findViewById(R.id.AUR_edPhoneNumber);
        inputFields.add(new InputField(editTextAdmNo, layoutAdmNo));
        inputFields.add(new InputField(editTextName, layoutName));
        inputFields.add(new InputField(editTextPhoneNumber, layoutPhoneNumber));
        editTextName.setImeActionLabel("actionNext", EditorInfo.IME_ACTION_NEXT);
        editTextAdmNo.setImeActionLabel("actionNext", EditorInfo.IME_ACTION_NEXT);
        editTextPhoneNumber.setImeActionLabel("actionDone", EditorInfo.IME_ACTION_DONE);
        editTextAdmNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    editTextName.requestFocus();
                }
                return true;
            }
        });
        editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    editTextPhoneNumber.requestFocus();
                }
                return true;
            }
        });
        editTextPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                }
                return true;
            }
        });
        if (PavsDatabase.getAppUser() == null) {
            newUser();
        } else {
            loadUser();
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveUserInfo();
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
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
    }

    private void saveUserInfo() {
        if (detailsAreFine()) {
            String admNo = editTextAdmNo.getText().toString();
            String name = editTextName.getText().toString();
            String phoneNumber = editTextPhoneNumber.getText().toString();
            String id = FirebaseAuth.getInstance().getUid();
            Student student = new Student(id);
            student.setAdmissionNumber(admNo)
                    .setPhoneNumber(phoneNumber)
                    .setUserName(name);
            PavsDatabase.saveUser(student);
            finish();
        }
    }

    private boolean detailsAreFine() {
        for (InputField inputField : inputFields) {
            if (inputField.editText.getText().length() == 0) {
                inputField.inputLayout.setError("You have to fill this field.");
                return false;
            }
        }
        return true;
    }

    private void loadUser() {
        editTextAdmNo.setEnabled(false);
        editTextAdmNo.setText(PavsDatabase.getAppUser().getAdmissionNumber());
        editTextName.setText(PavsDatabase.getAppUser().getUserName());
        editTextPhoneNumber.setText(PavsDatabase.getAppUser().getPhoneNumber());
    }

    private void newUser() {
        btnCancel.setVisibility(View.INVISIBLE);
        editTextPhoneNumber.setText("");
        editTextAdmNo.setText("");
        editTextPhoneNumber.setText("");
    }

    private class InputField {
        private TextInputEditText editText;
        private TextInputLayout inputLayout;

        InputField(TextInputEditText editText, TextInputLayout inputLayout) {
            this.editText = editText;
            this.inputLayout = inputLayout;
        }
    }
}
