package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nenecorp.pavsstudent.DataModel.Pavs.Student;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class JoinRequests extends AppCompatActivity {
    private View viewNewRequest, viewNoRequest;
    private Student reqStudent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_requests);
        initialize();
    }

    private void loadContent() {
        viewNewRequest = findViewById(R.id.AJR_lytManageRequest);
        viewNoRequest = findViewById(R.id.AJR_lytNoRequests);
        if (pavsDB.getJoinRequests().size() == 0) {
            noRequests();
        } else {
            manageRequest();
        }
    }

    private void manageRequest() {
        if (viewNoRequest.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.FadeIn)
                    .duration(300)
                    .repeat(0)
                    .playOn(viewNewRequest);
            viewNoRequest.setVisibility(View.GONE);
        }
        viewNewRequest.setVisibility(View.VISIBLE);
        reqStudent = pavsDB.getJoinRequests().get(0);
        ((TextView) findViewById(R.id.AJR_txtStudentName)).setText(reqStudent.getUserName());
        ((TextView) findViewById(R.id.AJR_txtAdmNo)).setText(reqStudent.getAdmissionNumber());
        findViewById(R.id.AJR_btnCancel).setOnClickListener(v ->
                Animator.OnClick(v, v12 -> {
                    {
                        finish();
                        pavsDB.rejectJoinRequest(reqStudent);
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(JoinRequests.this, JoinRequests.class));

                    }

                }));
        findViewById(R.id.AJR_btnApprove).setOnClickListener(v ->
                Animator.OnClick(v, v1 -> {
                    finish();
                    pavsDB.approveJoinRequest(reqStudent);
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(JoinRequests.this, JoinRequests.class));
                }));
    }


    private void noRequests() {
        if (viewNewRequest.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.FadeIn).duration(300)
                    .repeat(0)
                    .playOn(viewNoRequest);
            viewNewRequest.setVisibility(View.GONE);
        }
        viewNoRequest.setVisibility(View.VISIBLE);
    }
}
