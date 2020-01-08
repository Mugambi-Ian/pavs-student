package com.nenecorp.pavsstudent.Interface.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.Interface.StudentUi.Home;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectSelection;
import com.nenecorp.pavsstudent.Interface.StudentUi.StudentInfo;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class SignUp extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Sign_In";
    private GoogleSignInClient mGoogleSignInClient;
    private View signInProgress, signInBtn;

    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void finish() {
        boolean start = FirebaseAuth.getInstance().getCurrentUser() != null;
        if (PavsDatabase.getAppUser() == null && start) {
            overridePendingTransition(0,0);
            startActivity(new Intent(this, StudentInfo.class));
            overridePendingTransition(0, 0);
        } else {
            if (PavsDatabase.getAppUser() != null && PavsDatabase.selectedProjectType() && start) {
                overridePendingTransition(0,0);
                startActivity(new Intent(this, Home.class));
                overridePendingTransition(0, 0);
            } else if (PavsDatabase.getAppUser() != null && start) {
                overridePendingTransition(0,0);
                startActivity(new Intent(this, ProjectSelection.class));
                overridePendingTransition(0, 0);
            }
        }
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupWindowAnimations();
        signInProgress = findViewById(R.id.ASU_progressBar);
        findViewById(R.id.ASU_btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInBtn = v;
                Animator.OnClick(v, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                });
                v.setEnabled(false);
                signInProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            signInProgress.setVisibility(View.GONE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        mGoogleSignInClient.signOut();
        String[] email = acct.getEmail().split("@");
        String domain = email[1].toLowerCase();
        if (domain.equals("kabarak.ac.ke")) {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                            }

                        }
                    });
        } else {
            signInBtn.setEnabled(true);
            Toast.makeText(this, "Exclusively Kabarak", Toast.LENGTH_LONG).show();
        }

    }
}
