package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nenecorp.pavsstudent.DataModel.Chat.Chat;
import com.nenecorp.pavsstudent.DataModel.Pavs.Student;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;
import com.nenecorp.pavsstudent.Utility.Tools.PresenceManager.PresenceRecord;

import java.util.ArrayList;

public class JoinRequests extends AppCompatActivity {
    private View viewNewRequest, viewNoRequest;
    private Student reqStudent;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_requests);
        viewNewRequest = findViewById(R.id.AJR_lytManageRequest);
        viewNoRequest = findViewById(R.id.AJR_lytNoRequests);
        if (PavsDatabase.getJoinRequests().size() == 0) {
            noRequests();
        } else {
            manageRequest();
        }
        PavsDatabase.addDataListener(new PavsDatabase.DataListener() {
            @Override
            public void newRequests(ArrayList<Student> students) {
                if (students.size() == 0) {
                    noRequests();
                } else {
                    manageRequest();
                }
            }

            @Override
            public void teamPresence(ArrayList<PresenceRecord> presenceRecords) {

            }

            @Override
            public void projectStatus(String projectStatus) {

            }

            @Override
            public void newMessage(Chat chat) {

            }
        });
    }

    private void manageRequest() {
        if (viewNoRequest.getVisibility()==View.VISIBLE){
            YoYo.with(Techniques.FadeIn)
                    .duration(300)
                    .repeat(0)
                    .playOn(viewNewRequest);
            viewNoRequest.setVisibility(View.GONE);
        }
        viewNewRequest.setVisibility(View.VISIBLE);
        reqStudent = PavsDatabase.getJoinRequests().get(0);
        ((TextView) findViewById(R.id.AJR_txtStudentName)).setText(reqStudent.getUserName());
        ((TextView) findViewById(R.id.AJR_txtAdmNo)).setText(reqStudent.getAdmissionNumber());
        findViewById(R.id.AJR_btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectStudent();
                    }
                });
            }
        });

    }

    private void rejectStudent() {
        PavsDatabase.rejectGroupJoin(reqStudent);

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
