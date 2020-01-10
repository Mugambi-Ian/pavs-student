package com.nenecorp.pavsstudent.DataModel;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nenecorp.pavsstudent.DataModel.Chat.Chat;
import com.nenecorp.pavsstudent.DataModel.Chat.Message;
import com.nenecorp.pavsstudent.DataModel.NcModels.IdString;
import com.nenecorp.pavsstudent.DataModel.Pavs.Project;
import com.nenecorp.pavsstudent.DataModel.Pavs.Student;
import com.nenecorp.pavsstudent.Utility.Resources.Cache;
import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PavsDB {
    public Chat teamChat;
    private Student appUser;
    private Project studentProject;
    private DataSnapshot pavsDatabase;
    private ArrayList<IdString> requestIds;
    private ArrayList<IdString> memberIds;
    private ArrayList<Student> joinRequests;
    private ArrayList<Student> teamMembers;
    private ArrayList<DataListener> dataListeners;

    public interface DatabaseInterface {
        void onLoaded(PavsDB pavsDB);
    }

    public PavsDB(DatabaseInterface databaseInterface) {
        init();
        final Handler handler = new Handler();
        final int delay = 5;
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isLoaded()) {
                    databaseInterface.onLoaded(PavsDB.this);
                } else {
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    private void init() {
        dataListeners = new ArrayList<>();
        teamChat = new Chat(new ArrayList<>());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pavsDatabase = dataSnapshot;
                appUser = null;
                studentProject = null;
                getAppUser();
                if (teamChat != null) {
                    for (DataListener listener : dataListeners) {
                        listener.newMessage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FirebaseDatabase.getInstance().getReference().addValueEventListener(eventListener);
    }

    public void rejectJoinRequest(Student reqStudent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Dictionary.STUDENT_RECORDS).child(reqStudent.getStudentsId());
        reference.child(Dictionary.projectId).setValue(null);
        reference.child(Dictionary.teamRequest).setValue(Dictionary.TEAM_DENIED);
        DatabaseReference p = FirebaseDatabase.getInstance().getReference().child(Dictionary.TEAM_PROJECT);
        p = p.child(getProjectYear(reqStudent.getProjectId()));
        p = p.child(reqStudent.getProjectId());
        p = p.child(Dictionary.joinRequests).child(reqStudent.getStudentsId());
        p.setValue(null);
    }


    public void addDataListener(DataListener dataListener) {
        dataListeners.add(dataListener);
    }

    public void approveJoinRequest(Student reqStudent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Dictionary.STUDENT_RECORDS).child(reqStudent.getStudentsId());
        reference.child(Dictionary.teamRequest).setValue(null);
        DatabaseReference p = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(Dictionary.TEAM_PROJECT);
        p = p.child(getProjectYear(reqStudent.getProjectId()));
        p = p.child(reqStudent.getProjectId());
        p.child(Dictionary.joinRequests).child(reqStudent.getStudentsId()).setValue(null);
        p.child(Dictionary.groupMembers).child(reqStudent.getStudentsId()).setValue(reqStudent.getStudentsId());
    }

    public void completeProject(String pdfUri, String zipUri) {
        String projectId = appUser.getProjectId();
        DatabaseReference project = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        project.child(Dictionary.projectStatus).setValue(Dictionary.COMPLETED);
        project.child("pdfUri").setValue(pdfUri);
        project.child("zipUri").setValue(zipUri);
        for (DataListener listener : dataListeners) {
            listener.projectStatus();
        }
    }

    public void leaveProject() {
        String projectId = appUser.getProjectId();
        DatabaseReference project = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        if (studentProject.getProjectType() == Dictionary.TEAM_PROJECT) {
            project.child(Dictionary.groupMembers).child(appUser.getStudentsId()).setValue(null);
        }
        DatabaseReference userSnapshot = FirebaseDatabase.getInstance().getReference().child(Dictionary.STUDENT_RECORDS);
        userSnapshot.child(appUser.getStudentsId()).child(Dictionary.projectId).setValue(null);
        userSnapshot.child(appUser.getStudentsId()).child(Dictionary.projectType).setValue(null);

    }


    public interface DataListener {
        void newRequests();

        void teamPresence();

        void projectStatus();

        void newMessage();
    }


    public ArrayList<Student> getJoinRequests() {
        if (joinRequests == null) {
            joinRequests = new ArrayList<>();
            for (IdString id : requestIds) {
                joinRequests.add(getStudent(id.getStringContent()));
            }
            for (DataListener d : dataListeners) {
                d.newRequests();
            }
        }
        return joinRequests;
    }

    private ArrayList<Student> getGroupMembers() {
        if (teamMembers == null) {
            teamMembers = new ArrayList<>();
            for (IdString id : memberIds) {
                teamMembers.add(getStudent(id.getStringContent()));
            }
            studentProject.setGroupMembers(getIdStringContent(memberIds));
        }
        return teamMembers;
    }

    public Student getStudent(String studentId) {
        DataSnapshot userSnapshot = pavsDatabase.child(Dictionary.STUDENT_RECORDS);
        Student x = null;
        if (userSnapshot.hasChild(studentId)) {
            userSnapshot = userSnapshot.child(studentId);
            String admissionNumber = userSnapshot.child(Dictionary.admissionNumber).getValue(String.class);
            String phoneNumber = userSnapshot.child(Dictionary.phoneNumber).getValue(String.class);
            String userName = userSnapshot.child(Dictionary.userName).getValue(String.class);
            String projectType = userSnapshot.child(Dictionary.projectType).getValue(String.class);
            String projectId = userSnapshot.child(Dictionary.projectId).getValue(String.class);
            x = new Student(studentId).setAdmissionNumber(admissionNumber)
                    .setUserName(userName)
                    .setPhoneNumber(phoneNumber)
                    .setProjectType(projectType)
                    .setProjectId(projectId);
        }
        return x;
    }


    public Student getAppUser() {
        String uId = FirebaseAuth.getInstance().getUid();
        if (appUser == null && pavsDatabase != null & uId != null) {
            DataSnapshot userSnapshot = pavsDatabase.child(Dictionary.STUDENT_RECORDS);
            Student x;
            if (userSnapshot.hasChild(uId)) {
                userSnapshot = userSnapshot.child(uId);
                String admissionNumber = userSnapshot.child(Dictionary.admissionNumber).getValue(String.class);
                String phoneNumber = userSnapshot.child(Dictionary.phoneNumber).getValue(String.class);
                String userName = userSnapshot.child(Dictionary.userName).getValue(String.class);
                String projectType = userSnapshot.child(Dictionary.projectType).getValue(String.class);
                String projectId = userSnapshot.child(Dictionary.projectId).getValue(String.class);
                String teamRequest = userSnapshot.child(Dictionary.teamRequest).getValue(String.class);
                x = new Student(uId).setAdmissionNumber(admissionNumber)
                        .setUserName(userName)
                        .setPhoneNumber(phoneNumber)
                        .setProjectType(projectType)
                        .setProjectId(projectId);
                x.setTeamRequest(teamRequest);
                appUser = x;
                if (projectId != null && projectType != null) {
                    studentProject = null;
                    getStudentProject();
                }
            }
        }

        if (appUser != null) {
            String projectId = appUser.getProjectId();
            boolean c = projectId != null;
            c = c && !projectId.equals("");
            c = c && studentProject != null;
            c = c && studentProject.getProjectType().equals(Dictionary.TEAM_PROJECT);
            if (c) {
                memberIds = null;
                teamMembers = null;
                requestIds = null;
                joinRequests = null;
                teamChat = getTeamChat();
                requestIds = getRequestIds();
                memberIds = getTeamMembersId();
                teamMembers = getGroupMembers();
                joinRequests = getJoinRequests();
                studentProject.setGroupMembers(getIdStringContent(memberIds));
            }
        }
        return appUser;
    }

    private ArrayList<IdString> getTeamMembersId() {
        String projectId = studentProject.getProjectId();
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        return getStringList(projectSnapshot.child(Dictionary.groupMembers));
    }

    private ArrayList<IdString> getRequestIds() {
        String projectId = appUser.getProjectId();
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        ArrayList<IdString> joinRequest = getStringList(projectSnapshot.child(Dictionary.joinRequests));
        studentProject.setJoinRequests(getIdStringContent(joinRequest));
        return joinRequest;
    }

    private ArrayList<String> getIdStringContent(ArrayList<IdString> idStrings) {
        ArrayList<String> strings = new ArrayList<>();
        for (IdString idString : idStrings) {
            strings.add(idString.getStringContent());
        }
        return strings;
    }

    public Project getStudentProject() {
        if (appUser != null) {
            String projectId = appUser.getProjectId();
            if (studentProject == null && projectId != null && !projectId.equals("")) {
                DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
                String projectName = projectSnapshot.child(Dictionary.projectName).getValue(String.class);
                String projectType = projectSnapshot.child(Dictionary.projectType).getValue(String.class);
                String projectDescription = projectSnapshot.child(Dictionary.projectDescription).getValue(String.class);
                String projectStatus = projectSnapshot.child(Dictionary.projectStatus).getValue(String.class);
                Project x = new Project(projectId).setProjectName(projectName)
                        .setProjectType(projectType)
                        .setProjectDescription(projectDescription)
                        .setProjectStatus(projectStatus);
                if (projectType.equals(Dictionary.TEAM_PROJECT)) {
                    ArrayList<IdString> members = getStringList(projectSnapshot.child(Dictionary.groupMembers));
                    ArrayList<IdString> joinRequest = getStringList(projectSnapshot.child(Dictionary.joinRequests));
                    Integer teamSize = projectSnapshot.child(Dictionary.teamSize).getValue(Integer.class);
                    if (teamSize == null) {
                        teamSize = 4;
                    }
                    x.setGroupMembers(getIdStringContent(members));
                    x.setJoinRequests(getIdStringContent(joinRequest));
                    x.setTeamSize(teamSize);

                }

                studentProject = x;
                for (DataListener dataListener : dataListeners) {
                    dataListener.projectStatus();
                }
            }
        }
        return studentProject;
    }


    private ArrayList<IdString> getStringList(DataSnapshot child) {
        ArrayList<IdString> strings = new ArrayList<>();
        for (DataSnapshot snapshot : child.getChildren()) {
            String x = snapshot.getValue(String.class);
            strings.add(new IdString(snapshot.getKey(), x));
        }
        return strings;
    }

    private Chat getTeamChat() {
        ArrayList<Message> messages = new ArrayList<>();
        DataSnapshot chatSnapshot = pavsDatabase.child(Dictionary.CHAT_ROOM).child(appUser.getProjectId());
        for (DataSnapshot message : chatSnapshot.getChildren()) {
            String type = message.child(Dictionary.messageType).getValue(String.class);
            String content = message.child(Dictionary.messageContent).getValue(String.class);
            String id = message.child(Dictionary.messageId).getValue(String.class);
            String senderId = message.child(Dictionary.senderId).getValue(String.class);
            Message x = new Message(content)
                    .setMessageId(id)
                    .setMessageType(type)
                    .setSenderId(senderId);
            switch (type) {
                case Dictionary.PHOTO_MESSAGE:
                    ArrayList<IdString> photoUrl = getStringList(message.child(Dictionary.photoUrl));
                    x.setPhotoUrl(getIdStringContent(photoUrl));
                    break;
                case Dictionary.FILE_MESSAGE:
                    String url = message.child(Dictionary.fileUrl).getValue(String.class);
                    x.setFileUrl(url);
                    break;
            }
            messages.add(x);

        }
        return new Chat(messages);
    }

    public String getProjectYear(String id) {
        String i[] = id.split("-");
        return i[1];
    }

    public String getProjectId() {
        String id;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child("" + year + "RN");
        if (appUser.getProjectType().equals(Dictionary.TEAM_PROJECT)) {
            id = "TP";
        } else {
            id = "SP";
        }
        Random r = new Random();
        String alphabet = "123xyz";
        String suffix = "";
        for (int i = 0; i < 3; i++) {
            suffix = suffix + alphabet.charAt(r.nextInt(alphabet.length()));
        }
        id = id + "-" + year + "RN" + "-" + projectSnapshot.getChildrenCount() + "-" + suffix.toUpperCase();
        return id;
    }

    public boolean selectedProjectType() {
        return appUser.getProjectType() != null && !appUser.getProjectType().equals("");
    }

    public boolean teamProject() {
        return appUser.getProjectType().equals(Dictionary.TEAM_PROJECT);
    }

    public void saveUser(Student student) {
        student.setUserName(titleCaseConversion(student.getUserName()));
        student.setAdmissionNumber(student.getAdmissionNumber().toUpperCase());
        FirebaseDatabase.getInstance().getReference().child(Dictionary.STUDENT_RECORDS).child(student.getStudentsId()).setValue(student);
        appUser = student;
    }

    public String titleCaseConversion(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }
        return converted.toString();
    }

    public boolean isLoaded() {
        return pavsDatabase != null;
    }

    public void saveProject(Project project) {
        project.setProjectName(titleCaseConversion(project.getProjectName()));
        String projectId = project.getProjectId();
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(project.getProjectType()).child(getProjectYear(projectId));
        while (projectSnapshot.hasChild(project.getProjectId())) {
            project.setProjectId(getProjectId());
        }
        project.setProjectStatus(Dictionary.REQUESTING);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(project.getProjectType());
        ref = ref.child(getProjectYear(project.getProjectId()));
        ref.child(project.getProjectId()).setValue(project);
        ref.child(project.getProjectId()).child(Dictionary.projectStatus).setValue(Dictionary.REQUESTING);
        if (project.getProjectType().equals(Dictionary.TEAM_PROJECT)) {
            ref.child(project.getProjectId()).child(Dictionary.groupMembers).child(appUser.getStudentsId()).setValue(appUser.getStudentsId());
        }
        appUser.setProjectId(projectId);
        saveUser(appUser);
    }

    public void joinMessaging() {
        FirebaseMessaging.getInstance().subscribeToTopic(studentProject.getProjectId());
    }

    public void sendMessage(Message message) {
        teamChat.newMessage(message);
        DatabaseReference msgReference = FirebaseDatabase.getInstance().getReference().child(Dictionary.CHAT_ROOM);
        msgReference = msgReference.child(appUser.getProjectId()).push();
        message.setMessageId(msgReference.getKey());
        msgReference.setValue(message);
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", "" + studentProject.getProjectName());
            notification.put("message", "New message from" + appUser.getUserName());
            notification.put("to", studentProject.getProjectId());
            sendNotification(notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RequestQueue requestQueue() {
        return Volley.newRequestQueue(Cache.getHome().getApplicationContext());
    }

    private void sendNotification(JSONObject notification) {
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        ObjectRequest jsonObjectRequest = new ObjectRequest(FCM_API, notification, response -> {

        }, error -> {

        });
        requestQueue().add(jsonObjectRequest);
    }

    public boolean joinProject(String projectId) {
        return pavsDatabase.child(Dictionary.PROJECTS).child(Dictionary.TEAM_PROJECT).child(getProjectYear(projectId)).hasChild(projectId);
    }

    public void createJoinRequest(String projectId) {
        DatabaseReference projectSnapshot = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        projectSnapshot.child(Dictionary.joinRequests).child(appUser.getStudentsId()).setValue(appUser.getStudentsId());
        appUser.setProjectId(projectId);
        appUser.setTeamRequest(Dictionary.REQUESTING_TEAM);
        saveUser(appUser);
    }

    public Project getProject(String projectId) {
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        Integer teamSize = projectSnapshot.child(Dictionary.teamSize).getValue(Integer.class);
        ArrayList<IdString> members = getStringList(projectSnapshot.child(Dictionary.groupMembers));
        if (teamSize == null) {
            teamSize = 4;
        }
        Project x = new Project(projectId).setTeamSize(teamSize).setGroupMembers(getIdStringContent(members));
        return x;
    }


    private class ObjectRequest extends JsonObjectRequest {
        ObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() {
            HashMap<String, String> params = new HashMap<>();
            String serverKey = Dictionary.firebaseServerKey;
            params.put("Authorization", serverKey);
            String contentType = Dictionary.applicationJson;
            params.put("Content-Type", contentType);
            return params;
        }
    }
}

