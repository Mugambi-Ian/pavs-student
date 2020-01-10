package com.nenecorp.pavsstudent.Interface.StudentUi.Team;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nenecorp.pavsstudent.DataModel.Chat.Message;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.DataModel.PavsDBController;
import com.nenecorp.pavsstudent.R;
import com.nenecorp.pavsstudent.Utility.Adapters.ChatAdapter.ChatAdapter;
import com.nenecorp.pavsstudent.Utility.Resources.Animator;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;
import com.nenecorp.pavsstudent.Utility.Tools.WidgetHandler;

import java.util.ArrayList;

public class MyTeam extends AppCompatActivity {
    private View btnManageRequest;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messages;
    private EditText msgField;
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
        pavsDB.joinMessaging();
        msgField = findViewById(R.id.AMT_edMsgField);
        btnManageRequest = findViewById(R.id.AMT_btnManageRequest);
        ListView inboxListView = findViewById(R.id.AMT_lstInbox);
        messages = pavsDB.teamChat.getChatMessages();
        chatAdapter = new ChatAdapter(this, R.layout.activity_my_team, messages, pavsDB);
        inboxListView.setAdapter(chatAdapter);
        ((TextView) findViewById(R.id.AMT_txtProjectName)).setText(pavsDB.titleCaseConversion(pavsDB.getStudentProject().getProjectName()));
        if (pavsDB.getStudentProject().getGroupMembers().size() > 1) {
            ((TextView) findViewById(R.id.AMT_txtTeamSize)).setText(pavsDB.getStudentProject().getGroupMembers().size() + " members");
        }
        findViewById(R.id.AMT_btnCopyCode).setOnClickListener(v -> Animator.OnClick(v, v1 -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", pavsDB.getStudentProject().getProjectId());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MyTeam.this, "Group Id copied to clipboard", Toast.LENGTH_SHORT).show();
        }));
        WidgetHandler.setListViewHeightBasedOnChildren(inboxListView);
        if (pavsDB.getJoinRequests() != null && pavsDB.getJoinRequests().size() == 0) {
            noJoinRequests();
        } else {
            manageJoinRequests();
        }
        pavsDB.addDataListener(new PavsDB.DataListener() {
            @Override
            public void newRequests() {
                if (pavsDB.getJoinRequests() != null && pavsDB.getJoinRequests().size() == 0) {
                    noJoinRequests();
                } else {
                    manageJoinRequests();
                }
            }

            @Override
            public void teamPresence() {
            }

            @Override
            public void projectStatus() {

            }

            @Override
            public void newMessage() {
                if (chatAdapter != null) {
                    Log.i("TAG", "newMessage: ");
                    if (messages != pavsDB.teamChat.getChatMessages()) {
                        messages = pavsDB.teamChat.getChatMessages();
                        chatAdapter = new ChatAdapter(MyTeam.this, R.layout.activity_my_team, messages, pavsDB);
                        inboxListView.setAdapter(chatAdapter);
                        WidgetHandler.setListViewHeightBasedOnChildren(inboxListView);
                    }

                }
            }
        });
        btnManageRequest.setOnClickListener(v -> Animator.OnClick(v, v12 -> {
            overridePendingTransition(0, 0);
            startActivity(new Intent(MyTeam.this, JoinRequests.class));
        }));
        findViewById(R.id.AMT_btnSend).setOnClickListener(v -> {
            if (msgField.getText().length() != 0) {
                Animator.OnClick(v, v1 -> sendTextMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        initialize();
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

    private void sendTextMessage() {
        hideKeyboard();
        Message message = new Message(msgField.getText().toString());
        message.setSenderId(pavsDB.getAppUser().getStudentsId());
        message.setMessageType(Dictionary.TEXT_MESSAGE);
        msgField.setText("");
        pavsDB.sendMessage(message);
    }

    private void manageJoinRequests() {
        if (btnManageRequest.getVisibility() != View.VISIBLE) {
            btnManageRequest.setVisibility(View.VISIBLE);
        }
    }

    private void noJoinRequests() {
        if (btnManageRequest.getVisibility() == View.VISIBLE) {
            btnManageRequest.setVisibility(View.GONE);
        }
    }
}
