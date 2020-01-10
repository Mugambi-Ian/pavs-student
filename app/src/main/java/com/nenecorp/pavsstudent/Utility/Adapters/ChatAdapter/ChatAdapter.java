package com.nenecorp.pavsstudent.Utility.Adapters.ChatAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nenecorp.pavsstudent.DataModel.Chat.Message;
import com.nenecorp.pavsstudent.DataModel.PavsDB;
import com.nenecorp.pavsstudent.R;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<Message> {
    private PavsDB pavsDB;

    public ChatAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Message> objects, PavsDB pavsDB) {
        super(context, resource, objects);
        this.pavsDB = pavsDB;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View parentView = convertView;
        Message message = getItem(position);
        if (parentView == null) {
            if (message.getSenderId().equals(pavsDB.getAppUser().getStudentsId())) {
                parentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_sent, parent, false);
            } else {
                parentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_recieved, parent, false);
            }
        }
        ((TextView) parentView.findViewById(R.id.LIM_txtName)).setText(pavsDB.getStudent(message.getSenderId()).getUserName());
        ((TextView) parentView.findViewById(R.id.LIM_txtMessage)).setText(message.getMessageContent());
        return parentView;
    }


}
