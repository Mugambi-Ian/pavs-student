package com.nenecorp.pavsstudent.Interface.StudentUi.Project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.deepan.pieprogress.PieProgress;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

import java.util.ArrayList;

public class ProjectSubmission extends AppCompatActivity {
    private static final int PDF_REQUEST_CODE = 200, ZIP_REQUEST_CODE = 300;
    private String pdfUri, zipUri;
    private View lytWelcome, lytUpload;
    private ArrayList<MediaFile> pdfFiles;
    private View btnSubmit;
    private ArrayList<MediaFile> zipFiles;
    private float uploaded = 0;
    private float fileSize = 0;
    private PieProgress pieProgress;
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
        pdfFiles = new ArrayList<>();
        zipFiles = new ArrayList<>();
        btnSubmit = findViewById(R.id.ASP_btnSubmit);
        lytWelcome = findViewById(R.id.ASP_lytWelcome);
        lytUpload = findViewById(R.id.ASP_lytUploading);
        pieProgress = findViewById(R.id.ASP_pgProgress);
        findViewById(R.id.ASP_btnPdf).setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            Intent intent = new Intent(ProjectSubmission.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowFiles(true)
                    .setShowAudios(false)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setSelectedMediaFiles(pdfFiles)
                    .setMaxSelection(2)
                    .setRootPath(Environment.getExternalStorageDirectory().getPath())
                    .build());
            startActivityForResult(intent, PDF_REQUEST_CODE);
        }));
        findViewById(R.id.ASP_btnZip).setOnClickListener(v -> Animator.OnClick(v, v12 -> {
            Intent intent = new Intent(ProjectSubmission.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowFiles(true)
                    .setShowAudios(false)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setSelectedMediaFiles(zipFiles)
                    .setMaxSelection(2)
                    .setRootPath(Environment.getExternalStorageDirectory().getPath())
                    .build());
            startActivityForResult(intent, ZIP_REQUEST_CODE);
        }));
        btnSubmit.setOnClickListener(v -> startUpload());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_project);
        initialize();
    }

    private void startUpload() {
        hideViews();
        final StorageReference pdf = FirebaseStorage.getInstance().getReference().child(Dictionary.PROJECTS).child(pavsDB.getProjectYear(pavsDB.getStudentProject().getProjectId())).child(pavsDB.getStudentProject().getProjectId()).child("Pdf");
        final UploadTask uploadPdf = pdf.putFile(Uri.parse(pdfUri));
        uploadPdf.addOnProgressListener(taskSnapshot -> {
            uploaded = uploaded + taskSnapshot.getBytesTransferred();
            pieProgress.setProgress(uploaded);

            if (uploaded == fileSize) {
                pieProgress.setCompleted(true);
            }
        });
        uploadPdf.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            return pdf.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri taskResult = task.getResult();
                pdfUri = taskResult.toString();
                if (zipUri != null) {
                    final StorageReference zip = FirebaseStorage.getInstance().getReference().child(Dictionary.PROJECTS).child(pavsDB.getProjectYear(pavsDB.getStudentProject().getProjectId())).child(pavsDB.getStudentProject().getProjectId()).child("Zip");
                    final UploadTask uploadZip = zip.putFile(Uri.parse(zipUri));
                    uploadZip.addOnProgressListener(taskSnapshot -> {
                        uploaded = taskSnapshot.getBytesTransferred() + uploaded;
                        pieProgress.setProgress(uploaded);
                        if (uploaded == fileSize) {
                            pieProgress.setCompleted(true);
                        }
                    });
                    uploadZip.continueWithTask(task1 -> {
                        if (!task1.isSuccessful()) {
                            throw task1.getException();
                        }

                        return zip.getDownloadUrl();
                    }).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Uri taskResult1 = task.getResult();
                            zipUri = taskResult1.toString();
                            completedProject();
                        }
                    });
                } else {
                    completedProject();
                }
            }
        }).addOnFailureListener(command -> enableViews());
    }

    private void completedProject() {
        pavsDB.completeProject(pdfUri, zipUri);
        Toast.makeText(this, "Your Project has been submitted", Toast.LENGTH_SHORT);
        finish();
    }

    private void enableViews() {
        lytUpload.setVisibility(View.GONE);
        YoYo.with(Techniques.Tada)
                .duration(300)
                .repeat(0)
                .playOn(lytWelcome);
        lytWelcome.setVisibility(View.VISIBLE);
    }

    private void hideViews() {
        lytWelcome.setVisibility(View.GONE);
        YoYo.with(Techniques.Tada)
                .duration(300)
                .repeat(0)
                .playOn(lytUpload);
        lytUpload.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            pdfFiles.clear();
            pdfFiles.addAll(data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
            MediaFile pdfFile = pdfFiles.get(0);
            int index = pdfFile.getPath().lastIndexOf(".");
            String ext = pdfFile.getPath().substring(index);
            Log.i("TAG", "onActivityResult: " + ext);
            fileSize = pdfFile.getSize() + fileSize;
            findViewById(R.id.ASP_btnPdf).setEnabled(false);
            pdfUri = pdfFile.getUri().toString();
            if (btnSubmit.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeIn)
                        .duration(300)
                        .repeat(0)
                        .playOn(btnSubmit);
                btnSubmit.setVisibility(View.VISIBLE);
            }
            if (ext.toLowerCase().equals(".pdf")) {

            } else {
                Toast.makeText(this, "Format not recognized", Toast.LENGTH_SHORT);
            }
        }
        if (requestCode == ZIP_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            zipFiles.clear();
            zipFiles.addAll(data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
            MediaFile zipFile = zipFiles.get(0);
            int index = zipFile.getPath().lastIndexOf(".");
            String ext = zipFile.getPath().substring(index);
            if (ext.toLowerCase().equals(".zip")) {
                fileSize = zipFile.getSize() + fileSize;
                pieProgress.
                        findViewById(R.id.ASP_btnZip).setEnabled(false);
                zipUri = zipFile.getUri().toString();
            } else {
                Toast.makeText(this, "Format not recognized", Toast.LENGTH_SHORT);
            }
        }
    }

}
