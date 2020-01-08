package com.nenecorp.pavsstudent.DataModel;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.ImmutableMap;
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
import com.nenecorp.pavsstudent.Utility.Resources.NCJson;
import com.nenecorp.pavsstudent.Utility.Resources.NCTimeDate;
import com.nenecorp.pavsstudent.Utility.Tools.PresenceManager.PresencePnCallback;
import com.nenecorp.pavsstudent.Utility.Tools.PresenceManager.PresenceRecord;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PavsDatabase {
    private static Chat teamChat;
    private static Student appUser;
    private static Project studentProject;
    private static PubNub mPubNubDataStream;
    private static DataSnapshot pavsDatabase;
    private static ArrayList<IdString> requestIds;
    private static ArrayList<IdString> memberIds;
    private static ArrayList<Student> joinRequests;
    private static ArrayList<Student> teamMembers;
    private static ArrayList<String> subscribedChannels;
    private static PresencePnCallback mPresencePnCallback;
    private static ArrayList<PresenceRecord> presenceRecords;
    private static ArrayList<DataListener> dataListeners;
    private static final String TAG = "PAVSDatabase";

    public PavsDatabase() {
        dataListeners = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pavsDatabase = dataSnapshot;
                appUser = null;
                studentProject = null;
                getAppUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void rejectGroupJoin(Student reqStudent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Dictionary.STUDENT_RECORDS).child(reqStudent.getStudentsId());
        reference.child(Dictionary.projectId).setValue(null);
        reference.child(Dictionary.teamRequest).setValue(Dictionary.GROUP_DENIED);
        DatabaseReference p = FirebaseDatabase.getInstance().getReference().child(Dictionary.TEAM_PROJECT);
        p = p.child(getProjectYear(reqStudent.getProjectId()));
        p = p.child(reqStudent.getProjectId());
        p = p.child(Dictionary.joinRequests).child(getRequestId(reqStudent.getStudentsId()));
        p.setValue(null);
    }


    public static void addDataListener(DataListener dataListener) {
        dataListeners.add(dataListener);
    }

    public interface DataListener {
        void newRequests(ArrayList<Student> students);

        void teamPresence(ArrayList<PresenceRecord> presenceRecords);

        void projectStatus(String projectStatus);

        void newMessage(Chat chat);
    }

    public static ArrayList<PresenceRecord> getPresenceRecords() {
        return presenceRecords;
    }

    public static ArrayList<Student> getJoinRequests() {
        if (joinRequests == null) {
            joinRequests = new ArrayList<>();
            for (IdString id : requestIds) {
                joinRequests.add(getStudent(id.getStringContent()));
            }
            for (DataListener d : dataListeners) {
                d.newRequests(joinRequests);
            }
        }
        return joinRequests;
    }

    public static ArrayList<Student> getGroupMembers() {
        if (teamMembers == null) {
            teamMembers = new ArrayList<>();
            for (IdString id : memberIds) {
                teamMembers.add(getStudent(id.getStringContent()));
            }
            studentProject.setGroupMembers(getIdStringContent(memberIds));
        }
        return joinRequests;
    }

    private static Student getStudent(String studentId) {
        DataSnapshot userSnapshot = pavsDatabase.child(Dictionary.STUDENT_RECORDS);
        Student x = null;
        if (userSnapshot.hasChild(studentId)) {
            userSnapshot = userSnapshot.child(studentId);
            String admissionNumber = userSnapshot.child(Dictionary.admissionNumber).getValue(String.class);
            String phoneNumber = userSnapshot.child(Dictionary.phoneNumber).getValue(String.class);
            String userName = userSnapshot.child(Dictionary.userName).getValue(String.class);
            String projectType = userSnapshot.child(Dictionary.projectType).getValue(String.class);
            String projectId = userSnapshot.child(Dictionary.projectId).getValue(String.class);
            x = new Student(studentId).setAdmissionNumber(getAdmNo(admissionNumber))
                    .setUserName(userName)
                    .setPhoneNumber(phoneNumber)
                    .setProjectType(projectType)
                    .setProjectId(projectId);
        }
        return x;
    }


    private static String setAdmNo(String x) {
        return x.replace("/", "-");
    }

    private static String getAdmNo(String x) {
        return x.replace("-", "/");
    }

    public static Student getAppUser() {
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
                x = new Student(uId).setAdmissionNumber(getAdmNo(admissionNumber))
                        .setUserName(userName)
                        .setPhoneNumber(phoneNumber)
                        .setProjectType(projectType)
                        .setProjectId(projectId);
                appUser = x;
                studentProject = null;
                getStudentProject();
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

    private static ArrayList<IdString> getTeamMembersId() {
        String projectId = studentProject.getProjectId();
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        return getStringList(projectSnapshot.child(Dictionary.groupMembers));
    }

    private static ArrayList<IdString> getRequestIds() {
        String projectId = appUser.getProjectId();
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        ArrayList<IdString> joinRequest = getStringList(projectSnapshot.child(Dictionary.joinRequests));
        studentProject.setJoinRequests(getIdStringContent(joinRequest));
        return joinRequest;
    }

    public static String getRequestId(String studentId) {
        for (IdString idString : requestIds) {
            if (idString.getStringContent().equals(studentId)) {
                return idString.getStringId();
            }
        }
        return "";
    }

    private static ArrayList<String> getIdStringContent(ArrayList<IdString> idStrings) {
        ArrayList<String> strings = new ArrayList<>();
        for (IdString idString : idStrings) {
            strings.add(idString.getStringContent());
        }
        return strings;
    }

    public static Project getStudentProject() {
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
                    if (mPubNubDataStream == null && projectStatus.equals(Dictionary.APPROVED)) {
                        openBroadcast(x);
                    }
                }
                studentProject = x;
            }
        }
        return studentProject;
    }

    public static Project getProject(String projectId, String projectType) {
        if (projectId != null && !projectId.equals("")) {
            DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(projectType).child(getProjectYear(projectId)).child(projectId);
            String projectName = projectSnapshot.child(Dictionary.projectName).getValue(String.class);
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
                x.setGroupMembers(getIdStringContent(members));
                x.setJoinRequests(getIdStringContent(joinRequest));
                x.setTeamSize(teamSize);
            }
            return x;
        }
        return null;
    }

    private static void openBroadcast(Project x) {
        presenceRecords = new ArrayList<>();
        mPresencePnCallback = new PresencePnCallback();
        PNConfiguration pnConfig = new PNConfiguration()
                .setPublishKey(Dictionary.PUBNUB_PUBLISH_KEY)
                .setSubscribeKey(Dictionary.PUBNUB_SUBSCRIBE_KEY)
                .setUuid(appUser.getStudentsId()).setSecure(true);
        mPubNubDataStream = new PubNub(pnConfig);
        mPubNubDataStream.addListener(mPresencePnCallback);
        subscribedChannels = (ArrayList<String>) Collections.singletonList(x.getProjectId());
        mPubNubDataStream.subscribe().channels(subscribedChannels).withPresence().execute();
        mPubNubDataStream.hereNow().channels(subscribedChannels).includeState(true).async(new PNCallback<PNHereNowResult>() {
            @Override
            public void onResponse(PNHereNowResult result, PNStatus status) {
                if (status.isError()) {
                    return;
                }
                for (DataListener dataListener : dataListeners) {
                    dataListener.teamPresence(mPresencePnCallback.getRecords());
                }
                presenceRecords = mPresencePnCallback.getRecords();

            }
        });
        pnConfig.setPresenceTimeoutWithCustomInterval(60, 10);
        final Map<String, String> message = ImmutableMap.of(
                "sender", appUser.getStudentsId(),
                "message", Dictionary.ONLINE,
                "timestamp", NCTimeDate.getTimeStampUtc());
        mPubNubDataStream.publish().channel(x.getProjectId()).message(message).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                try {
                    if (!status.isError()) {
                        Log.v(TAG, "publish(" + NCJson.asJson(result) + ")");
                    } else {
                        Log.v(TAG, "publishErr(" + NCJson.asJson(status) + ")");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static ArrayList<IdString> getStringList(DataSnapshot child) {
        ArrayList<IdString> strings = new ArrayList<>();
        for (DataSnapshot snapshot : child.getChildren()) {
            String x = snapshot.getValue(String.class);
            strings.add(new IdString(snapshot.getKey(), x));
        }
        return strings;
    }

    private static Chat getTeamChat() {
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
        if (teamChat.getChatMessages().size() != messages.size()) {
            for (DataListener dataListener : dataListeners) {
                dataListener.newMessage(new Chat(messages));
            }
        }
        return new Chat(messages);
    }

    private static String getProjectYear(String id) {
        String i[] = id.split("-");
        return i[1];
    }

    public static String getProjectId() {
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

    public static boolean selectedProjectType() {
        return appUser.getProjectType() != null && !appUser.getProjectType().equals("");
    }

    public static boolean teamProject() {
        return appUser.getProjectType().equals(Dictionary.TEAM_PROJECT);
    }

    public static void saveUser(Student student) {
        student.setUserName(titleCaseConversion(student.getUserName()));
        student.setAdmissionNumber(setAdmNo(student.getAdmissionNumber()).toUpperCase());
        FirebaseDatabase.getInstance().getReference().child(Dictionary.STUDENT_RECORDS).child(student.getStudentsId()).setValue(student);
        appUser = student;
    }

    private static String titleCaseConversion(String text) {
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

    public static boolean isLoaded() {
        return pavsDatabase != null;
    }

    public static void saveProject(Project project) {
        project.setProjectName(titleCaseConversion(project.getProjectName()));
        String projectId = project.getProjectId();
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(project.getProjectType()).child(getProjectYear(projectId));
        while (projectSnapshot.hasChild(project.getProjectId())) {
            project.setProjectId(getProjectId());
        }
        project.setProjectStatus(Dictionary.WAITING);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(project.getProjectType());
        ref = ref.child(getProjectYear(project.getProjectId()));
        ref.child(project.getProjectId()).setValue(project);
        ref.child(project.getProjectId()).child(Dictionary.projectStatus).setValue(Dictionary.WAITING);
        appUser.setProjectId(projectId);
        saveUser(appUser);
        if (project.getProjectType().equals(Dictionary.TEAM_PROJECT)) {
            PNConfiguration config = new PNConfiguration();
            config.setPublishKey(Dictionary.PUBNUB_PUBLISH_KEY);
            config.setSubscribeKey(Dictionary.PUBNUB_SUBSCRIBE_KEY);
            config.setUuid(project.getProjectId());
            config.setSecure(true);
            mPubNubDataStream = new PubNub(config);
        }
    }

    public static void joinMessaging() {
        FirebaseMessaging.getInstance().subscribeToTopic(studentProject.getProjectId());
    }

    public static void sendMessage(Message message) {
        teamChat.newMessage(message);
        DatabaseReference msgReference = FirebaseDatabase.getInstance().getReference().child(Dictionary.CHAT_ROOM);
        msgReference = msgReference.child(appUser.getProjectId()).push();
        message.setMessageId(msgReference.getKey());
        msgReference.setValue(message);
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", "Team Post: " + studentProject.getProjectName());
            notification.put("message", "New message from" + appUser.getUserName());
            notification.put("to", studentProject.getProjectId());
            sendNotification(notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static RequestQueue requestQueue() {
        return Volley.newRequestQueue(Cache.getHome().getApplicationContext());
    }

    private static void sendNotification(JSONObject notification) {
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        ObjectRequest jsonObjectRequest = new ObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue().add(jsonObjectRequest);
    }

    public static boolean joinProject(String projectId) {
        return pavsDatabase.child(Dictionary.PROJECTS).child(Dictionary.TEAM_PROJECT).child(getProjectYear(projectId)).hasChild(projectId);
    }

    public static void createJoinRequest(String projectId) {
        DatabaseReference projectSnapshot = FirebaseDatabase.getInstance().getReference().child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        projectSnapshot.child(Dictionary.joinRequests).push().setValue(appUser.getStudentsId());
        appUser.setProjectId(projectId);
        appUser.setTeamRequest(Dictionary.GROUP_WAITING);
        saveUser(appUser);
    }

    public static Project getProject(String projectId) {
        DataSnapshot projectSnapshot = pavsDatabase.child(Dictionary.PROJECTS).child(appUser.getProjectType()).child(getProjectYear(projectId)).child(projectId);
        Integer teamSize = projectSnapshot.child(Dictionary.teamSize).getValue(Integer.class);
        ArrayList<IdString> members = getStringList(projectSnapshot.child(Dictionary.groupMembers));
        if (teamSize == null) {
            teamSize = 4;
        }
        Project x = new Project(projectId).setTeamSize(teamSize).setGroupMembers(getIdStringContent(members));
        return x;
    }

    public static void closeBroadcast() {
        if (subscribedChannels != null && mPubNubDataStream != null) {
            mPubNubDataStream.unsubscribe().channels(subscribedChannels).execute();
            mPubNubDataStream.removeListener(mPresencePnCallback);
            mPubNubDataStream.stop();
            mPubNubDataStream = null;
        }
    }

    private static class ObjectRequest extends JsonObjectRequest {
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
