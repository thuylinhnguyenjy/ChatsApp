package com.example.chatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.activity.ChatActivity;
import com.example.chatsapp.databinding.MessReceiveBinding;
import com.example.chatsapp.databinding.MessSendBinding;
import com.example.chatsapp.model.Message;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messList;
    final int MESS_SENT = 1;
    final int MESS_RECEIVE = 2;
    String receiverRoom, senderRoom;
    FirebaseDatabase database;

    public MessagesAdapter(Context context, ArrayList<Message> messList, String receiverRoom, String senderRoom) {
        this.context = context;
        this.messList = messList;
        this.receiverRoom = receiverRoom;
        this.senderRoom = senderRoom;
    }

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
        database = FirebaseDatabase.getInstance();

        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SentViewHolder.class) {
                SentViewHolder sentViewHolder = (SentViewHolder) holder;
                sentViewHolder.binding.emotion.setImageResource(reactions[pos]);
                sentViewHolder.binding.emotion.setVisibility(View.VISIBLE);
            } else {
                ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                receiveViewHolder.binding.emotion1.setImageResource(reactions[pos]);
                receiveViewHolder.binding.emotion1.setVisibility(View.VISIBLE);
            }

            message.setEmotion(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("Chat")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("Chat")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if (holder.getClass() == SentViewHolder.class){
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.binding.messSent.setText(message.getMessage());

            if (message.getEmotion() >= 0) {
                sentViewHolder.binding.emotion.setImageResource(reactions[message.getEmotion()]);
                sentViewHolder.binding.emotion.setVisibility(View.VISIBLE);
            } else {
                sentViewHolder.binding.emotion.setVisibility(View.GONE);
            }

            sentViewHolder.binding.messSent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

        } else {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.binding.messReceive.setText(message.getMessage());

            if (message.getEmotion() >= 0) {
                receiveViewHolder.binding.emotion1.setImageResource(reactions[message.getEmotion()]);
                receiveViewHolder.binding.emotion1.setVisibility(View.VISIBLE);
            } else {
                receiveViewHolder.binding.emotion1.setVisibility(View.GONE);
            }

            receiveViewHolder.binding.messReceive.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
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
