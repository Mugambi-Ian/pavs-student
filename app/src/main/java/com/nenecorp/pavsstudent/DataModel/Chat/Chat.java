package com.nenecorp.pavsstudent.DataModel.Chat;

import java.util.ArrayList;

public class Chat {
    public ArrayList<Message> getChatMessages() {
        return chatMessages;
    }


    public Chat(ArrayList<Message> chatMessages) {
        this.chatMessages = chatMessages;
    }

    private ArrayList<Message> chatMessages;

    public void newMessage(Message message) {
        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }
        if (message != null) {
            chatMessages.add(message);
        }
    }
}
