package com.nenecorp.pavsstudent.Interface.StudentUi;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.Interface.StartUp.SplashScreen;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectInfo;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectStatus;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.MyTeam;
import com.nenecorp.pavsstudent.Interface.StudentUi.Team.NewTeam;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

public class Home extends AppCompatActivity {
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void finish() {
        super.finish();
        Cache.setHome(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupWindowAnimations();
        Cache.setHome(this);
        ((TextView) findViewById(R.id.AH_txtUserName)).setText(PavsDatabase.getAppUser().getUserName());
        ((TextView) findViewById(R.id.AH_txtAdmNumber)).setText(PavsDatabase.getAppUser().getAdmissionNumber());
        findViewById(R.id.AH_btnLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logOut();
                    }
                });
            }
        });
        if (PavsDatabase.teamProject()) {
            initTeamProject();
        } else {
            initSoloProject();
        }
        findViewById(R.id.AH_btnMyInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(Home.this, StudentInfo.class));
                        overridePendingTransition(0, 0);
                    }
                });
            }
        });

    }

    private void initSoloProject() {
        findViewById(R.id.AH_btnRegistration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PavsDatabase.getStudentProject() == null) {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(Home.this, ProjectInfo.class));
                            overridePendingTransition(0, 0);
                        } else {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(Home.this, ProjectStatus.class));
                            overridePendingTransition(0, 0);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTeamProject();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initTeamProject();
    }

    public void initTeamProject() {
        if (PavsDatabase.getStudentProject() == null) {
            findViewById(R.id.AH_btnRegistration).setVisibility(View.VISIBLE);
            findViewById(R.id.AH_btnMyTeam).setVisibility(View.GONE);
        } else if (PavsDatabase.getStudentProject().getProjectStatus(PavsDatabase.getAppUser().getStudentsId()).equals(Dictionary.APPROVED)) {
            findViewById(R.id.AH_btnRegistration).setVisibility(View.GONE);
            findViewById(R.id.AH_btnMyTeam).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.AH_btnRegistration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PavsDatabase.getStudentProject() == null) {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(Home.this, NewTeam.class));
                            overridePendingTransition(0, 0);
                        } else if (PavsDatabase.getStudentProject().getProjectStatus(PavsDatabase.getAppUser().getStudentsId()).equals(Dictionary.WAITING) || PavsDatabase.getStudentProject().getProjectStatus(PavsDatabase.getAppUser().getStudentsId()).equals(Dictionary.DENIED)) {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(Home.this, ProjectStatus.class));
                            overridePendingTransition(0, 0);
                        } else {
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(Home.this, MyTeam.class));
                            overridePendingTransition(0, 0);
                        }
                    }
                });

            }
        });
        findViewById(R.id.AH_btnMyTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(Home.this, MyTeam.class));
                        overridePendingTransition(0, 0);
                    }
                });

            }
        });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, SplashScreen.class));
        overridePendingTransition(0, 0);
        finish();
    }

}
