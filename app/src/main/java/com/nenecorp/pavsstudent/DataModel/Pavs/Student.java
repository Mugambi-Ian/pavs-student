package com.nenecorp.pavsstudent.DataModel.Pavs;

public class Student {
    private String userName;
    private String studentsId;
    private String admissionNumber;
    private String phoneNumber;
    private String projectType;
    private String projectId;

    private String teamRequest;

    public String getTeamRequest() {
        return teamRequest;
    }

    public void setTeamRequest(String teamRequest) {
        this.teamRequest = teamRequest;
    }

    public String getProjectId() {
        return projectId;
    }

    public Student setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public Student(String studentsId) {
        this.studentsId = studentsId;
    }

    public Student setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Student setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
        return this;
    }

    public Student setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Student setProjectType(String projectType) {
        this.projectType = projectType;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public String getStudentsId() {
        return studentsId;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProjectType() {
        return projectType;
    }
}
