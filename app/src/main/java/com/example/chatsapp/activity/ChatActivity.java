package com.example.chatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatsapp.adapter.MessagesAdapter;
import com.example.chatsapp.databinding.ActivityChatBinding;
import com.example.chatsapp.databinding.ActivitySetupProfileBinding;
import com.example.chatsapp.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messList;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        String contactName = getIntent().getStringExtra("name");
        String receiverId = getIntent().getStringExtra("uid");
        String senderId = FirebaseAuth.getInstance().getUid();

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        messagesAdapter = new MessagesAdapter(this, messList, senderRoom, receiverRoom);

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomKey = database.getReference().push().getKey();
                String messText = binding.edittextMessage.getText().toString();
                Date date = new Date();
                Message mess = new Message(messText, senderId, date.getTime());
                binding.edittextMessage.setText("");

                database.getReference()
                        .child("Chat")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        database.getReference()
                                .child("Chat")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                        HashMap<String, Object> lastMessObject = new HashMap<>();
                        lastMessObject.put("lastMess", mess.getMessage());
                        lastMessObject.put("lastMessTime", mess.getTimestamp());
                        database.getReference().child("Chat").child(senderRoom).updateChildren(lastMessObject);
                        database.getReference().child("Chat").child(receiverRoom).updateChildren(lastMessObject);
                    }
                });
            }
        });

        getSupportActionBar().setTitle(contactName);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        database.getReference()
                .child("Chat")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messList.clear();
                        for (DataSnapshot ss : snapshot.getChildren()){
                            Message mess = ss.getValue(Message.class);
                            mess.setMessageId(ss.getKey());
                            messList.add(mess);
                        }
                            messagesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.rvMessage.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessage.setAdapter(messagesAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
