package com.nenecorp.pavsstudent.Interface.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.Interface.StudentUi.Home;
import com.nenecorp.pavsstudent.Interface.StudentUi.Project.ProjectSelection;
import com.nenecorp.pavsstudent.Interface.StudentUi.StudentDetails;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;

public class SignUp extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Sign_In";
    private GoogleSignInClient mGoogleSignInClient;
    private View signInProgress;
    private PavsDB pavsDB;

    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void finish() {
        boolean start = FirebaseAuth.getInstance().getCurrentUser() != null;
        if (pavsDB.getAppUser() == null && start) {
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, StudentDetails.class));
            overridePendingTransition(0, 0);
        } else {
            if (pavsDB.getAppUser() != null && pavsDB.selectedProjectType() && start) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, Home.class));
                overridePendingTransition(0, 0);
            } else if (pavsDB.getAppUser() != null && start) {
                overridePendingTransition(0, 0);
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
        signInProgress = findViewById(R.id.ASU_progressBar);
        findViewById(R.id.ASU_btnSignIn).setOnClickListener(v -> {
            Animator.OnClick(v, v1 -> signIn());
            v.setEnabled(false);
            signInProgress.setVisibility(View.VISIBLE);
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
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        finish();
                    }

                });
        /* if (domain.equals("kabarak.ac.ke")) {

        } else {
            signInBtn.setEnabled(true);
            Toast.makeText(this, "Exclusively Kabarak", Toast.LENGTH_LONG).show();
        }*/

    }
}
