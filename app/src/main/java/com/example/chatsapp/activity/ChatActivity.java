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

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messList;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;

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

        messagesAdapter = new MessagesAdapter(this, messList);
//        binding.rvMessage.setLayoutManager(new LinearLayoutManager(this));
//        binding.rvMessage.setAdapter(messagesAdapter);

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

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
                        Toast.makeText(ChatActivity.this, "Hehehehe", Toast.LENGTH_SHORT).show();

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
                    }
                });
            }
        });

        getSupportActionBar().setTitle(contactName);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

//        database.getReference()
//                .child("Chat")
//                .child(senderRoom)
//                .child("messages")
//                .addValueEventListener(new ValueEventListener() {
//                    @SuppressLint("NotifyDataSetChanged")
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        messList.clear();
//                        for (DataSnapshot ss : snapshot.getChildren()){
////                            Message mess = ss.getValue(Message.class);
////                            mess.setMessageId(ss.getKey());
////                            messList.add(mess);
//                            Toast.makeText(ChatActivity.this, ss.getValue(Message.class).getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                        //messagesAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
