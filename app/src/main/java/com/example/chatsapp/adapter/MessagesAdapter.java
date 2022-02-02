package com.example.chatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.ChatActivity;
import com.example.chatsapp.databinding.MessReceiveBinding;
import com.example.chatsapp.databinding.MessSendBinding;
import com.example.chatsapp.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messList;
    final int MESS_SENT = 1;
    final int MESS_RECEIVE = 2;
    //FirebaseRemoteConfig remoteConfig;

    public MessagesAdapter(Context context, ArrayList<Message> messList) {
        this.context = context;
        this.messList = messList;
    }

    //    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
//        //remoteConfig = FirebaseRemoteConfig.getInstance();
//        this.context = context;
//        this.messList = messages;
//        this.senderRoom = senderRoom;
//        this.receiverRoom = receiverRoom;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESS_SENT) {
            // Inflate the custom layout
            View view = inflater.inflate(R.layout.mess_send, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.mess_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
        // Return a new holder instance
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messList.get(position);
        if (holder.getClass() == SentViewHolder.class){
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.binding.messSent.setText(message.getMessage());
        } else {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.binding.messReceive.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message mess = messList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(mess.getSenderId()))
        return MESS_SENT;
        else return MESS_RECEIVE;
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        MessSendBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessSendBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {

        MessReceiveBinding binding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessReceiveBinding.bind(itemView);
        }
    }
}
