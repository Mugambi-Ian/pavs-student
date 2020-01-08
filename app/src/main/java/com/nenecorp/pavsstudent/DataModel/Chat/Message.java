package com.nenecorp.pavsstudent.DataModel.Chat;

import com.nenecorp.pavsstudent.Utility.Resources.Dictionary;

import java.util.ArrayList;

public class Message {
    private String messageId;
    private String senderId;
    private String messageContent;
    private String messageType;
    private String fileUrl;
    private ArrayList<String> photoUrl;

    public ArrayList<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(ArrayList<String> photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public String getMessageId() {
        return messageId;
    }

    public Message setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public Message setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getMessageContent() {
        return messageContent;
    }


    public String getMessageType() {
        return messageType;
    }

    public Message setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message(String messageContent) {
        this.messageContent = messageContent;
        messageType = Dictionary.TEXT_MESSAGE;
    }
}
